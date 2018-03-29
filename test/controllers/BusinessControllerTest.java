package controllers;

import global.BaseTestApplicationForTesting;
import models.AutomateCsbPagedList;
import models.ebean.CsbBaseModel;
import models.ebean.business.Business;
import org.junit.Test;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.*;

/**
 *
 */
public class BusinessControllerTest extends BaseTestApplicationForTesting {

    private final Logger.ALogger logger = Logger.of(BusinessControllerTest.class);

    private final CsbBaseModel.Deserializer deserializer = CsbBaseModel.getDeserializer();

    @Test
    public void getBusinessesNoParameters() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithAdminAuth(routes.BusinessController.getBusinesses());

        Result result = Helpers.route(app, requestBuilder);
        assertEquals(200, result.status());

        AutomateCsbPagedList<Business> businesses = deserializer.fromJsonAsPagedList(Helpers.contentAsString(result), Business.class);

        assertTrue(businesses.getTotalItemCount() > 25);
        assertEquals(25, businesses.getRowCount());
        assertEquals(0, businesses.getCurrentPage());
    }

    @Test
    public void getBusinessesSecondPage() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithAdminAuth(routes.BusinessController.getBusinesses());

        Result result = Helpers.route(app, requestBuilder);
        assertEquals(200, result.status());

        AutomateCsbPagedList<Business> firstPage = deserializer.fromJsonAsPagedList(Helpers.contentAsString(result), Business.class);

        requestBuilder = createRequestBuilderWithAdminAuth(routes.BusinessController.getBusinesses());
                requestBuilder = requestBuilder.uri(String.format("%s?%s=%d", requestBuilder.uri(), BaseController.KEY_PAGE, 1));

        logger.info("Request Path: {}", requestBuilder.uri());

        result = Helpers.route(app, requestBuilder);
        assertEquals(200, result.status());

        AutomateCsbPagedList<Business> secondPage = deserializer.fromJsonAsPagedList(Helpers.contentAsString(result), Business.class);

        assertTrue(secondPage.getTotalItemCount() > 25);
        assertEquals(25, secondPage.getRowCount());
        assertEquals(1, secondPage.getCurrentPage());

        String firstBusinessName = firstPage.getData().get(0).getBusinessName();
        String secondBusinessName = secondPage.getData().get(0).getBusinessName();
        assertTrue(firstBusinessName.compareTo(secondBusinessName) < 0);
    }

    @Test
    public void getBusinessDetails() throws Exception {
    }

    @Test
    public void createBusiness() throws Exception {
    }

    @Test
    public void editBusiness() throws Exception {
    }

    @Test
    public void deleteBusiness() throws Exception {
    }

}