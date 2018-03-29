package models.ebean.sync;

import io.ebean.annotation.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.*;

/**
 * Created by Corey Caplan on 10/24/17.
 */
@ApiModel(description = "A class that represents the standard properties of an admin email sync.")
@Table(name = "administrator_email_sync")
@Entity
public class EmailSyncModel extends CsbBaseModel {

    @ApiModelProperty(value = "The unique ID of a given sync.")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Expose
    private int syncId;

    public int getSyncId() {
        return syncId;
    }

    public void setSyncId(int syncId) {
        this.syncId = syncId;
    }
}
