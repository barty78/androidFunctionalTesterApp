
package server.pojos;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
@Table(name = "Devices")
public class Device extends Model {
	
	 public Device() {
         super();
	 }

    @Column(name ="DeviceId")
    @SerializedName("id")
    @Expose
    private long deviceId;
    
    @Column(name ="Barcode", index=true)
    @Expose
    private String barcode;

    @Column(name ="Serial", index=true)
    @Expose
    private String serial;

    @Column(name ="Model")
    @Expose
    private String model;

    @Column(name ="Fwver")
    @Expose
    private String fwver;

    @Column(name ="bt_addr")
    @Expose
    private String bt_addr;

    @Column(name ="passed")
    @Expose
    private int passed;

//    I've also added two new fields to devices...  Executed_Tests and Status..  Let me explain their use..

    @Column(name ="executed_tests")
    @Expose
    private long executed_tests;



    @Column(name ="status")
    @Expose
    private long status;

    /**
     *
     * @return
     *     The id
     */
    public long getDeviceId() {
        return deviceId;
    }
    /**
     *
     * @param id
     *     The id
     */
    public void setDeviceId(long id) {
        this.deviceId = id;
    }

    /**
     *
     * @return
     *     The jobId
     */
    public long getJobId() {
        return jobId;
    }

    /**
     *
     * @param jobId
     *     The job_id
     */
    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    /**
     *
     * @return
     *     The barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     *
     * @param barcode
     *     The barcode
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    //    The web service has already been updated to handle setting these fields, so nothing needs changing in the app to set the fields..  Web service uses the result and sets executed_tests and status accordingly.  You just need to use the fields in the devices list to display.
//    Executed tests identifies which tests the device has actually been run with.  Status identifies if test have passed or failed..  Since a device will go through more than one test sequence during production, we needed to be able to determine if a device has actually been through a test type, and if it passed/failed.
//    Both are bitmasked against testtype (ie, open test = 1, closed test = 2, other tests would be 4/8/16 etc.)..
    @Column(name ="JobId")
    @SerializedName("job_id")
    @Expose
    private long jobId;

    /**
     * 
     * @return
     *     The serial
     */
    public String getSerial() {
        return serial;
    }

    /**
     * 
     * @param serial
     *     The serial
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * 
     * @return
     *     The model
     */
    public String getModel() {
        return model;
    }

    /**
     * 
     * @param model
     *     The model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 
     * @return
     *     The fwver
     */
    public String getFwver() {
        return fwver;
    }

    /**
     * 
     * @param fwver
     *     The fwver
     */
    public void setFwver(String fwver) {
        this.fwver = fwver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Device device = (Device) o;

        return barcode.equals(device.barcode);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + barcode.hashCode();
        return result;
    }

    public String getBt_addr() {
        return bt_addr;
    }

    public void setBt_addr(String bt_addr) {
        this.bt_addr = bt_addr;
    }

    public long getExec_Tests() {
        return executed_tests;
    }

    public void setExec_Tests(long exec_tests) {
        this.executed_tests = exec_tests;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }



}
