package models.ebean.sync;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.business.Business;
import models.ebean.business.Client;

import javax.persistence.*;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@ApiModel(description = "The pieces of a sync that occurred with the admin\'s email. There can be many components " +
        "that tie to a single EmailSyncModel.")
@Table(name = "administrator_email_sync_business_clients")
@Entity
public class AdministratorEmailSyncComponent extends CsbBaseModel {

    @ApiModelProperty(value = "The unique parent ID of a given set of contact statuses.")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Expose
    private int businessClientSyncId;

    @ApiModelProperty(value = "The sync model that is associated with this admin email sync.")
    @ManyToOne(cascade = CascadeType.ALL)
    @Expose
    private EmailSyncModel syncModel;

    @ApiModelProperty(value = "A client that is associated with this admin sync.")
    @ManyToOne(cascade = CascadeType.ALL)
    @Expose
    private Client client;

    @ApiModelProperty(value = "A business that is associated with this admin sync.")
    @ManyToOne(cascade = CascadeType.ALL)
    @Expose
    private Business business;

    public int getBusinessClientSyncId() {
        return businessClientSyncId;
    }

    public void setBusinessClientSyncId(int businessClientSyncId) {
        this.businessClientSyncId = businessClientSyncId;
    }

    public EmailSyncModel getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(EmailSyncModel syncModel) {
        this.syncModel = syncModel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

}
