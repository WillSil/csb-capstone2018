package initialization;

import io.ebean.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

/**
 * Created by Corey Caplan on 12/2/17.
 * <p></p>
 * This class calculates DDL changes in our model by looking for field changes and comparing them to the SQL files
 * found in src/main/resources/dbmigration and outputs the next version there.
 */
public class MainDBMigration {

    public static void main(String[] args) throws IOException {
        // This should always be set to the next version which is NOT present in the dbmigration folder
        System.setProperty("ddl.migration.version", "1.0");
        System.setProperty("ddl.migration.name", "DDL for final submission");

        DbMigration dbMigration = new DbMigration();
        dbMigration.setPlatform(Platform.POSTGRES);

        // Generate the migration ddl and xml with EbeanServer in "offline" mode
        dbMigration.generateMigration();
    }

}
