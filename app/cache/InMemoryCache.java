package cache;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Corey Caplan on 11/3/17.
 */
@Singleton
public class InMemoryCache implements AutomateCsbCache {

    private Map<String, ?> cache = new HashMap<>();

    @Inject
    public InMemoryCache() {
    }

    @Override
    public void removeAndBlock(String key) {
        cache.remove(key);
    }

    @Override
    public void removeAsync(String key) {
        cache.remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) cache.get(key);
    }
}
