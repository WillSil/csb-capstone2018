package clients;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import models.ebean.user.Role;
import models.exceptions.InvalidTokenException;
import models.exceptions.ServerErrorException;
import models.ebean.user.User;
import models.ebean.user.UserRole;
import models.ebean.user.UserRoleModel;
import play.Logger;
import utilities.Validation;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Corey Caplan on 10/24/17.
 */
@Singleton
public final class AuthenticatorClient {

    private static final Logger.ALogger logger = Logger.of(AuthenticatorClient.class);

    public enum AuthenticatorResult {
        SUCCESS, MISSING_TOKEN, INVALID_TOKEN, SERVER_ERROR, NOT_AUTHORIZED;
    }

    static final String KEY_JWT_KEY = "play.http.jwt.key";
    static final String KEY_JWT_ALGORITHM = "HmacSHA512";
    static final SignatureAlgorithm JWT_ALGORITHM = SignatureAlgorithm.HS512;

    static final String KEY_AUTOMATE_CSB_ID = "automate_csb_id";
    static final String KEY_GOOGLE_USER_ID = "google_user_id";
    static final String KEY_NAME = "name";
    static final String KEY_EMAIL = "email";
    static final String KEY_IS_ACTIVE = "is_active";
    static final String KEY_DATE_ADDED = "date_added";
    static final String KEY_DATE_ENDED = "date_ended";
    static final String KEY_ROLE_TYPE = "role_type";
    static final String KEY_ROLE_DESCRIPTION = "role_description";
    static final String KEY_ROLE_PRIORITY = "role_priority";

    private final byte[] encodedKey;

