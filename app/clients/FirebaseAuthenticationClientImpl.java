package clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.firebase.AutomateCsbToken;
import play.Logger;
import play.libs.Json;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Corey Caplan on 11/6/17.
 */
public class FirebaseAuthenticationClientImpl implements FirebaseAuthenticationClient {

    private static final Logger.ALogger logger = Logger.of(FirebaseAuthenticationClientImpl.class);

    @Inject
    public FirebaseAuthenticationClientImpl() {
    }

    @Override
    public AutomateCsbToken verifyIdToken(String idToken) throws InterruptedException, TimeoutException, ExecutionException {
        FirebaseToken token = FirebaseAuth.getInstance()
                .verifyIdTokenAsync(idToken)
                .get(15, TimeUnit.SECONDS);

        logger.debug("Token Claims: {}", new GsonBuilder().setPrettyPrinting().create().toJson(token.getClaims()));

        String googleAccountId = getGoogleAccountIdFromFirebaseCustomObject(token);

        return new AutomateCsbToken(googleAccountId, token.getEmail(), token.getName());
    }

    private String getGoogleAccountIdFromFirebaseCustomObject(FirebaseToken token) {
        try {
            return Json.parse(new Gson().toJson(token.getClaims()))
                    .get("firebase")
                    .get("identities")
                    .get("google.com")
                    .get(0).asText();
        } catch (Exception e) {
            logger.error("Error: ", e);
            return null;
        }
    }

}
