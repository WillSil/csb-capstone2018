package global;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import play.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Corey Caplan on 5/3/17.
 */
@Singleton
public class GlobalApplicationImpl implements GlobalApplication {

    private static final Logger.ALogger logger = Logger.of(GlobalApplicationImpl.class);

    public static final String APPLICATION_NAME = "Automate CSB";

    @Inject
    public GlobalApplicationImpl() {
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream("conf/firebase-auth-service-account.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        FirebaseOptions options;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(FirebaseApp.getApps().stream().noneMatch(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))) {
            FirebaseApp.initializeApp(options);
        }

        logger.info("Initialized connection to Firebase");
    }

}
