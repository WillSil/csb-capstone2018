package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import global.BaseTestApplicationForTesting;
import models.AutomateCsbPagedList;
import models.ebean.CsbBaseModel;
import models.ebean.business.Engagement;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import repositories.EngagementRepository;
import utilities.ParameterConstants;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Corey Caplan on 11/20/17.
 */
public class EngagementControllerTest extends BaseTestApplicationForTesting {

    private static final int ENGAGEMENT_ID = 1;

    private EngagementRepository engagementRepository;

    @Before
    public void setup() {
        engagementRepository = instanceOf(EngagementRepository.class);
    }

    @Test
    public void getEngagementsWithNoParameters() throws Exception {
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.getEngagements());

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        String json = Helpers.contentAsString(result);
        AutomateCsbPagedList<Engagement> list = CsbBaseModel.getDeserializer().fromJsonAsPagedList(json, Engagement.class);

        assertEquals(25, list.getData().size());
        assertEquals(25, list.getRowCount());
        assertEquals(0, list.getCurrentPage());
        assertTrue(list.getTotalItemCount() > 25);

        CsbBaseModel.Serializer serializer = CsbBaseModel.getSerializer(Collections.singletonMap(CsbBaseModel.KEY_PRETTY_PRINT, new String[]{"true"}));
        Logger.info("JSON: {}", serializer.print(list.getData()));

