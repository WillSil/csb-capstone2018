package actions;

import controllers.BaseController;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utilities.ApiMessages;
import clients.AuthenticatorClient;
import utilities.ResultUtility;

import javax.inject.Inject;

/**
 * Created by Corey Caplan on 10/24/17.
 */
public abstract class BaseAuthenticator extends Security.Authenticator {

    private final AuthenticatorClient authenticatorClient;

    private AuthenticatorClient.AuthenticatorResult result;

    @Inject
    public BaseAuthenticator(AuthenticatorClient authenticatorClient) {
        this.authenticatorClient = authenticatorClient;
    }

    @Override
    public final String getUsername(Http.Context context) {
        Http.Headers headers = context.request().getHeaders();
        result = getResultFromRequestHeaders(headers);

        if(result == AuthenticatorClient.AuthenticatorResult.SUCCESS) {
            String jwt = headers.get(BaseController.AUTHORIZATION).orElse(null);
            authenticatorClient.putUserIntoArgs(context.args, authenticatorClient.getUserFromRequest(jwt));
            return result.toString(); // success
        } else {
            return null; // failure
        }
    }

    @Override
    public final Result onUnauthorized(Http.Context ctx) {
        switch (result) {
            case MISSING_TOKEN:
                return unauthorized(ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_TOKEN, ApiMessages.Reason.BAD_REQUEST));
            case INVALID_TOKEN:
                return unauthorized(ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_TOKEN, ApiMessages.Reason.NOT_AUTHORIZED));
            case NOT_AUTHORIZED:
                return unauthorized(ResultUtility.getFailure(ApiMessages.MESSAGE_NOT_AUTHORIZED, ApiMessages.Reason.NOT_AUTHORIZED));
            case SERVER_ERROR:
                return internalServerError(ResultUtility.getFailure(ApiMessages.MESSAGE_CANNOT_COMMUNICATE_WITH_GOOGLE, ApiMessages.Reason.SERVER_ERROR));
            default:
                throw new RuntimeException("Invalid result, found: " + result);
        }
    }

    AuthenticatorClient getAuthenticatorClient() {
        return authenticatorClient;
    }

    abstract AuthenticatorClient.AuthenticatorResult getResultFromRequestHeaders(Http.Headers headers);

}
