package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.auth.FirebaseAuth;
import global.BaseTestApplicationForTesting;
import io.ebean.Ebean;
import models.ebean.user.User;
import models.ebean.user.query.QUser;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repositories.UserRepository;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.*;
import static play.test.Helpers.*;

/**
 * Created by Corey Caplan on 11/9/17.
 */
public class AdminControllerTest extends BaseTestApplicationForTesting {

    private static final Logger.ALogger logger = Logger.of(AdminControllerTest.class);

    private static final String ADMIN_EMAIL = "cdc218@lehigh.edu";

    private UserRepository userRepository;

    @Before
    public void setup() {
        Ebean.find(User.class)
                .where()
                .eq(QUser.alias().email.toString(), ADMIN_EMAIL)
                .findOneOrEmpty()
                .ifPresent(Ebean::delete);

        userRepository = app.injector().instanceOf(UserRepository.class);
    }

    @Test
    public void signInAdminWithGoogleSuccess() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithNoAuth(routes.AdminController.signInAdminWithGoogle())
                .header(AUTHORIZATION, AdminController.ROUTE_PASSWORD)
                .bodyJson(getJsonNodeForEmail());

        Result result = route(app, requestBuilder);

        logger.info("Result: {}", contentAsString(result));

        assertEquals(200, result.status());

        User user = userRepository.getUserByEmail(ADMIN_EMAIL).toCompletableFuture().join();
        assertNotNull(user);

        removeUserFromFirebase(user);
    }

    @Test
    public void signInAdminWithGoogleFailure() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithNoAuth(routes.AdminController.signInAdminWithGoogle())
                .header(AUTHORIZATION, "A BAD PASSWORD")
                .bodyJson(getJsonNodeForEmail());

        Result result = route(app, requestBuilder);

        logger.info("Result: {}", contentAsString(result));

        assertEquals(401, result.status());

        User admin = userRepository.getUserByEmail(ADMIN_EMAIL).toCompletableFuture().join();
        assertNull(admin);
    }

    @Test
    public void signInAdminWithGoogleFailureDueToAlreadyExists() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithNoAuth(routes.AdminController.signInAdminWithGoogle())
                .bodyJson(getJsonNodeForEmail())
                .header(AUTHORIZATION, AdminController.ROUTE_PASSWORD);

        Result result = route(app, requestBuilder);

        logger.info("Result: {}", contentAsString(result));

        assertEquals(200, result.status());

        User user = userRepository.getUserByEmail(ADMIN_EMAIL).toCompletableFuture().join();
        assertNotNull(user);

        requestBuilder = createRequestBuilderWithNoAuth(routes.AdminController.signInAdminWithGoogle())
                .bodyJson(getJsonNodeForEmail())
                .header(AUTHORIZATION, AdminController.ROUTE_PASSWORD);

        result = route(app, requestBuilder);

        assertEquals(409, result.status());

        removeUserFromFirebase(user);
    }

    @Test
    public void getFirebaseAccessToken() throws Exception {
        Http.RequestBuilder requestBuilder = createRequestBuilderWithNoAuth(routes.AdminController.signInAdminWithGoogle())
                .bodyJson(getJsonNodeForEmail())
                .header(AUTHORIZATION, AdminController.ROUTE_PASSWORD);

        Result result = route(app, requestBuilder);

        assertEquals(200, result.status());

        User user = userRepository.getUserByEmail(ADMIN_EMAIL).toCompletableFuture().join();

        assertNotNull(user);

        String firebaseToken = FirebaseAuth.getInstance().createCustomTokenAsync(user.getGoogleAccountId())
                .get(30, TimeUnit.SECONDS);
        assertNotNull(firebaseToken);

        logger.info("Firebase token: {}", firebaseToken);

        removeUserFromFirebase(user);
    }

    private static JsonNode getJsonNodeForEmail() {
        return Json.newObject().put(User.KEY_EMAIL, ADMIN_EMAIL);
    }

    private static void removeUserFromFirebase(User user) throws Exception {
        FirebaseAuth.getInstance()
                .deleteUserAsync(user.getGoogleAccountId())
                .get(30, TimeUnit.SECONDS);
    }

}