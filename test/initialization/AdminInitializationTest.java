package initialization;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Userinfoplus;
import global.BaseTestApplicationForProduction;
import io.ebean.Ebean;
import models.ebean.user.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Corey Caplan on 11/17/17.
 * <p>
 * Extend from <i>BaseTestApplicationForProduction</i> since we are going to input this information into a real database
 */
public class AdminInitializationTest extends BaseTestApplicationForProduction {

    private AdminInitialization adminInitialization;

    @Before
    public void setUp() throws Exception {
        adminInitialization = new AdminInitialization();
    }

    @Test
    public void getCredential() throws Exception {
        Credential credential = adminInitialization.getCredential();

        assertNotNull(credential);

        Userinfoplus googleAccount = adminInitialization.getGoogleAccountId(credential);
        System.out.println("Google Account ID = " + googleAccount.getId());
        System.out.println("Refresh Token = " + credential.getRefreshToken());
        System.out.println("Access Token = " + credential.getAccessToken());
        System.out.println("Expiration Time Millis = " + credential.getExpirationTimeMilliseconds());

        System.out.println("Be sure to save the preceding refresh_token to access the admin\'s Google account");

        saveGoogleAccountToEbean(googleAccount, credential);
    }

    private void saveGoogleAccountToEbean(Userinfoplus googleAccount, Credential credential) throws Exception {
        try {
            Ebean.beginTransaction();

            User user = new User();
            user.setEmail(googleAccount.getEmail());
            user.setGoogleAccountId(googleAccount.getId());
            user.setName(googleAccount.getName());

            UserRole role = new UserRole();
            role.setDateAdded(new Date());
            role.setActive(true);
            role.setUserRoleModel(Role.ADMINISTRATOR.convertToUserRoleModel());
            user.setUserRole(role);

            Ebean.save(user);

            assertNotNull(user.getUserId());

            AuthUser authUser = new AuthUser(user, credential.getRefreshToken(), credential.getAccessToken(),
                    credential.getExpirationTimeMilliseconds());
            user.setAuthUser(authUser);

            Ebean.save(user);
            Ebean.commitTransaction();
        } finally {
            Ebean.endTransaction();
        }
    }

}