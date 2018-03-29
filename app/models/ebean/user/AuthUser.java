package models.ebean.user;

import models.ebean.CsbBaseModel;

import javax.persistence.*;

/**
 * Created by Corey Caplan on 11/11/17.
 */
@Table(name = "auth_user")
@Entity
public class AuthUser extends CsbBaseModel {

    @Id
    private String userId;

    @OneToOne
    private User user;
    private String refreshToken;
    private String accessToken;
    private long expirationTimeUnixMillis;

    public AuthUser(User user, String refreshToken, String accessToken, long expirationTimeUnixMillis) {
        this.userId = user.getUserId();
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expirationTimeUnixMillis = expirationTimeUnixMillis;
    }

    public String getUserId() {
        return userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public long getExpirationTimeUnixMillis() {
        return expirationTimeUnixMillis;
    }
}
