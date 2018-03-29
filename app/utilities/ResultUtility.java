package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import play.libs.Json;
import play.mvc.Result;
import utilities.ApiMessages.Reason;

/**
 * Created by Corey Caplan on 10/23/17.
 */
public final class ResultUtility {

    @ApiModel(description = "A class used to standardize non-data-model responses from the server.")
    public static class StandardizedResult {

        @ApiModelProperty(value = "True if the operation was successful, false otherwise.")
        private boolean isSuccess;

        @ApiModelProperty(value = "A HTML-friendly message to be displayed in the UI.")
        private String message;

        @ApiModelProperty(value = "A message for the developer that pinpoints the issue")
        private String developerMessage;

        @ApiModelProperty(value = "A reason for the failure, if any, or success if the operation was successful.")
        private Reason reason;

        private StandardizedResult(boolean isSuccess, String message, String developerMessage, Reason reason) {
            this.isSuccess = isSuccess;
            this.message = message;
            this.developerMessage = developerMessage;
            this.reason = reason;
        }

        private StandardizedResult(boolean isSuccess, String message, Reason reason) {
            this(isSuccess, message, null, reason);
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public String getMessage() {
            return message;
        }

        public String getDeveloperMessage() {
            return developerMessage;
        }

        public Reason getReason() {
            return reason;
        }
    }

    private ResultUtility() {
    }


    /**
     * @return A node that can be used for a {@link Result} to represent a successful operation.
     */
    public static JsonNode getSuccess() {
        return Json.toJson(new StandardizedResult(true, "Operation successful", Reason.SUCCESS));
    }

    /**
     * @param uiMessage A message that is friendly for the UI to see (and is HTML safe; IE doesn't contain quotes,
     *                  brackets, etc.).
     * @param reason    A standardized reason why the error occurred.
     * @return A node that can be used for a {@link Result} to represent a failed operation.
     */
    public static JsonNode getFailure(String uiMessage, Reason reason) {
        return Json.toJson(new StandardizedResult(false, uiMessage, reason));
    }

    public static JsonNode getFailureMissingParam(String fieldName) {
        String uiMessage = "There was an error communicating with the server";
        String developerMessage = "The parameter with the name \"" + fieldName + "\" was missing";
        return Json.toJson(new StandardizedResult(false, uiMessage, developerMessage, Reason.BAD_REQUEST));
    }

    public static JsonNode getFailureInvalidParam(String fieldName) {
        String uiMessage = "There was an error communicating with the server";
        String developerMessage = "The parameter with the name \"" + fieldName + "\" was invalid";
        return Json.toJson(new StandardizedResult(false, uiMessage, developerMessage, Reason.BAD_REQUEST));
    }

}
