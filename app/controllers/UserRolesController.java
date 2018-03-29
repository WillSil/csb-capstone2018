package controllers;

import models.ControllerComponent;
import play.mvc.BodyParser;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class UserRolesController extends BaseController {

    @Inject
    public UserRolesController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    public CompletionStage<Result> getUserRoles() {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> revokeUserRole() {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addUserRole() {
        return wrapInFuture(TODO);
    }

}
