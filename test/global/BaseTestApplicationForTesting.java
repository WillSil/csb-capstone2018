package global;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import org.junit.After;
import org.junit.Before;
import play.Logger;
import play.mvc.Call;
import play.mvc.Http;
import utilities.Validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

/**
 * Created by Corey Caplan on 11/2/17.
 */
public class BaseTestApplicationForTesting extends BaseTestApplication {

    private static final Logger.ALogger logger = Logger.of(BaseTestApplicationForTesting.class);

    @Override
    Map<String, Object> getExtraBindings() {
        logger.info("Pulling extra bindings for testing...");

        Map<String, Object> map = new HashMap<>();
        map.put("db.default.driver", "org.h2.Driver");
        map.put("db.default.url", "jdbc:h2:mem:play;MODE=PostgreSQL;DATABASE_TO_UPPER=FALSE");
        map.put("db.default.username", "sa");
        map.put("db.default.password", "");
        return map;
    }

    @Before
    public void setupDummySql() throws Exception {
        // Insert dummy data
        File file = new File("./sql/dummy-data-creation.sql");
        String sql = new String(Files.readAllBytes(file.toPath()));
        int amount = 0;
        String[] queries = sql.split(";+");
        EbeanServer server = Ebean.getDefaultServer();

        server.beginTransaction();

        for (String query : queries) {
            if (!Validation.isEmpty(query)) {
                amount += Ebean.getDefaultServer()
                        .createSqlUpdate(query)
                        .execute();
            }
        }

        server.commitTransaction();

        server.endTransaction();

        logger.info("Executed dummy data creation. Amount updated: {}", amount);

        assertTrue(amount >= 0);
    }

    @After
    public void destroyDummyData() throws IOException {
        // Destroy dummy data
        File file = new File("./sql/dummy-data-destruction.sql");
        String sql = new String(Files.readAllBytes(file.toPath()));
        int amount = 0;
        String[] queries = sql.split(";+");
        EbeanServer server = Ebean.getDefaultServer();

        server.beginTransaction();

        for (String query : queries) {
            if (!Validation.isEmpty(query)) {
                amount += Ebean.getDefaultServer()
                        .createSqlUpdate(query)
                        .execute();
            }
        }

        server.commitTransaction();

        server.endTransaction();

        logger.info("Executed dummy data destruction. Amount destroyed: {}", amount);

        assertTrue(amount >= 0);

    }

}
