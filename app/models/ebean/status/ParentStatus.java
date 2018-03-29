package models.ebean.status;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Corey Caplan on 10/10/17.
 */

@ApiModel(value = "A parent of a given set of contact statuses, whose children are descendants in a dropdown list.")
@Table(name = "business_status_parent_model")
@Entity
public class ParentStatus extends CsbBaseModel {

    @ApiModelProperty(value = "The unique parent ID of a given set of contact statuses.")
    @Id
    @Expose
    private String statusParentType;

    @ApiModelProperty(value = "The unique parent ID of a given set of contact statuses.")
    @Expose
    private String statusParentDescription;

    @ApiModelProperty(value = "The phase number associated with this status. Could be between 1 to 5.",
            allowableValues = "1, 2, 3, 4, 5")
    @Expose
    private int phaseNumber;

    @ApiModelProperty(hidden = true)
    @Expose
    private int parentOrderNumber;

    @ApiModelProperty(value = "The list of child statuses that are associated with this parent status. They should " +
            "be displayed in a descendant list in a dropdown.")
    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private List<ChildStatus> childStatusList;

    public String getStatusParentType() {
        return statusParentType;
    }

    public void setStatusParentType(String statusParentType) {
        this.statusParentType = statusParentType;
    }

    public String getStatusParentDescription() {
        return statusParentDescription;
    }

    public void setStatusParentDescription(String statusParentDescription) {
        this.statusParentDescription = statusParentDescription;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public int getParentOrderNumber() {
        return parentOrderNumber;
    }

    public void setParentOrderNumber(int parentOrderNumber) {
        this.parentOrderNumber = parentOrderNumber;
    }

    public List<ChildStatus> getChildStatusList() {
        return childStatusList;
    }

    public void setChildStatusList(List<ChildStatus> childStatusList) {
        this.childStatusList = childStatusList;
    }
}
