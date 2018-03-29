package controllers;

import actions.AdminAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.*;
import models.ControllerComponent;
import models.ebean.CsbBaseModel;
import models.ebean.business.Engagement;
import models.ebean.status.ChildStatus;
import models.search.EngagementSearch;
import play.Logger;
import play.libs.F;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Security;
import repositories.ChildStatusRepository;
import repositories.EngagementRepository;
import utilities.ParameterConstants;
import utilities.Validation;
import utilities.ApiMessages;
import utilities.ResultUtility;

import javax.inject.Inject;
import java.util.Map;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static models.search.EngagementSearch.DateFilter.*;
import static utilities.ParameterConstants.*;

/**
 * Created by Corey Caplan on 10/2/17.
 * <p></p>
 * Contains the routes necessary to handle all negotiations. There may be sub-classes down the road that break apart
 * this controller's functionality.
 */
@Api(value = "Engagements")
@Security.Authenticated(AdminAuthenticator.class)
public class EngagementController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(EngagementController.class);

    private final EngagementRepository engagementRepository;
    private final ChildStatusRepository childStatusRepository;

    private static final String KEY_GET_ENGAGEMENT_VALUE = "Get all of the admin\'s engagements";
    private static final String KEY_GET_ENGAGEMENT_DETAILS_VALUE = "Gets the details for this engagement";
    private static final String KEY_CREATE_ENGAGEMENT_VALUE = "Creates a new engagements";
    private static final String KEY_EDIT_ENGAGEMENT_VALUE = "Edits an already existing engagements";
    private static final String KEY_DELETE_ENGAGEMENT_VALUE = "Deletes an engagements permanently from the database";

    @Inject
    public EngagementController(ControllerComponent controllerComponent, EngagementRepository engagementRepository,
                                ChildStatusRepository childStatusRepository) {
        super(controllerComponent);
        this.engagementRepository = engagementRepository;
        this.childStatusRepository = childStatusRepository;
    }

    @ApiOperation(value = KEY_GET_ENGAGEMENT_VALUE, httpMethod = KEY_GET)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = ParameterConstants.BUSINESS_ID, paramType = "query", dataType = "int", value = "The ID of the business that whose engagements should be retrieved."),
            @ApiImplicitParam(name = ParameterConstants.ENGAGEMENT_NAME, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.BUSINESS_NAME, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = ParameterConstants.ENGAGEMENT_DATE_FILTER, paramType = "query", dataType = "string",
                    value = DESCRIPTION_DATE_FILTER + " Can be: " + PAST_INCOMPLETE + ", " + PAST_FULL + ", " + CURRENT + ", " + FUTURE
            ),
            @ApiImplicitParam(name = ParameterConstants.CHILD_STATUS_TYPE, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = KEY_ASCENDING, paramType = "query", dataType = "boolean", defaultValue = "true", value = ParameterConstants.DESCRIPTION_ASCENDING),
            @ApiImplicitParam(name = KEY_IS_OR_FILTER, paramType = "query", dataType = "boolean", defaultValue = "true", value = ParameterConstants.DESCRIPTION_IS_OR_FILTER),
            @ApiImplicitParam(name = KEY_PAGE, paramType = "query", dataType = "int", defaultValue = "0", value = ParameterConstants.DESCRIPTION_PAGE),
            @ApiImplicitParam(name = KEY_SORT_BY, paramType = "query", dataType = "String",
                    defaultValue = EngagementSearch.SortByCriteria.ENGAGEMENT_NAME,
                    value = DESCRIPTION_SORT_BY + " Can be: " + ENGAGEMENT_NAME + ", " + BUSINESS_NAME + ", " + ENGAGEMENT_DATE_STATUS_LAST_UPDATED
            ),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK response", response = Engagement.class)
    })
    public CompletionStage<Result> getEngagements() {
        Map<String, String[]> queryParams = request().queryString();

        int businessId = Validation.integer(BUSINESS_ID, queryParams, -1);
        String engagementNameValue = getStringFromQueryParams(ENGAGEMENT_NAME, queryParams);
        String businessNameValue = getStringFromQueryParams(BUSINESS_NAME, queryParams);

        String rawDateFilter = getStringFromQueryParams(ENGAGEMENT_DATE_FILTER, queryParams);
        String dateFilter = EngagementSearch.DateFilter.parse(rawDateFilter);

        String rawChildStatusType = getStringFromQueryParams(CHILD_STATUS_TYPE, queryParams);

        CompletionStage<ChildStatus> childStatusCompletableFuture;
        if (Validation.isEmpty(rawChildStatusType)) {
            childStatusCompletableFuture = CompletableFuture.completedFuture(null);
        } else {
            childStatusCompletableFuture = childStatusRepository.getChildStatusFromType(rawChildStatusType);
        }

        boolean isOrFilter = Validation.bool(KEY_IS_OR_FILTER, queryParams, true);
        boolean isAscending = Validation.bool(KEY_ASCENDING, queryParams, true);

        String sortBy = Validation.string(KEY_SORT_BY, queryParams);
        String sortByCriteria = EngagementSearch.SortByCriteria.parse(sortBy);

        int page = Validation.page(KEY_PAGE, queryParams);

        return childStatusCompletableFuture.thenApply(status -> {
            EngagementSearch search = new EngagementSearch(businessId, engagementNameValue, businessNameValue,
                    dateFilter, status, isOrFilter, sortByCriteria, isAscending);

            return engagementRepository.getEngagements(search, page).toCompletableFuture().join();
        }).thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }

    @ApiOperation(value = KEY_GET_ENGAGEMENT_DETAILS_VALUE, httpMethod = KEY_GET, response = Engagement.class)
    public CompletionStage<Result> getEngagementDetails(int engagementId) {

        return engagementRepository.getEngagementDetailsById(engagementId)
                .thenApplyAsync(CsbBaseModel.getSerializer(request().queryString())::printWithResult, getContext());
    }


    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_NAME),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_NOTES),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_STARTED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_ENDED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_IMPLEMENTED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_STATUS_LAST_UPDATED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_IS_ACTIVE)
    })
    @ApiOperation(value = KEY_CREATE_ENGAGEMENT_VALUE, httpMethod = KEY_POST, response = Engagement.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createEngagement(int businessId) {
        JsonNode jsonNode = request().body().asJson();

        if (businessId == -1) {
            logger.info("Invalid business ID, found {}", businessId);
            return wrapInFuture(badRequest(
                    ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_ID, ApiMessages.Reason.BAD_REQUEST)
            ));
        }

        String engagementName = Validation.string(ParameterConstants.ENGAGEMENT_NAME, jsonNode, true);
        if (Validation.isEmpty(engagementName)) {
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(ENGAGEMENT_NAME)));
        }

        long dateStartedTemp = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_STARTED, jsonNode);
        if (dateStartedTemp == -1) {
            logger.info("Invalid date started, found: {}", dateStartedTemp);
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(ENGAGEMENT_DATE_STARTED)));
        }

        long dateImplementedTemp = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_IMPLEMENTED, jsonNode);
        if (dateImplementedTemp == -1) {
            logger.info("Invalid date implemented, found: {}", dateImplementedTemp);
            return wrapInFuture(badRequest(ResultUtility.getFailureInvalidParam(ENGAGEMENT_DATE_IMPLEMENTED)));
        }

        Date dateEnded = null;
        long dateEndedTemp = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_ENDED, jsonNode);
        if (dateEndedTemp != -1) {
            dateEnded = new Date(dateEndedTemp);
        }

        Date dateStarted = new Date(dateStartedTemp);
        Date dateImplemented = new Date(dateImplementedTemp);
        String engagementNotes = getStringFromJson(ENGAGEMENT_NOTES, jsonNode, false);

        return engagementRepository.createEngagement(businessId, engagementName, engagementNotes, dateStarted, dateEnded, dateImplemented)
                .thenApplyAsync(engagement -> {
                    if (engagement == null) {
                        return badRequest(ResultUtility.getFailure(ApiMessages.MESSAGE_MISSING_BUSINESS_ID, ApiMessages.Reason.BAD_REQUEST));
                    } else {
                        return CsbBaseModel.getSerializer(request().queryString()).printWithResult(engagement);
                    }
                }, getContext());

    }


    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_NAME),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_NOTES),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_STARTED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_ENDED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_IMPLEMENTED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_DATE_STATUS_LAST_UPDATED, example = "1512260763342", dataType = "long", value = DESCRIPTION_DATE),
            @ApiImplicitParam(paramType = "body", name = ParameterConstants.ENGAGEMENT_IS_ACTIVE)
    })
    @ApiOperation(value = KEY_EDIT_ENGAGEMENT_VALUE, httpMethod = KEY_POST, response = Engagement.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateEngagement(int engagementId) {
        JsonNode node = request().body().asJson();

        String engagementName = Validation.string(ParameterConstants.ENGAGEMENT_NAME, node, true);
        String notes = Validation.string(ParameterConstants.ENGAGEMENT_NOTES, node, false);

        long rawDateStarted = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_STARTED, node);
        Date dateStarted = null;
        if (rawDateStarted != -1) {
            dateStarted = new Date(rawDateStarted);
        }

        long rawDateEnded = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_ENDED, node);
        Date dateEnded = null;
        if (rawDateEnded != -1) {
            dateEnded = new Date(rawDateEnded);
        }

        long rawDateImplemented = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_IMPLEMENTED, node);
        Date dateImplemented = null;
        if (rawDateImplemented != -1) {
            dateImplemented = new Date(rawDateImplemented);
        }

        long rawDateStatusLastUpdated = Validation.getLong(ParameterConstants.ENGAGEMENT_DATE_STATUS_LAST_UPDATED, node);
        Date dateStatusLastUpdated = null;
        if (rawDateStatusLastUpdated != -1) {
            dateStatusLastUpdated = new Date(rawDateStatusLastUpdated);
        }


        Boolean isActive = null;
        JsonNode activeNode = node.get(ParameterConstants.ENGAGEMENT_IS_ACTIVE);
        if (activeNode != null) {
            isActive = activeNode.asBoolean();
        }

        return engagementRepository.updateEngagement(engagementId, engagementName, isActive, notes,
                dateStarted, dateEnded, dateImplemented, dateStatusLastUpdated)
                .thenApplyAsync(engagement -> {
                    if (engagement != null) {
                        return CsbBaseModel.getSerializer(request().queryString()).printWithResult(engagement);
                    } else {
                        return notFound(ResultUtility.getFailureInvalidParam(ENGAGEMENT_ID));
                    }
                }, getContext());
    }

    @ApiOperation(value = KEY_DELETE_ENGAGEMENT_VALUE, response = Engagement.class)
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> deleteEngagement(int engagementId) {
        CompletionStage<Engagement> engagementCompletionStage = engagementRepository.deleteEngagement(engagementId);
        return transformEngagementDetailsResponseToResult(engagementCompletionStage);
    }

    @ApiOperation(value = "Update the phase of an engagement", httpMethod = KEY_POST, response = Engagement.class)
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = CHILD_STATUS_TYPE, value = "The type of phase to which the engagement will be updated", paramType = "body")
    })
    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> updateEngagementStatus(int engagementId) {
        JsonNode node = request().body().asJson();

        String rawChildStatus = getStringFromJson(CHILD_STATUS_TYPE, node, true);

        CompletionStage<ChildStatus> completionStage;
        if (!Validation.isEmpty(rawChildStatus)) {
            completionStage = childStatusRepository.getChildStatusFromType(rawChildStatus);
        } else {
            completionStage = CompletableFuture.completedFuture(null);
        }

        return completionStage.thenApply(status -> {
            F.Either<Result, Engagement> resultEngagementEither;
            if (status == null) {
                resultEngagementEither = F.Either.Left(badRequest(ResultUtility.getFailureInvalidParam(CHILD_STATUS_TYPE)));
                return resultEngagementEither;
            }

            Engagement engagement = engagementRepository.updateEngagementStatus(engagementId, status)
                    .toCompletableFuture()
                    .join();

            if (engagement == null) {
                resultEngagementEither = F.Either.Left(notFound(ResultUtility.getFailureInvalidParam(ENGAGEMENT_ID)));
            } else {
                resultEngagementEither = F.Either.Right(engagement);
            }

            return resultEngagementEither;
        }).thenApplyAsync(resultEngagementEither -> {
            if (resultEngagementEither.left.isPresent()) {
                return resultEngagementEither.left.get();
            }

            Engagement engagement = resultEngagementEither.right.orElse(null);
            if (engagement == null) {
                return notFound(ResultUtility.getFailureInvalidParam(ENGAGEMENT_ID));
            } else {
                return CsbBaseModel.getSerializer(request().queryString()).printWithResult(engagement);
            }
        }, getContext());
    }


    private CompletionStage<Result> transformEngagementDetailsResponseToResult(CompletionStage<Engagement> engagementCompletionStage) {
        return engagementCompletionStage.thenApplyAsync(engagement -> {
            if (engagement != null) {
                return CsbBaseModel.getSerializer(request().queryString()).printWithResult(engagement);
            } else {
                return badRequest(ResultUtility.getFailure(ApiMessages.MESSAGE_INVALID_ID, ApiMessages.Reason.BAD_REQUEST));
            }
        }, getContext());
    }

}
