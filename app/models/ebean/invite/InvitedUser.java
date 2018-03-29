package models.ebean.invite;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.user.UserRoleModel;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Corey Caplan on 10/9/17.
 */
@ApiModel(description = InvitedUser.KEY_DESCRIPTION)
@Table(name = "invited_user")
@Entity
public class InvitedUser extends CsbBaseModel {

    static final String KEY_DESCRIPTION = "A user that has been invited to access this service (via gmail), but has " +
            "not yet accepted the invitation. This list basically works as a queue, and as users accept the invite, " +
            "users are popped off the queue.";

    @ApiModelProperty(value = "The email of the user that was invited.")
    @Id
    @Expose
    private String email;

    @ApiModelProperty(value = "The role of the user that was invited, as selected by the admin.")
    @ManyToOne
    @Expose
    private UserRoleModel userRoleModel;

    @ApiModelProperty(value = "The date and time at which the user was invited to the service.")
    @Expose
    private Date inviteDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRoleModel getUserRole() {
        return userRoleModel;
    }

    public void setUserRole(UserRoleModel userRoleModel) {
        this.userRoleModel = userRoleModel;
    }

    public Date getInviteDate() {
        return inviteDate;
    }

    public void setInviteDate(Date inviteDate) {
        this.inviteDate = inviteDate;
    }
}
