package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.file.ClientFile;
import models.ebean.file.EngagementFile;
import models.ebean.invite.InvitedUser;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Corey Caplan on 10/24/17.
 */
@ApiModel(description = "The general user role model that is tied to a user\'s role.")
@Table(name = "user_role_model")
@Entity
public class UserRoleModel extends CsbBaseModel {

    @ApiModelProperty(value = "The role type that this user has. Can be \"fiscal_manager\", \"legal_contractor\", \"administrator\".")
    @Id
    @Column(name = "user_role_type")
    @Expose
    private String roleType;

    @OneToMany(mappedBy = "userRoleModel")
    private List<UserRole> userRole;

    @OneToMany(mappedBy = "userRoleModel")
    private List<ClientFile> clientFile;

    @OneToMany(mappedBy = "userRoleModel")
    private List<EngagementFile> engagementFile;

    @OneToMany(mappedBy = "userRoleModel")
    private List<InvitedUser> invitedUser;

    @ApiModelProperty(value = "The UI-friendly text associated with this role that is safe to display in the UI.")
    @Column(name = "user_role_description")
    @Expose
    private String roleDescription;

    @ApiModelProperty(value = "A numeric value associated with this user role. A higher number indicates the " +
            "user has more privileges.")
    @Column(name = "user_role_priority")
    @Expose
    private int rolePriority;

    public UserRoleModel() {
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public int getRolePriority() {
        return rolePriority;
    }

    public void setRolePriority(int rolePriority) {
        this.rolePriority = rolePriority;
    }
}
