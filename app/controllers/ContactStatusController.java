package controllers;

import actions.AdminAuthenticator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import models.ControllerComponent;
import models.ebean.CsbBaseModel;
import play.mvc.Result;
import play.mvc.Security;
import repositories.ChildStatusRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@Api()
@Security.Authenticated(AdminAuthenticator.class)
public class ContactStatusController extends BaseController {

    private final ChildStatusRepository childStatusRepository;

    @Inject
    public ContactStatusController(ControllerComponent controllerComponent, ChildStatusRepository childStatusRepository) {
        super(controllerComponent);
        this.childStatusRepository = childStatusRepository;
    }

    @ApiOperation(value = "Gets Engagement Phase")
    public CompletionStage<Result> getContactStatuses() {
        return childStatusRepository.getAllChildStatuses()
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }

}
