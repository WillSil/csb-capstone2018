package models.ebean.status;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;
import models.ebean.business.Engagement;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@ApiModel(description = ChildStatus.KEY_DESCRIPTION)
@Table(name = "business_status_child_model")
@Entity
public class ChildStatus extends CsbBaseModel {

    static final String KEY_DESCRIPTION = "A status that acts as a child of a parent. Meaning, it will be descendant " +
            "of a parent in a dropdown list.";

    @ApiModelProperty(value = "The parent of this child status.")
    @Expose
    private String statusParentType;

    @ApiModelProperty(value = "The unique child status that is used as a classification mechanism.")
    @Id
    @Expose
    private String statusChildType;

    @ApiModelProperty(value = "The UI-friendly version of this child status that can be (safely) set in the UI.")
    @Expose
    private String statusChildDescription;

    @ApiModelProperty(hidden = true)
    @Expose
    private int childOrderNumber;

    @OneToMany(mappedBy = "currentStateType")
    private List<Engagement> engagementList;

    public String getStatusParentType() {
        return statusParentType;
    }

    public void setStatusParentType(String statusParentType) {
        this.statusParentType = statusParentType;
    }

    public String getStatusChildType() {
        return statusChildType;
    }

    public void setStatusChildType(String statusChildType) {
        this.statusChildType = statusChildType;
    }

    public String getStatusChildDescription() {
        return statusChildDescription;
    }

    public void setStatusChildDescription(String statusChildDescription) {
        this.statusChildDescription = statusChildDescription;
    }

    public int getChildOrderNumber() {
        return childOrderNumber;
    }

    public void setChildOrderNumber(int childOrderNumber) {
        this.childOrderNumber = childOrderNumber;
    }
}
