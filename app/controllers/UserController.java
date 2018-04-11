package controllers;

import clients.FirebaseAuthenticationClient;
import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.annotations.*;
import models.ebean.CsbBaseModel;
import models.ControllerComponent;
import models.ebean.user.User;
import models.firebase.AutomateCsbToken;
import play.Logger;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Result;
import repositories.UserRepository;
import utilities.ApiMessages;
import clients.AuthenticatorClient;
import utilities.ResultUtility;
import utilities.Validation;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by Corey Caplan on 5/3/17.
 */

@Api(value = "User")
public class UserController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(UserController.class);
    private final UserRepository userRepository;
    private final FirebaseAuthenticationClient firebaseAuthenticationClient;
    private final AuthenticatorClient authenticatorClient;

    @Inject
    public UserController(ControllerComponent controllerComponent, UserRepository userRepository,
                          FirebaseAuthenticationClient firebaseAuthenticationClient,
                          AuthenticatorClient authenticatorClient) {
        super(controllerComponent);
        this.userRepository = userRepository;
        this.firebaseAuthenticationClient = firebaseAuthenticationClient;
        this.authenticatorClient = authenticatorClient;
    }

    @ApiOperation(value = "Attempts to log the user into their account", response = User.class)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = User.KEY_TOKEN, required = true, value = "The ID token given by Firebase, to be used to log in to the service",
                    dataType = "String", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = ApiMessages.MESSAGE_MISSING_TOKEN, response = ResultUtility.StandardizedResult.class),
            @ApiResponse(code = 401, message = ApiMessages.MESSAGE_INVALID_TOKEN, response = ResultUtility.StandardizedResult.class),
            @ApiResponse(code = 401, message = ApiMessages.MESSAGE_NOT_AUTHORIZED, response = ResultUtility.StandardizedResult.class),
            @ApiResponse(code = 200, message = "Success", response = User.class)
    })
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> login() {
        String idToken = Validation.string(User.KEY_TOKEN, request().body().asJson(), true);
        if (Validation.isEmpty(idToken)) {
            return wrapInFuture(badRequest(ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_TOKEN, ApiMessages.Reason.BAD_REQUEST)));
        }

        return CompletableFuture.supplyAsync(() -> getFirebaseTokenOrResultOnError(idToken))
                .thenApplyAsync(this::getUserFromLoginOrCreationOrResultOnError, getContext())
                .thenApplyAsync(this::getResultFromUserOrResult, getContext());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> logout() {
        return wrapInFuture(TODO);
    }

    private F.Either<AutomateCsbToken, Result> getFirebaseTokenOrResultOnError(String idToken) {
        F.Either<AutomateCsbToken, Result> tokenResultEither;
        try {
            tokenResultEither = F.Either.Left(firebaseAuthenticationClient.verifyIdToken(idToken));
            return tokenResultEither;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("Error: ", e);

            if (e.getCause() instanceof FirebaseAuthException) {
                tokenResultEither = F.Either.Right(
                        unauthorized(ResultUtility.getFailure(
                                ApiMessages.MESSAGE_INVALID_TOKEN,
                                ApiMessages.Reason.NOT_AUTHORIZED)
                        ));
            } else {
                tokenResultEither = F.Either.Right(
                        internalServerError(ResultUtility.getFailure(
                                ApiMessages.MESSAGE_CANNOT_COMMUNICATE_WITH_GOOGLE,
                                ApiMessages.Reason.SERVER_ERROR)
                        )
                );
            }
            return tokenResultEither;
        }
    }

    private F.Either<User, Result> getUserFromLoginOrCreationOrResultOnError(F.Either<AutomateCsbToken, Result> eitherTokenOrResult) {
        F.Either<User, Result> eitherUserOrResult;

        if (eitherTokenOrResult.right.isPresent()) {
            eitherUserOrResult = F.Either.Right(eitherTokenOrResult.right.get());
            return eitherUserOrResult;
        } else if (!eitherTokenOrResult.left.isPresent()) {
            logger.error("Error: ", new IllegalStateException());
            eitherUserOrResult = F.Either.Right(
                    internalServerError(ResultUtility.getFailure(
                            ApiMessages.MESSAGE_MISC_SERVER_ERROR,
                            ApiMessages.Reason.SERVER_ERROR)
                    )
            );
            return eitherUserOrResult;
        }

        AutomateCsbToken token = eitherTokenOrResult.left.get();

        String googleUserId = token.getGoogleUserId();
        String email = token.getEmail();
        String name = token.getName();

        Optional<User> user = Optional.ofNullable(userRepository.loginIfUserExists(googleUserId, email)
                .toCompletableFuture()
                .join()
        );
        if (!user.isPresent()) {
            user = Optional.ofNullable(
                    userRepository.createAccountIfUserIsInvited(googleUserId, email, name)
                            .toCompletableFuture()
                            .join()
            );
        }

        eitherUserOrResult = user.<F.Either<User, Result>>map(F.Either::Left)
                .orElseGet(() -> {
                    logger.info("Could not find user or create one for Google Account ID: {}", googleUserId);
                    return F.Either.Right(unauthorized(ResultUtility.getFailure(
                            ApiMessages.MESSAGE_INVALID_TOKEN,
                            ApiMessages.Reason.NOT_AUTHORIZED)
                    ));
                });

        return eitherUserOrResult;
    }

    private Result getResultFromUserOrResult(F.Either<User, Result> eitherUserOrResult) {
        if (eitherUserOrResult.right.isPresent()) {
            return eitherUserOrResult.right.get();
        } else if (!eitherUserOrResult.left.isPresent()) {
            logger.error("Error: ", new IllegalStateException());
            return internalServerError(ResultUtility.getFailure(
                    ApiMessages.MESSAGE_MISC_SERVER_ERROR,
                    ApiMessages.Reason.SERVER_ERROR)
            );
        }

        User user = eitherUserOrResult.left.get();
        String jwt = authenticatorClient.createJwtForUser(user);
        user.setJwt(jwt);

        return ok(CsbBaseModel.getSerializer(request().queryString()).print(user));
    }

}
