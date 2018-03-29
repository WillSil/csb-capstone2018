package global;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class BaseTestApplicationForProduction extends BaseTestApplication {

    @Override
    Map<String, Object> getExtraBindings() {
        Map<String, Object> map = new HashMap<>();
        map.put("db.default.driver", "org.postgresql.Driver");
        map.put("db.default.url", "postgres://vlquqcltuvahuj:3cf257d997dbdc35e57536299ea2cb6207142411e9c68226037dc8c9ab5d3430@ec2-184-72-248-8.compute-1.amazonaws.com:5432/d92ch0gpjm7jda?sslmode=require");
        return map;
    }

}
