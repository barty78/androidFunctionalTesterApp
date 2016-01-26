package server.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mauriziopietrantuono on 04/01/16.
 */
public class DevicesList {

    @SerializedName("New")
    @Expose
    private List<Device> New = new ArrayList<Device>();
    @SerializedName("Updated")
    @Expose
    private List<Device> Updated = new ArrayList<Device>();

    /**
     *
     * @return
     * The New
     */
    public List<Device> getNew() {
        return New;
    }

    /**
     *
     * @param New
     * The New
     */
    @SuppressWarnings("unused")
    public void setNew(List<Device> New) {
        this.New = New;
    }

    /**
     *
     * @return
     * The Updated
     */
    public List<Device> getUpdated() {
        return Updated;
    }

    /**
     *
     * @param Updated
     * The Updated
     */
    public void setUpdated(List<Device> Updated) {
        this.Updated = Updated;
    }

}