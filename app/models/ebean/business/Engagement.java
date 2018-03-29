package models.ebean.business;

import com.google.gson.annotations.Expose;
import io.ebean.annotation.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.file.EngagementFile;
import models.ebean.status.ChildStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@ApiModel(description = Engagement.KEY_ENGAGEMENT_DESCRIPTION)
@Table(name = "engages")
@Entity
public class Engagement extends CsbBaseModel {

    static final String KEY_ENGAGEMENT_DESCRIPTION = "An engagement that occurs between the admin and a business. " +
            "A business can have many engagements associated with it, with some being active, and some being " +
            "inactive. Engagements can be thought of any active talking, negotiating, or emailing between the admin " +
            "and the business.";

    @ApiModelProperty(value = "The engagement\'s unique ID in the database")
    @Id
    @Expose
    private long engagementId;

    @ApiModelProperty(value = "The name of this engagement. This does not have a unique constraint, meaning there " +
            "may be duplicates.")
    @Expose
    private String engagementName;

    @ApiModelProperty(value = "True if the engagement is active, false otherwise. Indicates whether or not the " +
            "admin is still actively attending to this engagement. False could indicate that this engagement ended " +
            "abruptly or occurred in the past.")
    @Expose
    private boolean isActive;

    @ApiModelProperty(value = "The business tied to this engagement")
    @JoinColumn(name = "business_id")
    @ManyToOne
    @Expose
    private Business business;

    @ApiModelProperty(value = "The status that is currently are tied to this engagement. It classifies at what point it\'s at in the process")
    @Expose
    @ManyToOne
    private ChildStatus currentStateType;

    @ApiModelProperty(value = "Any miscellaneous notes that are tied to this engagement")
    @Column(columnDefinition = "TEXT")
    @Expose
    private String engagementNotes;

    @ApiModelProperty(value = "The date at which this engagement started")
    @NotNull
    @Expose
    private Date dateStarted;

    @ApiModelProperty(value = "The date at which the engagement ended. Can be null if the engagement is active")
    @Expose
    private Date dateEnded;

    @ApiModelProperty(value = "The date at which the project is set to be implemented. With this date, we are only " +
            "concerned with the year.")
    @Expose
    private Date dateImplemented;

    @ApiModelProperty(value = "The date at which the status for this engagement was last updated")
    @Expose
    private Date dateStatusLastUpdated;

    @ApiModelProperty(value = "The files that are tied to this engagement. Can be empty but never null")
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<EngagementFile> engagementFiles;

    public long getEngagementId() {
        return engagementId;
    }

    public void setEngagementId(long engagementId) {
        this.engagementId = engagementId;
    }

    public String getEngagementName() {
        return engagementName;
    }

    public void setEngagementName(String engagementName) {
        this.engagementName = engagementName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public ChildStatus getCurrentStateType() {
        return currentStateType;
    }

    public void setCurrentStateType(ChildStatus currentStateType) {
        this.currentStateType = currentStateType;
    }

    public String getEngagementNotes() {
        return engagementNotes;
    }

    public void setEngagementNotes(String engagementNotes) {
        this.engagementNotes = engagementNotes;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public Date getDateEnded() {
        return dateEnded;
    }

    public void setDateEnded(Date dateEnded) {
        this.dateEnded = dateEnded;
    }

    public Date getDateImplemented() {
        return dateImplemented;
    }

    public void setDateImplemented(Date dateImplemented) {
        this.dateImplemented = dateImplemented;
    }

    public Date getDateStatusLastUpdated() {
        return dateStatusLastUpdated;
    }

    public void setDateStatusLastUpdated(Date dateStatusLastUpdated) {
        this.dateStatusLastUpdated = dateStatusLastUpdated;
    }

    public List<EngagementFile> getEngagementFiles() {
        return engagementFiles;
    }

    public void setEngagementFiles(List<EngagementFile> engagementFiles) {
        this.engagementFiles = engagementFiles;
    }
}
