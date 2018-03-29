package clients;

import clients.AuthenticatorClient.AuthenticatorResult;
import global.BaseTestApplicationForTesting;
import io.jsonwebtoken.*;
import models.ebean.user.Role;
import models.ebean.user.User;
import models.ebean.user.UserRole;
import models.ebean.user.UserRoleModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Corey Caplan on 11/2/17.
 */
public class AuthenticatorClientTest extends BaseTestApplicationForTesting {

    private AuthenticatorClient client;

    @Before
    public void setup() {
        client = instanceOf(AuthenticatorClient.class);
    }

    @Test
    public void isAdminAuthenticated() throws Exception {
        isUserAuthenticated();

        String jwt = client.createJwtForUser(User.getDummy(Role.ADMINISTRATOR));
        AuthenticatorResult result = client.isAdminAuthenticated(jwt);
        assertEquals(AuthenticatorResult.SUCCESS, result);
    }

    @Test
    public void isFiscalManagerAuthenticated() throws Exception {
        isUserAuthenticated();

        String jwt = client.createJwtForUser(User.getDummy(Role.FISCAL_MANAGER));
        AuthenticatorResult result = client.isFiscalManagerAuthenticated(jwt);
        assertEquals(AuthenticatorResult.SUCCESS, result);
    }

    @Test
    public void isLegalContractorAuthenticated() throws Exception {
        isUserAuthenticated();

        String jwt = client.createJwtForUser(User.getDummy(Role.LEGAL_CONTRACTOR));
        AuthenticatorResult result = client.isLegalContractorAuthenticated(jwt);
        assertEquals(AuthenticatorResult.SUCCESS, result);
    }

    @Test
    public void putUserIntoArgs() throws Exception {
        Map<String, Object> args = new HashMap<>();
        User user = User.getDummy(Role.ADMINISTRATOR);
        user.setJwt(client.createJwtForUser(user));

        client.putUserIntoArgs(args, user);

        assertTrue(args.containsKey(user.getJwt()));
        assertNotNull(args.get(user.getJwt()));
        assertTrue(args.get(user.getJwt()) instanceof User);
    }

    @Test
    public void createJwtForUser() throws Exception {
        User user = User.getDummy(Role.ADMINISTRATOR);
        String rawJwt = client.createJwtForUser(user);
        Jws<Claims> jwt = Jwts.parser()
                .setSigningKey(client.getEncodedKey())
                .parseClaimsJws(rawJwt);
        Claims claims = jwt.getBody();

        assertEquals(user.getUserId(), claims.get(AuthenticatorClient.KEY_AUTOMATE_CSB_ID, String.class));
        assertEquals(user.getEmail(), claims.get(AuthenticatorClient.KEY_EMAIL, String.class));
        assertEquals(user.getGoogleAccountId(), claims.get(AuthenticatorClient.KEY_GOOGLE_USER_ID, String.class));
        assertEquals(user.getName(), claims.get(AuthenticatorClient.KEY_NAME, String.class));

        UserRole userRole = user.getUserRole();
        assertEquals(userRole.isActive(), claims.get(AuthenticatorClient.KEY_IS_ACTIVE, Boolean.class));
        assertEquals(userRole.getDateAdded(), claims.get(AuthenticatorClient.KEY_DATE_ADDED, Date.class));
        assertEquals(userRole.getDateEnded(), claims.get(AuthenticatorClient.KEY_DATE_ENDED, Date.class));

        UserRoleModel userRoleModel = userRole.getUserRoleModel();
        assertEquals(userRoleModel.getRoleType(), claims.get(AuthenticatorClient.KEY_ROLE_TYPE, String.class));
        assertEquals(userRoleModel.getRoleDescription(), claims.get(AuthenticatorClient.KEY_ROLE_DESCRIPTION, String.class));
        assertEquals((Integer) userRoleModel.getRolePriority(), claims.get(AuthenticatorClient.KEY_ROLE_PRIORITY, Integer.class));
    }

    @Test
    public void getUserFromRequest() throws Exception {
        User user = User.getDummy(Role.ADMINISTRATOR);
        String jwt = client.createJwtForUser(user);
        User user2 = client.getUserFromRequest(jwt);

        assertEquals(user, user2);
    }

    // Mark - Private Methods

    private void isUserAuthenticated() {
        AuthenticatorResult result = client.isAdminAuthenticated(null);
        assertEquals(AuthenticatorResult.MISSING_TOKEN, result);

        String jwt = "aJwt";
        result = client.isAdminAuthenticated(jwt);
        assertEquals(AuthenticatorResult.INVALID_TOKEN, result);

        jwt = Jwts.builder()
                .claim("user_id", "abc123")
                .signWith(SignatureAlgorithm.HS512, client.getEncodedKey())
                .compact();
        result = client.isAdminAuthenticated(jwt);
        assertEquals(AuthenticatorResult.INVALID_TOKEN, result);
    }

}