package models.ebean.file;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.user.UserRoleModel;

import javax.persistence.*;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@ApiModel(description = "A file that is tied to an engagement with a business")
@Table(name = "engages_file")
@Entity
public class EngagementFile extends CsbBaseModel {

    @ApiModelProperty(value = "The unique ID associated with the file in the database")
    @Id
    @Expose
    private String fileId;

    @ApiModelProperty(value = "The file properties associated with this engagement")
    @OneToOne
    @Expose
    private UploadedFile file;

    @ApiModelProperty(value = "The unique ID of the engagement associated with the database")
    @Expose
    private long engagementId;

    @ApiModelProperty(value = "The role required by the user to access this file")
    @ManyToOne
    @Expose
    private UserRoleModel userRoleModel;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public long getEngagementId() {
        return engagementId;
    }

    public void setEngagementId(long engagementId) {
        this.engagementId = engagementId;
    }

    public UserRoleModel getUserRoleModel() {
        return userRoleModel;
    }

    public void setUserRoleModel(UserRoleModel userRoleModel) {
        this.userRoleModel = userRoleModel;
    }
}
