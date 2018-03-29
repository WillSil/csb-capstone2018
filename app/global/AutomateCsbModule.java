package global;

import cache.AutomateCsbCache;
import cache.CacheWrapper;
import clients.FirebaseAuthenticationClient;
import clients.FirebaseAuthenticationClientImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.Environment;
import play.Logger;
import play.Mode;

/**
 *
 */
public class AutomateCsbModule extends AbstractModule {

    @Override
    protected void configure() {
        Logger.info("Binding modules to interfaces...");
        bind(AutomateCsbCache.class).to(CacheWrapper.class).asEagerSingleton();
        bind(FirebaseAuthenticationClient.class).to(FirebaseAuthenticationClientImpl.class).asEagerSingleton();
        bind(GlobalApplication.class).to(GlobalApplicationImpl.class).asEagerSingleton();
    }

}
