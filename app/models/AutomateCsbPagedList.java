package models;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Corey Caplan on 10/25/17.
 */
public class AutomateCsbPagedList<T> {

    @ApiModelProperty(value = "The amount of items in the current row.")
    @Expose
    private final int rowCount;

    @ApiModelProperty(value = "The 0-indexed page that the user is currently on.")
    @Expose
    private final int currentPage;

    @ApiModelProperty(value = "The total number of items that are in the result set.")
    @Expose
    private final int totalItemCount;

    @ApiModelProperty(value = "The data that is contained in this list of items.")
    @Expose
    private final List<T> data;

    public AutomateCsbPagedList(int rowCount, int currentPage, int totalItemCount, List<T> data) {
        this.rowCount = rowCount;
        this.currentPage = currentPage;
        this.totalItemCount = totalItemCount;
        this.data = data;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public List<T> getData() {
        return data;
    }
}
