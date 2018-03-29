package models.ebean.business;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.file.ClientFile;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Corey Caplan on 10/9/17.
 */
@Table(name = "client")
@Entity
public class Client extends CsbBaseModel {

    @ApiModelProperty(value = "The client\'s unique ID in the database")
    @Id
    @Expose
    private long clientId;

    @ApiModelProperty(value = "The client\'s full name")
    @Expose
    private String clientName;

    @ApiModelProperty(value = "The client\'s primary email address")
    @Expose
    private String clientEmail;

    @ApiModelProperty(value = "The client\'s primary phone number")
    @Expose
    private String clientPhone;

    @ApiModelProperty(value = "The notes for which the admin set for this client.")
    @Expose
    private String clientNotes;

    @ManyToOne
    private Business business;

    @ApiModelProperty(value = "The list of files tied to this client unique ID in the database. Never null but may be empty")
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<ClientFile> clientFileList;

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientNotes() {
        return clientNotes;
    }

    public void setClientNotes(String clientNotes) {
        this.clientNotes = clientNotes;
    }

    public List<ClientFile> getClientFileList() {
        return clientFileList;
    }

    public void setClientFileList(List<ClientFile> clientFileList) {
        this.clientFileList = clientFileList;
    }
}