        list.getData().forEach(engagement -> {
            assertNotNull(engagement.getCurrentStateType().getStatusChildType());
            assertNotNull(engagement.getCurrentStateType().getStatusChildDescription());
            assertNotNull(engagement.getCurrentStateType().getStatusParentType());
            assertNotEquals(0, engagement.getCurrentStateType().getChildOrderNumber());
        });
    }

    @Test
    public void updateEngagementStatusSuccess() throws Exception {
        long date = new Date().getTime() / 10000L;
        JsonNode node = Json.newObject().put(ParameterConstants.CHILD_STATUS_TYPE, "phase_1");
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagementStatus(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement engagement = CsbBaseModel.getDeserializer().fromJson(Helpers.contentAsString(result), Engagement.class);

        assertEquals(ENGAGEMENT_ID, engagement.getEngagementId());
        assertEquals(date, engagement.getDateStatusLastUpdated().getTime() / 10000L);
    }

    @Test
    public void updateEngagementStatusNoChildStatus() throws Exception {
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagementStatus(ENGAGEMENT_ID));

        Result result = Helpers.route(app, builder);

        assertEquals(400, result.status());
    }

    @Test
    public void updateEngagementStatusInvalidEngagementId() throws Exception {
        JsonNode node = Json.newObject()
                .put(ParameterConstants.CHILD_STATUS_TYPE, "phase_1");
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagementStatus(-11))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(404, result.status());
    }

    @Test
    public void updateEngagementStartDate() throws Exception {
        long startTime = new Date().getTime();

        Engagement originalEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        JsonNode node = Json.newObject()
                .put(ParameterConstants.ENGAGEMENT_DATE_STARTED, startTime);
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagement(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement updatedEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        assertEquals(startTime, updatedEngagement.getDateStarted().getTime());

        // Make sure everything else is the same
        assertEquals(originalEngagement.getDateEnded().getTime(), updatedEngagement.getDateEnded().getTime());
        assertEquals(originalEngagement.getDateStatusLastUpdated().getTime(), updatedEngagement.getDateStatusLastUpdated().getTime());
        assertEquals(originalEngagement.getDateImplemented().getTime(), updatedEngagement.getDateImplemented().getTime());
        assertEquals(originalEngagement.getCurrentStateType().getStatusChildType(), updatedEngagement.getCurrentStateType().getStatusChildType());
        assertEquals(originalEngagement.getEngagementName(), updatedEngagement.getEngagementName());
        assertEquals(originalEngagement.getEngagementNotes(), updatedEngagement.getEngagementNotes());
    }

    @Test
    public void updateEngagementEndDate() throws Exception {
        long endTime = new Date().getTime();

        Engagement originalEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        JsonNode node = Json.newObject()
                .put(ParameterConstants.ENGAGEMENT_DATE_ENDED, endTime);
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagement(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement updatedEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        assertEquals(endTime, updatedEngagement.getDateEnded().getTime());

        // Make sure everything else is the same
        assertEquals(originalEngagement.getDateStarted().getTime(), updatedEngagement.getDateStarted().getTime());
        assertEquals(originalEngagement.getDateStatusLastUpdated().getTime(), updatedEngagement.getDateStatusLastUpdated().getTime());
        assertEquals(originalEngagement.getDateImplemented().getTime(), updatedEngagement.getDateImplemented().getTime());
        assertEquals(originalEngagement.getCurrentStateType().getStatusChildType(), updatedEngagement.getCurrentStateType().getStatusChildType());
        assertEquals(originalEngagement.getEngagementName(), updatedEngagement.getEngagementName());
        assertEquals(originalEngagement.getEngagementNotes(), updatedEngagement.getEngagementNotes());
    }

    @Test
    public void updateEngagementImplementationDate() throws Exception {
        long implementationTime = new Date().getTime();

        Engagement originalEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        JsonNode node = Json.newObject()
                .put(ParameterConstants.ENGAGEMENT_DATE_IMPLEMENTED, implementationTime);
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagement(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement updatedEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        assertEquals(implementationTime, updatedEngagement.getDateImplemented().getTime());

        // Make sure everything else is the same
        assertEquals(originalEngagement.getDateStarted().getTime(), updatedEngagement.getDateStarted().getTime());
        assertEquals(originalEngagement.getDateEnded().getTime(), updatedEngagement.getDateEnded().getTime());
        assertEquals(originalEngagement.getDateStatusLastUpdated().getTime(), updatedEngagement.getDateStatusLastUpdated().getTime());
        assertEquals(originalEngagement.getCurrentStateType().getStatusChildType(), updatedEngagement.getCurrentStateType().getStatusChildType());
        assertEquals(originalEngagement.getEngagementName(), updatedEngagement.getEngagementName());
        assertEquals(originalEngagement.getEngagementNotes(), updatedEngagement.getEngagementNotes());
    }

    @Test
    public void updateEngagementNotesAndName() throws Exception {
        String name = "Automate CSB RULEZ";
        String notes = "Lorem Ipsum, y\'all";

        Engagement originalEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        JsonNode node = Json.newObject()
                .put(ParameterConstants.ENGAGEMENT_NAME, name)
                .put(ParameterConstants.ENGAGEMENT_NOTES, notes);
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagement(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement updatedEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        assertEquals(name, updatedEngagement.getEngagementName());
        assertEquals(notes, updatedEngagement.getEngagementNotes());

        // Make sure everything else is the same
        assertEquals(originalEngagement.getDateStarted().getTime(), updatedEngagement.getDateStarted().getTime());
        assertEquals(originalEngagement.getDateEnded().getTime(), updatedEngagement.getDateEnded().getTime());
        assertEquals(originalEngagement.getDateStatusLastUpdated().getTime(), updatedEngagement.getDateStatusLastUpdated().getTime());
        assertEquals(originalEngagement.getDateImplemented().getTime(), updatedEngagement.getDateImplemented().getTime());
        assertEquals(originalEngagement.getCurrentStateType().getStatusChildType(), updatedEngagement.getCurrentStateType().getStatusChildType());
    }

    @Test
    public void updateEngagementNotesToNull() throws Exception {
        String notes = "   "; // put some blank space - this should translate to null for the notes

        Engagement originalEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        JsonNode node = Json.newObject()
                .put(ParameterConstants.ENGAGEMENT_NOTES, notes);
        Http.RequestBuilder builder = createRequestBuilderWithAdminAuth(routes.EngagementController.updateEngagement(ENGAGEMENT_ID))
                .bodyJson(node);

        Result result = Helpers.route(app, builder);

        assertEquals(200, result.status());

        Engagement updatedEngagement = engagementRepository.getEngagementDetailsById(ENGAGEMENT_ID)
                .toCompletableFuture()
                .join();

        assertNull(updatedEngagement.getEngagementNotes());

        // Make sure everything else is the same
        assertEquals(originalEngagement.getDateStarted().getTime(), updatedEngagement.getDateStarted().getTime());
        assertEquals(originalEngagement.getDateEnded().getTime(), updatedEngagement.getDateEnded().getTime());
        assertEquals(originalEngagement.getDateStatusLastUpdated().getTime(), updatedEngagement.getDateStatusLastUpdated().getTime());
        assertEquals(originalEngagement.getDateImplemented().getTime(), updatedEngagement.getDateImplemented().getTime());
        assertEquals(originalEngagement.getCurrentStateType().getStatusChildType(), updatedEngagement.getCurrentStateType().getStatusChildType());
        assertEquals(originalEngagement.getEngagementName(), updatedEngagement.getEngagementName());
    }

}