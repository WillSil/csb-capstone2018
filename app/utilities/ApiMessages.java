package utilities;

import models.ebean.user.User;

import java.util.Arrays;

/**
 * Created by Corey Caplan on 10/11/17.
 */
public final class ApiMessages {

    public enum Reason {

        SUCCESS, BAD_REQUEST, NOT_AUTHORIZED, SERVER_ERROR;
    }

    public static final String MESSAGE_MISC_SERVER_ERROR = "A server error occurred. This was the result of a " +
            "developer bug that needs to be fixed. This issue has been logged in the server";

    public static final String MESSAGE_CANNOT_COMMUNICATE_WITH_GOOGLE = "There was an error communicating with Google, " +
            "please try again or submit a bug report.";

    public static final String MESSAGE_BAD_LOGIN = "Could not verify your account\'s Firebase token. Please try again or use a different account.";

    public static final String MESSAGE_NOT_AUTHORIZED = "You are not authorized to access this route. Please login " +
            "or talk to the administrator to gain access to the service";

    public static final String MESSAGE_INVALID_TOKEN = "The token sent to this service by Google was invalid. Please " +
            "refresh the token and try again.";

    public static final String MESSAGE_MISSING_TOKEN = "The token sent to this service was missing. Please resend " +
            "the request with a valid token and consult the API docs.";

    public static final String MESSAGE_MISSING_BUSINESS_NAME = "The business name sent to this service was missing. " +
            "Please resend the request with a valid business name or consult the API docs.";

    public static final String MESSAGE_INVALID_BUSINESS_EMAIL = "The business email sent to this service was invalid. " +
            "Please resend the request with a valid business email or consult the API docs.";

    public static final String MESSAGE_INVALID_ID = "The ID sent to this service was invalid. " +
            "Please resend the request with a valid ID or consult the API docs.";

    public static final String MESSAGE_MISSING_BUSINESS_ID = "The business ID sent to this service was missing. " +
            "Please resend the request with a valid business ID or consult the API docs.";

    public static final String MESSAGE_MISSING_CLIENT_NAME = "The client name sent to this service was missing. " +
            "Please resend the request with a valid business name or consult the API docs.";

    public static final String MESSAGE_INVALID_CLIENT_EMAIL = "The client email sent to this service was invalid. " +
            "Please resend the request with a valid client email or consult the API docs.";

    private ApiMessages() {
    }

}
