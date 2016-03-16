
package server.pojos.records;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
//@Table(name = "TestRecord")
public class TestRecord {

    private long ID;

    public TestRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

    //@Column(name = "uploaded")
    //@Expose NB: not needed anymore
    private boolean uploaded=false;

    //@Column(name = "JobNo")
	//@Expose NB: not needed anymore
    private long JobNo;
    
    //@Expose not needed anymore
    //@Column(name = "FixtureNo")
    private String FixtureNo;
    
    @Expose
    //@Column(name = "Barcode")
    private long Barcode;
    
    @Expose
    //@Column(name = "Serial")
    private String Serial;
    
    @Expose
    //@Column(name = "Model")
    private long Model;
    
    //@Column(name = "FWVer")
    @Expose
    private String FWVer;
    
    @Expose
    //@Column(name = "StartedAt")
    private String StartedAt;
    
    @Expose
    //@Column(name = "Duration")
    private String Duration;
    
    @Expose
    //@Column(name = "Readings")
    private Readings Readings;
    
    //@Column(name = "Result")
    @Expose
    private long Result;

    //@Column(name = "BT_Addr")
    @Expose
    private String BT_Addr;

    public long getJobNo() {
        return JobNo;
    }

    public void setJobNo(long JobNo) {
        this.JobNo = JobNo;
    }

    public String getFixtureNo() {
        return FixtureNo;
    }

    public void setFixtureNo(String FixtureNo) {
        this.FixtureNo = FixtureNo;
    }

    public long getBarcode() {
        return Barcode;
    }

    public void setBarcode(long Barcode) {
        this.Barcode = Barcode;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String Serial) {
        this.Serial = Serial;
    }

    public long getModel() {
        return Model;
    }

    public void setModel(long Model) {
        this.Model = Model;
    }

    public String getFWVer() {
        return FWVer;
    }

    public void setFWVer(String FWVer) {
        this.FWVer = FWVer;
    }

    public String getStartedAt() {
        return StartedAt;
    }

    public void setStartedAt(String StartedAt) {
        this.StartedAt = StartedAt;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String Duration) {
        this.Duration = Duration;
    }

    public Readings getReadings() {
        return Readings;
    }

    public void setReadings(Readings Readings) {
        this.Readings = Readings;
    }

    public long getResult() {
        return Result;
    }

    public void setResult(long Result) {
        this.Result = Result;
    }

    public String getBT_Addr() {
        return BT_Addr;
    }

    public void setBT_Addr(String BT_Addr) {
        this.BT_Addr = BT_Addr;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (Barcode ^ (Barcode >>> 32));
		result = prime * result
				+ ((Duration == null) ? 0 : Duration.hashCode());
		result = prime * result + ((FWVer == null) ? 0 : FWVer.hashCode());
		result = prime * result
				+ ((FixtureNo == null) ? 0 : FixtureNo.hashCode());
		result = prime * result + (int) (JobNo ^ (JobNo >>> 32));
		result = prime * result + (int) (Model ^ (Model >>> 32));
		result = prime * result
				+ ((Readings == null) ? 0 : Readings.hashCode());
		result = prime * result + (int) (Result ^ (Result >>> 32));
		result = prime * result + ((Serial == null) ? 0 : Serial.hashCode());
		result = prime * result
				+ ((StartedAt == null) ? 0 : StartedAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestRecord other = (TestRecord) obj;
		if (Barcode != other.Barcode)
			return false;
		if (Duration == null) {
			if (other.Duration != null)
				return false;
		} else if (!Duration.equals(other.Duration))
			return false;
		if (FWVer == null) {
			if (other.FWVer != null)
				return false;
		} else if (!FWVer.equals(other.FWVer))
			return false;
		if (FixtureNo == null) {
			if (other.FixtureNo != null)
				return false;
		} else if (!FixtureNo.equals(other.FixtureNo))
			return false;
		if (JobNo != other.JobNo)
			return false;
		if (Model != other.Model)
			return false;
		if (Readings == null) {
			if (other.Readings != null)
				return false;
		} else if (!Readings.equals(other.Readings))
			return false;
		if (Result != other.Result)
			return false;
		if (Serial == null) {
			if (other.Serial != null)
				return false;
		} else if (!Serial.equals(other.Serial))
			return false;
		if (StartedAt == null) {
			if (other.StartedAt != null)
				return false;
		} else if (!StartedAt.equals(other.StartedAt))
			return false;
		return true;
	}

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getID() {
        return ID;
    }
}
