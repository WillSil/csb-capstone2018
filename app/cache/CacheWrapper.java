package cache;

import com.typesafe.config.Config;
import controllers.BaseController;
import net.spy.memcached.*;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.transcoders.SerializingTranscoder;
import play.Environment;
import play.Logger;
import play.api.Play;
import play.inject.Injector;
import utilities.ConfigurationUtility;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Corey Caplan on 10/24/17.
 */
@Singleton
public class CacheWrapper implements AutomateCsbCache {

    private static final Logger.ALogger logger = Logger.of(CacheWrapper.class);

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    static String createHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input.getBytes("UTF-8"));
            byte[] hash = digest.digest();

            StringBuilder stringBuilder = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                stringBuilder.append(HEX_CHARS[(b & 0xF0) >> 4]);
                stringBuilder.append(HEX_CHARS[b & 0x0F]);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            logger.error("Error: ", e);
            return null;
        }
    }

    private MemcachedClient client;

    @Inject
    public CacheWrapper(Config config, Environment environment) {
        try {
            ConnectionFactory connection;
            String username;
            String password;
            String serverAddress;

            username = ConfigurationUtility.getString("MEMCACHIER_USERNAME", config, environment);
            password = ConfigurationUtility.getString("MEMCACHIER_PASSWORD", config, environment);
            serverAddress = ConfigurationUtility.getString("MEMCACHIER_SERVERS", config, environment);

            AuthDescriptor authDescriptor = new AuthDescriptor(
                    new String[]{"PLAIN"},
                    new PlainCallbackHandler(username, password)
            );
            connection = new ConnectionFactoryBuilder()
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                    .setAuthDescriptor(authDescriptor)
                    .setFailureMode(FailureMode.Retry)
                    .setOpTimeout(5000)
                    .setTranscoder(new CustomSerializingTranscoder())
                    .build();

            client = new MemcachedClient(connection, AddrUtil.getAddresses(serverAddress));
            logger.info("Successfully created Mem-Cache client");

            client.flush().get();
            logger.info("Successfully flushed Mem-Cache client");
        } catch (Exception e) {
            logger.error("Couldn't create a connection to Mem-Cache", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * @param key            The key that the given object should be saved as.
     * @param expirationTime The time (in seconds) since the UNIX epoch, used as the expiration time or seconds since
     *                       the current time.
     * @param objectToSave   The object that should be saved to in the cache.
     */
    void set(String key, int expirationTime, Object objectToSave) {
        // Don't block the thread with the operation, just assume it passes
        if (key.length() > 250) {
            // They key is too long, it needs to be shortened.
            key = key.substring(0, 250);
        }

        // Block the thread and wait for the value to be set
        client.set(key, expirationTime, objectToSave);
    }

    /**
     * @param key The key that the given object should be saved as
     */
    public void removeAndBlock(String key) {
        remove(key, false);
    }

    /**
     * @param key The key that the given object should be saved as
     */
    public void removeAsync(String key) {
        remove(key, true);
    }


    /**
     * Gets an object from the cache.
     *
     * @param key The key whose value should be extracted from the cache.
     * @return The object that was in the cache or null if there was none to be found.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key.length() > 250) {
            // They key is too long, it needs to be shortened.
            key = key.substring(0, 250);
        }

        try {
            return (T) client.get(key);
        } catch (Exception e) {
            logger.error("Error thrown while getting: {}", e);
            logger.error("Error: ", e);
            return null;
        }
    }

    private void remove(String key, boolean async) {
        // Don't block the thread with the operation, just assume it passes
        if (key.length() > 250) {
            // They key is too long, it needs to be shortened.
            key = key.substring(0, 250);
        }

        if(async) {
            client.delete(key);
        } else {
            // Block the thread and wait for the value to be deleted
            try {
                client.delete(key).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.error("An error occurred while removing item from cache: ", e);
            }
        }
    }

    static class CustomSerializingTranscoder extends SerializingTranscoder {

        @Override
        protected Object deserialize(byte[] bytes) {
            final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            ObjectInputStream in = null;
            try {
                ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
                in = new ObjectInputStream(bs) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                        try {
                            return currentClassLoader.loadClass(objectStreamClass.getName());
                        } catch (Exception e) {
                            return super.resolveClass(objectStreamClass);
                        }
                    }
                };
                return in.readObject();
            } catch (Exception e) {
                logger.error("Error: ", e);
                throw new RuntimeException(e);
            } finally {
                closeStream(in);
            }
        }

        private static void closeStream(Closeable c) {
            if (c != null) {
                try {
                    c.close();
                } catch (IOException e) {
                    logger.error("Error: ", e);
                }
            }
        }
    }

}
