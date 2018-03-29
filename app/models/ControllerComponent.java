package models;

import cache.AutomateCsbCache;
import cache.CacheWrapper;
import com.typesafe.config.Config;
import play.Environment;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.util.concurrent.Executor;

/**
 * Created by Corey Caplan on 10/2/17.
 * <p></p>
 * Contains basic components needed to initialize the controller's constructor
 */
public class ControllerComponent {

    private final AutomateCsbCache cache;
    private final Config config;
    private final Environment environment;
    private final HttpExecutionContext context;

    @Inject
    public ControllerComponent(AutomateCsbCache cache, Config config, Environment environment, HttpExecutionContext context) {
        this.cache = cache;
        this.config = config;
        this.environment = environment;
        this.context = context;
    }

    public AutomateCsbCache getCache() {
        return cache;
    }

    public Config getConfig() {
        return config;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Executor getContext() {
        return context.current();
    }
}
