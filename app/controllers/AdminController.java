package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import io.ebean.Ebean;
import io.swagger.annotations.Api;
import models.ControllerComponent;
import models.ebean.user.Role;
import models.ebean.user.User;
import models.ebean.user.UserRole;
import models.ebean.user.UserRoleModel;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import repositories.UserRepository;
import utilities.ApiMessages;
import utilities.ResultUtility;
import utilities.Validation;

import javax.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created by Corey Caplan on 11/9/17.
 */
public class AdminController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(AdminController.class);

    static final String ROUTE_PASSWORD = "automate_csb_2017_2018_admin_sign_in";

    private static final String ADMIN_PASSWORD = "csb2017CSB";

    private final UserRepository userRepository;

    @Inject
    public AdminController(ControllerComponent controllerComponent, UserRepository userRepository) {
        super(controllerComponent);
        this.userRepository = userRepository;
    }

    /**
     * Attempts to sign the admin in, by redirecting him/her to Google.
     * <p></p>
     * Note, the password, which has been externalized to a private static final constant above, must be sent for this
     * route to work. It should be sent via the authorization header of the HTTP request.
     */
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> signInAdminWithGoogle() {
        Optional<String> authorization = request().header(AUTHORIZATION);
        logger.debug("Authorization: {}", authorization);

        if (!ROUTE_PASSWORD.equals(authorization.orElse(null))) {
            JsonNode result = ResultUtility.getFailure(ApiMessages.MESSAGE_NOT_AUTHORIZED, ApiMessages.Reason.NOT_AUTHORIZED);
            return wrapInFuture(unauthorized(result));
        }

        // Make the field final since it's used in the lambda
        final String email = Validation.string(User.KEY_EMAIL, request().body().asJson(), true);
        if (Validation.isEmpty(email)) {
            return wrapInFuture(badRequest(ResultUtility.getFailureMissingParam(User.KEY_EMAIL)));
        } else if (!email.matches(User.EMAIL_REGEX)) {
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(User.KEY_EMAIL)));
        }

        return CompletableFuture.supplyAsync(() -> {
            Boolean isAdminExist = userRepository.isAdminCreated().toCompletableFuture().join();
            if (isAdminExist == null || isAdminExist) {
                JsonNode node = ResultUtility.getFailure("Admin has already been created", ApiMessages.Reason.BAD_REQUEST);
                return status(409, node);
            }

            return ok();
        }).thenApplyAsync(result -> {
            if (result.status() != OK) {
                return result;
            }

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(true)
                    .setPassword(ADMIN_PASSWORD)
                    .setDisplayName("Administrator")
                    .setDisabled(false);

            try {
                return FirebaseAuth.getInstance()
                        .createUserAsync(request)
                        .get(20, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error("Error: ", e);
                return internalServerError(ResultUtility.getFailure(ApiMessages.MESSAGE_CANNOT_COMMUNICATE_WITH_GOOGLE, ApiMessages.Reason.SERVER_ERROR));
            }
        }).thenApplyAsync(resultOrUserRecord -> {
            if (resultOrUserRecord instanceof Result) {
                return (Result) resultOrUserRecord;
            }

            UserRecord userRecord = (UserRecord) resultOrUserRecord;

            User user = new User();
            user.setName(userRecord.getDisplayName());
            user.setEmail(userRecord.getEmail());
            user.setGoogleAccountId(userRecord.getUid());

            UserRole role = new UserRole();
            role.setActive(true);
            role.setDateAdded(new Date());
            user.setUserRole(role);

            UserRoleModel model = new UserRoleModel();
            model.setRoleType(Role.ADMINISTRATOR.getRawText());
            model.setRoleDescription(Role.ADMINISTRATOR.getUiText());
            model.setRolePriority(Role.ADMINISTRATOR.getPriority());
            role.setUserRoleModel(model);

            Ebean.save(user);

            return ok(ResultUtility.getSuccess());
        });
    }

}
