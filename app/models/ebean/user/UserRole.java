package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by Corey Caplan on 10/9/17.
 */
@ApiModel(description = "The role of the user in the database for accessing routes for this web server.")
@Table(name = "user_role")
@Entity
public class UserRole extends CsbBaseModel {

    @Id
    private String userId;

    @OneToOne
    private User user;

    @ApiModelProperty(value = "The role userRoleModel type of this user which contains miscellaneous information about the " +
            "user\'s role.")
    @ManyToOne(optional = false)
    @Expose
    private UserRoleModel userRoleModel;

    @ApiModelProperty(value = "The date at which the given user was added for this role into the database.")
    @Expose
    private Date dateAdded;

    @ApiModelProperty(value = "The date at which the given user\'s role was revoked in the database.")
    @Nullable
    @Expose
    private Date dateEnded;

    @ApiModelProperty(value = "True if the user is active for this role and can access this service (with the given " +
            "role) or false otherwise.")
    @Expose
    private boolean isActive;

    public UserRoleModel getUserRoleModel() {
        return userRoleModel;
    }

    public void setUserRoleModel(UserRoleModel userRoleModel) {
        this.userRoleModel = userRoleModel;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @Nullable
    public Date getDateEnded() {
        return dateEnded;
    }

    public void setDateEnded(@Nullable Date dateEnded) {
        this.dateEnded = dateEnded;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
