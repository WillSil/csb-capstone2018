package models.ebean.file;

import io.ebean.annotation.Expose;
import io.ebean.annotation.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.ebean.CsbBaseModel;

import javax.persistence.*;

/**
 * Created by Corey Caplan on 10/10/17.
 */
@ApiModel(description = "A file that has been uploaded to the system (either manually or via email scraping).")
@Table(name = "uploaded_file")
@Entity
public class UploadedFile extends CsbBaseModel {

    @ApiModelProperty(hidden = true)
    @Id
    @Expose
    private String fileId;

    @ApiModelProperty(value = "The file\'s name in the database")
    @NotNull
    @Expose
    private String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
