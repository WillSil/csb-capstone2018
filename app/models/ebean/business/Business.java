package models.ebean.business;

import com.google.gson.annotations.Expose;
import io.ebean.annotation.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Corey Caplan on 10/9/17.
 */
@ApiModel(description = "A business with which the administrator has worked with or is actively working with.")
@Table(name = "client_business")
@Entity
public class Business extends CsbBaseModel {

    @ApiModelProperty(value = "The business\'s unique ID in the database")
    @Id
    @Expose
    private long businessId;

    @ApiModelProperty(value = "The business\'s name")
    @NotNull
    @Expose
    private String businessName;

    @ApiModelProperty(value = "The business\'s primary email address")
    @Expose
    private String businessEmail;

    @ApiModelProperty(value = "The business\'s primary phone number")
    @Expose
    private String businessPhoneNumber;

    @ApiModelProperty(value = "The business\'s primary address")
    @Expose
    private String businessAddress;

    @ApiModelProperty(value = "The business\'s industry")
    @Expose
    private String businessIndustry;

    @ApiModelProperty(value = "The clients that have contacted the admin on behalf of this business")
    @Expose
    @OneToMany(cascade = CascadeType.ALL)
    private List<Client> clientList;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Engagement> engagementList;

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getBusinessIndustry() {
        return businessIndustry;
    }

    public void setBusinessIndustry(String businessIndustry) {
        this.businessIndustry = businessIndustry;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }
}
