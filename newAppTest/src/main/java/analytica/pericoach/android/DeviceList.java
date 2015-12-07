package analytica.pericoach.android;

import java.sql.Date;

public class DeviceList {
	
	private String devId;
	private String serial;
	private String firmwareVersion;

	private Integer job_id;
	
	private String date;
	
	
	public Integer getJobId() {
		return job_id;
	}
	
	public void setJobId(Integer job_id) {
		this.job_id = job_id;
	}
	
	public String getDate() {
		return date;
	}
	/*
	public long getDateEpoch(){
		return date.getTime() / 1000;
	}
	
	public void setDateEpoch(long seconds){
		date = new Date(seconds * 1000);
	}
*/
	public void setDate(String date) {
		this.date = date;
	}

	public String getSerial() {
		return serial;
	}
	
	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public String getDevId() {
		return devId;
	}
	
	public void setDevId(String devId) {
		this.devId = devId;
	}
	
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	
	@Override
	public String toString(){
		String s = "";
		
		s = "DEV_ID:" + devId;
		//+ " - JOB_ID:" + Integer.toString(job_id);
		
		return s;
	}
	
}
