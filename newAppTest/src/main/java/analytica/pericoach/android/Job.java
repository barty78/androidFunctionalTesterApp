package analytica.pericoach.android;

public class Job {
	
	private long id;
	private Integer test_id;
	private String date;
	private String jobNo;
	private Integer totalqty;
	private Integer testedqty;
	private Integer passedqty;
	private String lastUpdated;
	private Integer lastReportedRecord;
	private Integer lastReportNumber;
	private Integer active;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Integer getTestID() {
		return test_id;
	}
	
	public void setTestID(Integer testID) {
		this.test_id = testID;
	}
	
	public String getJobNo() {
		return jobNo;
	}
	
	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}
	
	public Integer getTotalQty() {
		return totalqty;
	}
	
	public void setTotalQty(Integer totalQty) {
		this.totalqty = totalQty;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean isActive() {
		if (active == 1) {
			return true;
		}
		return false;
	}
	
	public void setActive(Integer active) {
		this.active = active;
	}
	
	public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Integer getLastReportedRecord() {
    	return lastReportedRecord;
    }
    
    public void setLastReportedRecord(Integer lastReportedRecord) {
    	this.lastReportedRecord = lastReportedRecord;
    }
    
    public Integer getLastReportNumber() {
    	return lastReportNumber;
    }
    
    public void setLastReportNumber(Integer lastReportNumber) {
    	this.lastReportNumber = lastReportNumber;
    }
    
    public Integer getTestedQty() {
        return testedqty;
    }

    public void setTestedQty(Integer testedQty) {
        this.testedqty = testedQty;
    }
	
    public Integer getPassedQty() {
        return passedqty;
    }

    public void setPassedQty(Integer passedQty) {
        this.passedqty = passedQty;
    }
}