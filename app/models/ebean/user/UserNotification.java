package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.business.Business;
import models.ebean.business.Engagement;
import models.ebean.sync.AdministratorEmailSyncComponent;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by Corey Caplan on 10/17/17.
 */
@ApiModel(description = "A notification that is tied to a user of the service")
@Table(name = "user_notification")
@Entity
public class UserNotification extends CsbBaseModel {

    @ApiModelProperty(value = "The unique ID of this user\'s notification in the database.")
    @Id
    @Expose
    private String notificationId;

    @ApiModelProperty(value = "The unique ID of this user in the database.")
    @Expose
    private String userId;

    @ApiModelProperty(value = "The message tied to this notification. It contains raw text that is HTML escaped.")
    @Expose
    private String notificationMessage;

    @ApiModelProperty(value = "The date at which the notification occurred")
    @Expose
    private Date notificationDate;

    @ApiModelProperty(value = "The business tied to this notification, can be null.")
    @ManyToOne
    @Nullable
    @Expose
    private Business business;

    @ApiModelProperty(value = "The engagement tied to this notification, can be null.")
    @ManyToOne
    @Nullable
    @Expose
    private Engagement engagement;


    @ApiModelProperty(value = "True if the user has seen this notification, or false otherwise.")
    @Expose
    private boolean isSeen;

    @ApiModelProperty(value = "The model-type of notification that is tied to this user notification.")
    @ManyToOne
    @Expose
    private UserNotificationModel notificationType;

    @ApiModelProperty(value = "The admin email sync that is tied to this notification. Can be null")
    @ManyToOne
    @Nullable
    @Expose
    private AdministratorEmailSyncComponent emailSync;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Nullable
    public Business getBusiness() {
        return business;
    }

    public void setBusiness(@Nullable Business business) {
        this.business = business;
    }

    @Nullable
    public Engagement getEngagement() {
        return engagement;
    }

    public void setEngagement(@Nullable Engagement engagement) {
        this.engagement = engagement;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public UserNotificationModel getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(UserNotificationModel notificationType) {
        this.notificationType = notificationType;
    }

    @Nullable
    public AdministratorEmailSyncComponent getEmailSync() {
        return emailSync;
    }

    public void setEmailSync(@Nullable AdministratorEmailSyncComponent emailSync) {
        this.emailSync = emailSync;
    }


}
