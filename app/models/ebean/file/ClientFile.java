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
@ApiModel(description = "A file that is tied to a client of a business")
@Table(name = "client_business_files")
@Entity
public class ClientFile extends CsbBaseModel {

    @ApiModelProperty(value = "The unique ID associated with the file in the database")
    @Id
    @Expose
    private String fileId;

    @ApiModelProperty(value = "The file properties associated with this client")
    @OneToOne(cascade = CascadeType.ALL)
    @Expose
    private UploadedFile file;

    @ApiModelProperty(value = "The business ID associated with the file in the database")
    @Expose
    private int businessId;

    @ApiModelProperty(value = "The client ID associated with the file in the database")
    @Expose
    private long clientId;

    @ApiModelProperty(value = "The role required by the user to access this file")
    @ManyToOne(optional = false)
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

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public UserRoleModel getUserRoleModel() {
        return userRoleModel;
    }

    public void setUserRoleModel(UserRoleModel userRoleModel) {
        this.userRoleModel = userRoleModel;
    }
}
