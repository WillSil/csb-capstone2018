package controllers;

import actions.AdminAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.*;
import models.AutomateCsbPagedList;
import models.ControllerComponent;
import models.ebean.CsbBaseModel;
import models.ebean.business.Business;
import models.ebean.business.query.QBusiness;
import models.search.BusinessSearch;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security;
import repositories.BusinessRepository;
import utilities.ApiMessages;
import utilities.ApiMessages.Reason;
import utilities.ParameterConstants;
import utilities.ResultUtility;
import utilities.Validation;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@Api(value = "Businesses")
@Security.Authenticated(AdminAuthenticator.class)
public class BusinessController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(BusinessController.class);

    private final BusinessRepository businessRepository;

    private static final String KEY_GET_BUSINESSES_VALUE = "Get all of the admin\'s businesses";
    private static final String KEY_GET_BUSINESSES_DETAILS_VALUE = "Gets the details for this business, including clients, engagements, files and other misc";
    private static final String KEY_CREATE_BUSINESSES_VALUE = "Creates a new business";
    private static final String KEY_EDIT_BUSINESS_VALUE = "Edits an already existing business";
    private static final String KEY_DELETE_BUSINESS_VALUE = "Deletes a business permanently from the database";

    @Inject
    public BusinessController(ControllerComponent controllerComponent, BusinessRepository businessRepository) {

        super(controllerComponent);
        this.businessRepository = businessRepository;
    }

    public static class BusinessPagedList extends TypeToken<AutomateCsbPagedList<Business>> {
    }

    @ApiOperation(value = KEY_GET_BUSINESSES_VALUE, httpMethod = KEY_GET)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = ParameterConstants.BUSINESS_NAME, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.BUSINESS_EMAIL, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.BUSINESS_INDUSTRY, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.CLIENT_NAME, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.CLIENT_EMAIL, paramType = "body", dataType = "string"),
            @ApiImplicitParam(name = KEY_ASCENDING, paramType = "body", dataType = "boolean", defaultValue = "true", value = ParameterConstants.DESCRIPTION_ASCENDING),
            @ApiImplicitParam(name = KEY_IS_OR_FILTER, paramType = "body", dataType = "boolean", defaultValue = "true", value = ParameterConstants.DESCRIPTION_IS_OR_FILTER),
            @ApiImplicitParam(name = KEY_PAGE, paramType = "body", dataType = "int", defaultValue = "0", value = ParameterConstants.DESCRIPTION_PAGE),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK response", response = Business.class)
    })
    public CompletionStage<Result> getBusinesses() {
        Map<String, String[]> queryParams = request().queryString();

        String businessNameValue = getStringFromQueryParams(ParameterConstants.BUSINESS_NAME, queryParams);
        String businessEmailValue = getStringFromQueryParams(ParameterConstants.BUSINESS_EMAIL, queryParams);
        String businessIndustryValue = getStringFromQueryParams(ParameterConstants.BUSINESS_INDUSTRY, queryParams);
        String clientNameValue = getStringFromQueryParams(ParameterConstants.CLIENT_NAME, queryParams);
        String clientEmailValue = getStringFromQueryParams(ParameterConstants.CLIENT_EMAIL, queryParams);

        boolean isAscending = Validation.bool(KEY_ASCENDING, queryParams, true);
        boolean isOrFilter = Validation.bool(KEY_IS_OR_FILTER, queryParams, true);

        BusinessSearch search = new BusinessSearch(businessNameValue, businessEmailValue, clientNameValue,
                clientEmailValue, businessIndustryValue, isOrFilter, BusinessSearch.SortByPredicate.BUSINESS_NAME,
                isAscending);

        logger.debug("BusinessSearch: {}", Json.prettyPrint(Json.toJson(search)));

        int page = Validation.page(KEY_PAGE, queryParams);

        return businessRepository.getBusinesses(search, page)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }

    @ApiOperation(value = KEY_GET_BUSINESSES_DETAILS_VALUE, httpMethod = KEY_GET, response = Business.class)
    public CompletionStage<Result> getBusinessDetails(int businessId) {

        return businessRepository.getBusinessDetailsById(businessId)
                .thenApplyAsync(business -> {
                    if (business != null) {
                        return CsbBaseModel.getSerializer(request().queryString()).printWithResult(business);
                    } else {
                        return notFound(ResultUtility.getFailureInvalidParam(QBusiness.alias().businessId.toString()));
                    }
                }, getContext());
    }

    @ApiOperation(value = KEY_CREATE_BUSINESSES_VALUE, httpMethod = KEY_POST, response = Business.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createBusiness() {
        JsonNode jsonNode = request().body().asJson();

        QBusiness qBusiness = QBusiness.alias();
        String businessName = getStringFromJson(qBusiness.businessName.toString(), jsonNode, true);
        if (Validation.isEmpty(businessName)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_NAME, Reason.BAD_REQUEST)
            ));
        }

        String businessEmail = getStringFromJson(qBusiness.businessEmail.toString(), jsonNode, true);
        if (!Validation.isEmpty(businessEmail) && !Validation.isEmailValid(businessEmail)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_BUSINESS_EMAIL, Reason.BAD_REQUEST)
            ));
        }

        String businessAddress = getStringFromJson(qBusiness.businessAddress.toString(), jsonNode, true);
        String businessIndustry = getStringFromJson(qBusiness.businessIndustry.toString(), jsonNode, true);
        String businessPhoneNumber = getStringFromJson(qBusiness.businessPhoneNumber.toString(), jsonNode, true);

        return businessRepository.createBusiness(businessName, businessEmail, businessPhoneNumber, businessAddress, businessIndustry)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }

    @ApiOperation(value = KEY_EDIT_BUSINESS_VALUE, httpMethod = KEY_POST, response = Business.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> editBusiness(int businessId) {
        JsonNode jsonNode = request().body().asJson();

        QBusiness qBusiness = QBusiness.alias();
        String businessName = getStringFromJson(qBusiness.businessName.toString(), jsonNode, true);
        if (Validation.isEmpty(businessName)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_NAME, Reason.BAD_REQUEST)
            ));
        }

        String businessEmail = getStringFromJson(qBusiness.businessEmail.toString(), jsonNode, true);
        if (!Validation.isEmpty(businessEmail) && !Validation.isEmailValid(businessEmail)) {
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_BUSINESS_EMAIL, Reason.BAD_REQUEST)
            ));
        }

        String businessAddress = getStringFromJson(qBusiness.businessAddress.toString(), jsonNode, true);
        String businessIndustry = getStringFromJson(qBusiness.businessIndustry.toString(), jsonNode, true);
        String businessPhoneNumber = getStringFromJson(qBusiness.businessPhoneNumber.toString(), jsonNode, true);

        CompletionStage<Business> businessCompletionStage = businessRepository.updateBusiness(businessId, businessName,
                businessEmail, businessPhoneNumber, businessAddress, businessIndustry);
        return transformBusinessDetailsResponseToResult(businessCompletionStage);
    }

    @ApiOperation(value = KEY_DELETE_BUSINESS_VALUE, httpMethod = KEY_POST, response = Business.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> deleteBusiness(int businessId) {
        CompletionStage<Business> businessCompletionStage = businessRepository.deleteBusiness(businessId);
        return transformBusinessDetailsResponseToResult(businessCompletionStage);
    }

    private CompletionStage<Result> transformBusinessDetailsResponseToResult(CompletionStage<Business> businessCompletionStage) {
        return businessCompletionStage.thenApplyAsync(business -> {
            if (business != null) {
                return CsbBaseModel.getSerializer(request().queryString()).printWithResult(business);
            } else {
                return badRequest(ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_ID, Reason.BAD_REQUEST));
            }
        }, getContext());
    }

}