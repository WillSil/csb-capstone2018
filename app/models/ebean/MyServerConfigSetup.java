package models.ebean;

import io.ebean.Platform;
import io.ebean.config.ServerConfig;
import io.ebean.config.dbplatform.DbType;
import io.ebean.event.ServerConfigStartup;
import play.Logger;

/**
 * Created by Corey Caplan on 10/17/17.
 */
@SuppressWarnings("unused")
public class MyServerConfigSetup implements ServerConfigStartup {

    public void onStart(ServerConfig serverConfig) {
        Logger.info("Creating eBean server config...");
        serverConfig.setDatabaseSequenceBatchSize(1);
        serverConfig.setDatabasePlatformName("postgres");
        serverConfig.loadFromProperties();
        serverConfig.addCustomMapping(DbType.TIMESTAMP, "timestamp", Platform.POSTGRES);
    }

}
