package models.ebean.user;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Corey Caplan on 10/9/17.
 */
@ApiModel(description = "Settings that a user sets and are tied to their account and adjust how this service " +
        "function for them.")
@Table(name = "user_settings")
@Entity
public class UserSettings extends CsbBaseModel {

    @ApiModelProperty(value = "The unique setting, not UI friendly.")
    @Id
    @Expose
    private String settingsType;

    @ApiModelProperty(value = "The ID of the user that is tied to this user.")
    @Expose
    private String userId;

    @ApiModelProperty(value = "A numeric value associated with this setting.", allowEmptyValue = true)
    @Column(name = "settings_numeric_value")
    @Expose
    private Double numericValue;

    @ApiModelProperty(value = "A string value associated with this setting.", allowEmptyValue = true)
    @Column(name = "settings_string_value")
    @Expose
    private String stringValue;

    @ApiModelProperty(value = "A date value associated with this setting.", allowEmptyValue = true)
    @Column(name = "settings_date_value")
    @Expose
    private Date dateValue;

    public String getSettingsType() {
        return settingsType;
    }

    public void setSettingsType(String settingsType) {
        this.settingsType = settingsType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(Double numericValue) {
        this.numericValue = numericValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

}
