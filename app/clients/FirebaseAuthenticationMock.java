package clients;

import com.google.firebase.auth.FirebaseAuthException;
import models.ebean.user.Role;
import models.ebean.user.User;
import models.firebase.AutomateCsbToken;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Corey Caplan on 11/6/17.
 */
public class FirebaseAuthenticationMock implements FirebaseAuthenticationClient {

    private static final String INVALID_TOKEN = "invalid_token";

    @Inject
    public FirebaseAuthenticationMock() {
    }

    public static String getOkayToken() {
        User user = User.getDummy(Role.ADMINISTRATOR);

        return user.getGoogleAccountId() + ";;" + user.getEmail() + ";;" + user.getName();
    }

    public static String getMalformedToken() {
        return INVALID_TOKEN;
    }

    public static String getInvalidToken() {
        return INVALID_TOKEN + ";;" + INVALID_TOKEN + ";;" + INVALID_TOKEN;
    }

    @Override
    public AutomateCsbToken verifyIdToken(String idToken) throws InterruptedException, TimeoutException, ExecutionException {
        String[] splitToken = idToken.split(";{2}+");
        if(splitToken.length != 3) {
            throw new ExecutionException(new IllegalArgumentException());
        }

        String googleUserId = splitToken[0];
        if(googleUserId.equals(INVALID_TOKEN)) {
            throw new ExecutionException(new FirebaseAuthException("401", "Invalid token!"));
        }

        String email = splitToken[1];
        String name = splitToken[2];
        return new AutomateCsbToken(googleUserId, email, name);
    }
}