    @Inject
    public AuthenticatorClient() {
        try {
            encodedKey = Files.readAllBytes(new File("conf/jwt-key.txt").toPath());
        } catch (IOException e) {
            String[] list = new File("./").list();
            if (list != null) {
                Arrays.stream(list).forEach(name -> logger.error("File: {}", name));
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Generates a one-time use key to be bound into the configuration of the application
     */
    static Key generateKey() {
        return MacProvider.generateKey();
    }

    public byte[] getEncodedKey() {
        return encodedKey;
    }

    public AuthenticatorResult isAdminAuthenticated(String jwt) {
        return isUserAuthenticated(jwt, this::getResultFromAdminUser);
    }

    public AuthenticatorResult isFiscalManagerAuthenticated(String jwt) {
        return isUserAuthenticated(jwt, this::getResultFromFiscalManagerUser);
    }

    public AuthenticatorResult isLegalContractorAuthenticated(String token) {
        return isUserAuthenticated(token, this::getResultFromLegalContractorUser);
    }

    /**
     * To be called after authenticating a user (which puts the object into the cache). Gets the user object and puts
     * it into the context's args.
     *
     * @param args The args associated with this Http.Context
     * @param user The user to be put into this context's argument map
     */
    public void putUserIntoArgs(Map<String, Object> args, User user) {
        args.put(user.getJwt(), user);
    }

    /**
     * @param user A user that has been successfully logged in with Google
     * @return A JWT for the user, represented as a raw string.
     */
    public String createJwtForUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(KEY_AUTOMATE_CSB_ID, user.getUserId());
        claims.put(KEY_EMAIL, user.getEmail());
        claims.put(KEY_GOOGLE_USER_ID, user.getGoogleAccountId());
        claims.put(KEY_NAME, user.getName());

        UserRole userRole = user.getUserRole();
        claims.put(KEY_IS_ACTIVE, userRole.isActive());
        claims.put(KEY_DATE_ADDED, userRole.getDateAdded());
        claims.put(KEY_DATE_ENDED, userRole.getDateEnded());
        claims.put(KEY_ROLE_TYPE, userRole.getUserRoleModel().getRoleType());
        claims.put(KEY_ROLE_DESCRIPTION, userRole.getUserRoleModel().getRoleDescription());
        claims.put(KEY_ROLE_PRIORITY, userRole.getUserRoleModel().getRolePriority());

        return Jwts.builder()
                .signWith(JWT_ALGORITHM, encodedKey)
                .setExpiration(null)
                .setClaims(claims)
                .compact();
    }

    /**
     * Should only be called if the authentication succeeded
     *
     * @param jwt The user's JWT which was taken from the HTTP headers
     * @return The user tied to this request.
     */
    public User getUserFromRequest(String jwt) {
        try {
            return getUserFromJwt(jwt);
        } catch (ServerErrorException | InvalidTokenException e) {
            logger.error("This should never happen");
            throw new RuntimeException(e);
        }
    }


    // Mark - Private Methods

    /**
     * @param jwt The user's JWT which was sent to the server in a request's header
     * @return The {@link User} associated with the token or null if an error occurs or the token is invalid.
     */
    private User getUserFromJwt(String jwt) throws ServerErrorException, InvalidTokenException {

        Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser()
                    .setSigningKey(encodedKey)
                    .parseClaimsJws(jwt);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            logger.info("Could not verify user for JWT: {}", jwt);
            throw new InvalidTokenException();
        }


        String userId = claimsJws.getBody().get(KEY_AUTOMATE_CSB_ID, String.class);
        String googleUserId = claimsJws.getBody().get(KEY_GOOGLE_USER_ID, String.class);
        String email = claimsJws.getBody().get(KEY_EMAIL, String.class);
        String name = claimsJws.getBody().get(KEY_NAME, String.class);
        Boolean isActive = claimsJws.getBody().get(KEY_IS_ACTIVE, Boolean.class);
        Date startDate = claimsJws.getBody().get(KEY_DATE_ADDED, Date.class);
        Date endDate = claimsJws.getBody().get(KEY_DATE_ENDED, Date.class); // Can be null
        String roleType = claimsJws.getBody().get(KEY_ROLE_TYPE, String.class);
        String roleDescription = claimsJws.getBody().get(KEY_ROLE_TYPE, String.class);
        Integer rolePriority = claimsJws.getBody().get(KEY_ROLE_PRIORITY, Integer.class);

        if (userId == null || googleUserId == null || email == null || name == null || isActive == null ||
                startDate == null || roleType == null || roleDescription == null ||
                rolePriority == null) {
            throw new InvalidTokenException();
        }

        User user = new User();
        user.setUserId(userId);
        user.setGoogleAccountId(googleUserId);
        user.setEmail(email);
        user.setName(name);

        UserRole userRole = new UserRole();
        userRole.setActive(isActive);
        userRole.setDateAdded(startDate);
        userRole.setDateEnded(endDate);

        UserRoleModel userRoleModel = new UserRoleModel();
        userRoleModel.setRoleType(roleType);
        userRoleModel.setRoleDescription(roleDescription);
        userRoleModel.setRolePriority(rolePriority);
        userRole.setUserRoleModel(userRoleModel);

        user.setUserRole(userRole);

        return user;
    }

    private AuthenticatorResult isUserAuthenticated(String token, Function<User, AuthenticatorResult> resultFunction) {
        if (Validation.isEmpty(token)) {
            return AuthenticatorResult.MISSING_TOKEN;
        }

        User user;
        try {
            user = getUserFromJwt(token);
        } catch (InvalidTokenException e) {
            return AuthenticatorResult.INVALID_TOKEN;
        } catch (ServerErrorException e) {
            return AuthenticatorResult.SERVER_ERROR;
        }

        return resultFunction.apply(user);
    }

    private AuthenticatorResult getResultFromAdminUser(User user) {
        if (user.getUserRole().getUserRoleModel().getRoleType().equals(Role.ADMINISTRATOR.getRawText()) &&
                user.getUserRole().isActive()) {
            return AuthenticatorResult.SUCCESS;
        } else {
            return AuthenticatorResult.NOT_AUTHORIZED;
        }
    }

    private AuthenticatorResult getResultFromFiscalManagerUser(User user) {
        if (user.getUserRole().getUserRoleModel().getRoleType().equals(Role.FISCAL_MANAGER.getRawText()) &&
                user.getUserRole().isActive()) {
            return AuthenticatorResult.SUCCESS;
        } else {
            return AuthenticatorResult.NOT_AUTHORIZED;
        }
    }

    private AuthenticatorResult getResultFromLegalContractorUser(User user) {
        if (user.getUserRole().getUserRoleModel().getRoleType().equals(Role.LEGAL_CONTRACTOR.getRawText()) &&
                user.getUserRole().isActive()) {
            return AuthenticatorResult.SUCCESS;
        } else {
            return AuthenticatorResult.NOT_AUTHORIZED;
        }
    }

}
