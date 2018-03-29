package clients;

import models.firebase.AutomateCsbToken;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Corey Caplan on 11/6/17.
 */
public interface FirebaseAuthenticationClient {

    /**
     * @param idToken An ID token that was sent by a Firebase client
     * @return A token that has been verified by firebase, cannot be NULL (but not from a thrown exception)
     */
    AutomateCsbToken verifyIdToken(String idToken) throws InterruptedException, TimeoutException, ExecutionException;

}
