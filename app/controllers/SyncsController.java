package controllers;

import models.ControllerComponent;
import play.mvc.BodyParser;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class SyncsController extends BaseController {

    @Inject
    public SyncsController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> getEmailSyncs() {
        return wrapInFuture(TODO);
    }

}
