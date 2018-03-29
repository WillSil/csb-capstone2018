package models.ebean;

import io.ebean.config.IdGenerator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Corey Caplan on 10/9/17.
 */
public class UserIdGenerator implements IdGenerator {

    public static final String KEY_ID = "models.ebean.UserIdGenerator";

    /* Assign a string that contains the set of characters you allow. */
    private static final String SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BUFFER_LENGTH = 64;
    private static final Random RANDOM = new SecureRandom();

    private final StringBuilder builder;

    public UserIdGenerator() {
        builder = new StringBuilder();
    }

    @Override
    public Object nextValue() {
        builder.append("usr_");
        for (int idx = 0; idx < BUFFER_LENGTH; ++idx) {
            builder.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        }
        return new String(builder);
    }

    @Override
    public String getName() {
        return KEY_ID;
    }
}
