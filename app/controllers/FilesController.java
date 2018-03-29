package controllers;

import models.ControllerComponent;
import play.mvc.BodyParser;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class FilesController extends BaseController {

    @Inject
    public FilesController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    public CompletionStage<Result> downloadFile(String fileId) {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.MultipartFormData.class)
    public CompletionStage<Result> uploadFile() {
        return wrapInFuture(TODO);
    }

}
