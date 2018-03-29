package controllers;

import models.ControllerComponent;
import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class MainController extends BaseController {

    private final Assets assets;

    @Inject
    public MainController(ControllerComponent controllerComponent, Assets assets) {
        super(controllerComponent);
        this.assets = assets;
    }

    public Result options(String unused) {
        return status(NO_CONTENT)
                .withHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT")
                .withHeader("Access-Control-Max-Age", "3600")
                .withHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization")
                .withHeader("Access-Control-Allow-Credentials", "true");
    }

    public CompletionStage<Result> index() {
        return wrapInFuture(redirect(routes.MainController.docs()));
    }

    public Action<AnyContent> getResource(Assets.Asset file) {
        return assets.versioned("/public", file);
    }

    public Action<AnyContent> docs() {
        return assets.versioned("/public", new Assets.Asset("/swagger-ui/index.html"));
    }

}
