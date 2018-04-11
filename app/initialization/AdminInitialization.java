package initialization;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static clients.GmailClient.CLIENT_ID;
import static clients.GmailClient.CLIENT_SECRET;

/**
 * Created by Corey Caplan on 11/10/17.
 * <p></p>
 * This class is responsible for the initial authorization of the admin account for the Automate CSB web application
 */
public class AdminInitialization {

    /**
     * Global instance of the scopes required by this application.
     */
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.GMAIL_SEND
    );

    private final HttpTransport httpTransport;
    private final JsonFactory factory;

    private final AuthorizationCodeInstalledApp authorizationCodeInstalledApp;

    public static void main(String[] args) {
        try {
            AdminInitialization adminInitialization = new AdminInitialization();
            Credential credential = adminInitialization.getCredential();

            System.out.println("Google Account ID = " + adminInitialization.getGoogleAccountId(credential));
            System.out.println("Access Token = " + credential.getAccessToken());
            System.out.println("Refresh Token = " + credential.getRefreshToken());
            System.out.println("Expiration Time Millis = " + credential.getExpirationTimeMilliseconds());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    AdminInitialization() throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        factory = JacksonFactory.getDefaultInstance();


        authorizationCodeInstalledApp = createAuthorizationInstalledApp();

    }

    Credential getCredential() throws Exception {
        return authorizationCodeInstalledApp.authorize("admin");
    }

    Userinfoplus getGoogleAccountId(Credential credential) throws IOException {
        Oauth2 oauth2 = new Oauth2.Builder(httpTransport, factory, credential)
                .setApplicationName("Automate Csb")
                .build();

        return oauth2.userinfo()
                .get()
                .execute();
    }

    private AuthorizationCodeInstalledApp createAuthorizationInstalledApp() throws Exception {
        // Set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, factory,
                CLIENT_ID, CLIENT_SECRET, SCOPES)
                .setAccessType("offline")
                .setDataStoreFactory(new FileDataStoreFactory(new File("./conf/resources")))
                .build();

        // Create the receiving local server
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(9000)
                .setCallbackPath("/Callback")
                .build();

        // Authorize the user by opening up the browser
        return new AuthorizationCodeInstalledApp(flow, receiver);
    }

}
