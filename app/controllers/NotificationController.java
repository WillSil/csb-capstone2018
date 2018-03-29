package controllers;

import models.ControllerComponent;
import play.mvc.BodyParser;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
public class NotificationController extends BaseController {

    @Inject
    public NotificationController(ControllerComponent controllerComponent) {
        super(controllerComponent);
    }

    public CompletionStage<Result> getNotifications() {
        return wrapInFuture(TODO);
    }

    public CompletionStage<Result> getNotificationsCount() {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createNotification() {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateNotification(int notificationId) {
        return wrapInFuture(TODO);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> deleteNotification(int notificationId) {
        return wrapInFuture(TODO);
    }

}
