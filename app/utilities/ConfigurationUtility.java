package utilities;

import com.typesafe.config.Config;
import play.Environment;

/**
 * Created by Corey Caplan on 10/28/17.
 */
public class ConfigurationUtility {

    public static String getString(String key, Config config, Environment environment) {
        if (environment.isTest() || environment.isProd()) {
            return System.getenv(key);
        } else {
            return config.getString(key);
        }
    }

}
