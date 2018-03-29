package cache;

import javax.inject.Singleton;

/**
 * Created by Corey Caplan on 11/3/17.
 */
public interface AutomateCsbCache {

    void removeAndBlock(String key);

    void removeAsync(String key);

    <T> T get(String key);

}
