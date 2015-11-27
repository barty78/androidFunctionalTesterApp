
package server.pojos;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name = "Devices")
public class Device extends Model {
	
	 public Device() {
         super();
	 }

    @Column(name ="DeviceId")
    @SerializedName("id")
    @Expose
    private long deviceId;
    
    @Column(name ="JobId")
    @SerializedName("job_id")
    @Expose
    private long jobId;
    
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		result = prime * result + ((fwver == null) ? 0 : fwver.hashCode());
		result = prime * result + (int) (deviceId ^ (deviceId >>> 32));
		result = prime * result + (int) (jobId ^ (jobId >>> 32));
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((serial == null) ? 0 : serial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		if (fwver == null) {
			if (other.fwver != null)
				return false;
		} else if (!fwver.equals(other.fwver))
			return false;
		if (deviceId != other.deviceId)
			return false;
		if (jobId != other.jobId)
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (serial == null) {
			if (other.serial != null)
				return false;
		} else if (!serial.equals(other.serial))
			return false;
		return true;
	}

}
