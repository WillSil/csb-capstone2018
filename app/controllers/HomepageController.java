package controllers;

import models.ControllerComponent;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class HomepageController extends BaseController {

    @Inject
    public HomepageController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    public CompletionStage<Result> getDailyDigestForAdmin() {
        return wrapInFuture(TODO);
    }

}
