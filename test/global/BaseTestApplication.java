package global;

import cache.AutomateCsbCache;
import cache.InMemoryCache;
import clients.AuthenticatorClient;
import clients.FirebaseAuthenticationClient;
import clients.FirebaseAuthenticationMock;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.BaseController;
import models.ebean.user.Role;
import models.ebean.user.User;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.Logger;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.mvc.Call;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static play.mvc.Http.HeaderNames.HOST;
import static play.mvc.Http.HeaderNames.ORIGIN;
import static play.test.Helpers.fakeRequest;

/**
 *
 */
abstract class BaseTestApplication extends WithApplication {

    private static final Logger.ALogger logger = Logger.of(BaseTestApplication.class);

    private static final String HOST_FOR_TESTING = "localhost";
    private static final String ORIGIN_FOR_TESTING = "http://localhost:9000";

    @Override
    protected final Application provideApplication() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                // Configure any necessary custom modules here
                bind(AutomateCsbCache.class).to(InMemoryCache.class);
                bind(FirebaseAuthenticationClient.class).to(FirebaseAuthenticationMock.class).asEagerSingleton();
            }
        };

        Map<String, Object> map = new HashMap<>();
        map.put("application.global", "global.GlobalApplicationImpl");
        map.put("play.db.pool", "bonecp");
        map.put("play.evolutions.db.default.enabled", true);
        map.put("play.filters.csrf.header.bypassHeaders.X-Requested-With", "*");
        map.put("play.filters.csrf.header.bypassHeaders.Csrf-Token", "nocheck");
        map.put("play.filters.cors.pathPrefixes", Collections.singletonList("/"));
        map.put("play.filters.cors.allowedOrigins", Arrays.asList("*", "http://localhost:9000"));
        map.put("play.filters.cors.allowedHttpMethods", Arrays.asList("GET", "POST"));
        map.put("play.filters.cors.allowedHttpHeaders", Collections.singletonList("Accept"));
        map.put("play.filters.headers.contentSecurityPolicy", "");
        map.put("play.filters.hosts.allowed", Arrays.asList("localhost:9000", "localhost", "http://localhost:9000"));
        map.putAll(getExtraBindings());

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .configure(map)
                .overrides(testModule);

        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        logger.info("Created injector and building application...");

        return builder.build();
    }

    /**
     * @param call A call that needs to be augmented to create a standardized request.
     * @return A request builder that can be routed using the {@link Helpers#route(Application, Call)} method.
     */
    protected Http.RequestBuilder createRequestBuilderWithNoAuth(Call call) {
        Http.RequestBuilder requestBuilder = fakeRequest(call)
                .host(HOST_FOR_TESTING)
                .header(ORIGIN, ORIGIN_FOR_TESTING)
                .header("Csrf-Token", "nocheck");

        if (call.method().equals("POST")) {
            requestBuilder.header("Content-Type", "application/json");
        }

        return requestBuilder;
    }


    /**
     * @param call A call that needs to be augmented to create a standardized request.
     * @return A request builder that can be routed using the {@link Helpers#route(Application, Call)} method.
     */
    protected Http.RequestBuilder createRequestBuilderWithAdminAuth(Call call) {
        Http.RequestBuilder requestBuilder = fakeRequest(call)
                .host(HOST_FOR_TESTING)
                .header(ORIGIN, ORIGIN_FOR_TESTING)
                .header("Csrf-Token", "nocheck");

        requestBuilder = bindAdminJwtToRequest(requestBuilder);

        if (call.method().equals("POST")) {
            requestBuilder.header("Content-Type", "application/json");
        }

        return requestBuilder;
    }

    // Package-Private Methods

    /**
     * @return A map containing any extra bindings for the testing environment setup.
     */
    abstract Map<String, Object> getExtraBindings();

    // Private Methods

    private Http.RequestBuilder bindAdminJwtToRequest(Http.RequestBuilder requestBuilder) {
        AuthenticatorClient client = instanceOf(AuthenticatorClient.class);
        String jwt = client.createJwtForUser(User.getDummy(Role.ADMINISTRATOR));
        return requestBuilder.header(BaseController.AUTHORIZATION, jwt);
    }

}
