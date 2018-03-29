package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.UserIdGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Corey Caplan
 */
@ApiModel(description = "A user that uses this service.")
@Table(name = "_user")
@Entity
public class User extends CsbBaseModel implements Serializable {

    @SuppressWarnings("ConstantConditions")
    public static User getDummy(Role role) {
        User user = new User();
        String userId = "abc123";
        String email = "cdc218@lehigh.edu";
        String name = "Corey Caplan";
        String googleAccountId = "google123";
        Date dateAdded = new Date();
        dateAdded.setTime(dateAdded.getTime() - 1000L);
        boolean isActive = true;
        int rolePriority = role.getPriority();
        String roleType = role.getRawText();
        String roleDescription = role.getUiText();

        UserRole userRole = new UserRole();
        userRole.setDateAdded(dateAdded);
        userRole.setActive(isActive);

        UserRoleModel userRoleModel = new UserRoleModel();
        userRoleModel.setRolePriority(rolePriority);
        userRoleModel.setRoleType(roleType);
        userRoleModel.setRoleDescription(roleDescription);
        userRole.setUserRoleModel(userRoleModel);

        user.setUserId(userId);
        user.setGoogleAccountId(googleAccountId);
        user.setUserRole(userRole);
        user.setEmail(email);
        user.setName(name);

        return user;
    }

    private static final long serialVersionUID = 10231239148192413L;

    public static final String KEY_TOKEN = "id_token";
    public static final String KEY_EMAIL = "email";

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    @ApiModelProperty(value = "The unique ID of this user in the database.")
    @Id
    @GeneratedValue(generator = UserIdGenerator.KEY_ID)
    @Expose
    private String userId;

    @ApiModelProperty(value = "The unique email of this user in the database.")
    @Column(unique = true, nullable = false)
    @Expose
    private String email;

    @ApiModelProperty(value = "The user\'s name in the database.")
    @Expose
    @Column(nullable = false)
    private String name;

    @ApiModelProperty(value = "The user\'s unique ID according to Google and Google-Sign-In.")
    @Column(unique = true, nullable = false)
    @Expose
    private String googleAccountId;

    @ApiModelProperty(value = "The user\'s JWT (JSON Web Token), which is used to authenticate them for all secured routes.")
    @Expose
    private String jwt;

    @ApiModelProperty(value = "The role that this user currently has in this service.")
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Expose
    private UserRole userRole;

    @ApiModelProperty(hidden = true)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @Nullable
    private AuthUser authUser;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoogleAccountId() {
        return googleAccountId;
    }

    public void setGoogleAccountId(String googleAccountId) {
        this.googleAccountId = googleAccountId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Nullable
    public AuthUser getAuthUser() {
        return authUser;
    }

    public void setAuthUser(@Nullable AuthUser authUser) {
        this.authUser = authUser;
    }
}
