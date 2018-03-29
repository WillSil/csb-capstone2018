package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import actions.AdminAuthenticator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import models.ebean.CsbBaseModel;
import models.ControllerComponent;
import models.ebean.business.Client;
import models.ebean.business.query.QBusiness;
import models.ebean.business.query.QClient;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security;
import repositories.ClientRepository;
import utilities.ApiMessages;
import utilities.ParameterConstants;
import utilities.ResultUtility;
import utilities.Validation;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static utilities.ParameterConstants.CLIENT_EMAIL;
import static utilities.ParameterConstants.CLIENT_NAME;
import static utilities.ParameterConstants.ENGAGEMENT_ID;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@Api(value = "Clients")
@Security.Authenticated(AdminAuthenticator.class)
public class ClientController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(EngagementController.class);

    private final ClientRepository clientRepository;

    private static final String KEY_CREATE_CLIENT_VALUE = "Creates a new client";
    private static final String KEY_EDIT_CLIENT_VALUE = "Edits an already existing client";
    private static final String KEY_DELETE_CLIENT_VALUE = "Deletes a client permanently from the database";


    @Inject
    public ClientController(ControllerComponent controllerComponent, ClientRepository clientRepository) {
        super(controllerComponent);
        this.clientRepository = clientRepository;
    }

    @ApiOperation(value = "Gets all of a business\'s clients", response = Client.class)
    public CompletionStage<Result> getClientsForBusiness(int businessId) {
        return clientRepository.getAllClientsForBusiness(businessId, 0, true)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }

    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.CLIENT_NAME),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.CLIENT_EMAIL)
    })
    @ApiOperation(value = KEY_CREATE_CLIENT_VALUE, httpMethod = KEY_POST, response = Client.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createClient(int businessId) {

        JsonNode jsonNode = request().body().asJson();

        if (businessId == -1) {
            logger.info("Invalid business ID, found {}", businessId);
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_ID, ApiMessages.Reason.BAD_REQUEST)
            ));
        }

        String clientName = Validation.string(ParameterConstants.CLIENT_NAME, jsonNode, true);
        if (Validation.isEmpty(clientName)) {
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(CLIENT_NAME)));
        }


        String clientEmail = Validation.string(ParameterConstants.CLIENT_EMAIL, jsonNode, true);
        if (Validation.isEmpty(clientEmail)) {
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(CLIENT_EMAIL)));
        }

        if (!Validation.isEmailValid(clientEmail)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_CLIENT_EMAIL, ApiMessages.Reason.BAD_REQUEST)
            ));
        }

        QClient qClient = QClient.alias();

        String clientPhone = getStringFromJson(qClient.clientPhone.toString(), jsonNode, true);
        String clientNotes = getStringFromJson(qClient.clientNotes.toString(), jsonNode, true);


        return clientRepository.createClient(businessId, clientName, clientEmail, clientPhone, clientNotes)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }



    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.CLIENT_NAME),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.CLIENT_EMAIL),
    })
    @ApiOperation(value = KEY_EDIT_CLIENT_VALUE, httpMethod = KEY_POST, response = Client.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateClient(int businessId, int clientId) {

        JsonNode jsonNode = request().body().asJson();

        if (businessId == -1) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_ID, ApiMessages.Reason.BAD_REQUEST)
            ));
        }

        String clientName = Validation.string(ParameterConstants.CLIENT_NAME, jsonNode, true);
        String clientEmail = Validation.string(ParameterConstants.CLIENT_EMAIL, jsonNode, true);


        if (!Validation.isEmpty(clientEmail) && !Validation.isEmailValid(clientEmail)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_CLIENT_EMAIL, ApiMessages.Reason.BAD_REQUEST)
            ));
        }
        QClient qClient = QClient.alias();

        String clientPhone = getStringFromJson(qClient.clientPhone.toString(), jsonNode, true);
        String clientNotes = getStringFromJson(qClient.clientNotes.toString(), jsonNode, true);

        return clientRepository.updateClient(businessId, clientId, clientName, clientEmail, clientPhone, clientNotes)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());

    }


    @ApiOperation(value = KEY_DELETE_CLIENT_VALUE, httpMethod = KEY_POST, response = Client.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> deleteClient(int businessId, int clientId) {
        CompletionStage<Client> clientCompletionStage = clientRepository.deleteClient(businessId, clientId);
        return transformClientDetailsResponseToResult(clientCompletionStage);
    }

    private CompletionStage<Result> transformClientDetailsResponseToResult(CompletionStage<Client> clientCompletionStage) {
        return clientCompletionStage.thenApplyAsync(client -> {
            if (client != null) {
                return CsbBaseModel.getSerializer(request().queryString()).printWithResult(client);
            } else {
                return badRequest(ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_ID, ApiMessages.Reason.BAD_REQUEST));
            }
        }, getContext());
    }
}

