package clients;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;

import com.google.api.services.gmail.Gmail;
import models.ebean.user.AuthUser;
import models.ebean.user.User;
import play.libs.ws.WSClient;
import repositories.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static global.GlobalApplicationImpl.APPLICATION_NAME;

public class GmailClient {

    public static String CLIENT_ID = "797634065396-3j10c0o31gpo5inm5srrqt0ghjmvrpbt.apps.googleusercontent.com";
    public static String CLIENT_SECRET = "_TlRXcq3utQMeP-hY2CT-Wr0";

    private final UserRepository userRepository;

    @Inject
    public GmailClient(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CompletionStage<Void> getEmailsFromAdmin(String adminUserId) {
        return userRepository.getUserById(adminUserId)
                .thenApply(user -> {
                    Gmail gmail = createGmailClient(user.getAuthUser());


                    return null;
                });
    }

    private void getMessageFromGmail(Gmail gmail) {
        try {
            Gmail.Users.Messages.List list = gmail.users().messages().list("me");
            // TODO sync emails
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Gmail createGmailClient(AuthUser authUser) {
        try {
            JsonFactory factory = JacksonFactory.getDefaultInstance();
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();


            Credential credential = new GoogleCredential()
                    .setRefreshToken(authUser.getRefreshToken())
                    .setAccessToken(authUser.getAccessToken())
                    .setExpirationTimeMilliseconds(authUser.getExpirationTimeUnixMillis());

            return new Gmail.Builder(httpTransport, factory, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}