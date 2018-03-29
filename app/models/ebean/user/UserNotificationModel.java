package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Corey Caplan on 10/24/17.
 */
@ApiModel(description = "The user notification model, containing general miscellaneous information about the notification.")
@Table(name = "user_notification_type")
@Entity
public class UserNotificationModel extends CsbBaseModel {

    @ApiModelProperty(value = "The type of notification this user\'s notification is.")
    @Id
    @Expose
    private int notificationType;

    @ApiModelProperty(value = "A UI-friendly notification tied to the notification that can be shown in the UI.")
    @Expose
    private String notificationDescription;

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }
}
