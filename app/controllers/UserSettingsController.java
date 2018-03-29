package controllers;

import models.ControllerComponent;
import play.mvc.BodyParser;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class UserSettingsController extends BaseController {

    @Inject
    public UserSettingsController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    public CompletionStage<Result> getSettings() {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateSyncFrequency() {
        return wrapInFuture(TODO);
    }

}
