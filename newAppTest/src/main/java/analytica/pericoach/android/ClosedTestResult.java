package analytica.pericoach.android;

import hydrix.pfmat.generic.Device;
import hydrix.pfmat.generic.Result;
import hydrix.pfmat.generic.TEST;
import hydrix.pfmat.generic.TestSamples;

import java.util.ArrayList;

import android.util.Log;

@SuppressWarnings("unused")
public class ClosedTestResult {
	
	private Device.Information mdeviceInfo;
	
	private Integer resultRecord_id;
	private Integer job_id;
	private Integer dev_id;
	private Integer test_id;

	private TestSamples samples;
	
	private Result mResult;
	private String operator;
	private String date;
	
	private Boolean mComplete;


	public Boolean isComplete() {
		return mComplete;
	}
	
	public void setComplete(Boolean complete) {
		this.mComplete = complete;
	}
	
	public Device.Information getDeviceInfo() {
		return mdeviceInfo;
	}
	
	public Integer getResultRecordID() {
		return resultRecord_id;
	}
	
	public void setResultRecordID(Integer record_id) {
		this.resultRecord_id = record_id;
	}
	
	public Integer getJobId() {
		return job_id;
	}
	
	public void setJobId(Integer job_id) {
		this.job_id = job_id;
	}
	
	public Integer getDevId() {
		return dev_id;
	}
	
	public void setDevId(Integer dev_id) {
		this.dev_id = dev_id;
	}
	
	public Integer getTestId() {
		return test_id;
	}
	
	public void setTestId(Integer test_id) {
		this.test_id = test_id;
	}
	
	public TestSamples getSamples() {
		return samples;
	}
	
	public void setSamples(TestSamples samples) {
		this.samples = samples;
	}
	
	public Result getResult() {
		return mResult;
	}
	
	public void setResult(Result result) {
		this.mResult = result;
	}
	
	public void setFinalResult() {
		this.mResult = new Result(TEST.PASS);
		for (int i = 0; i < samples.getResults().size(); i++) {
			//Log.d("RESULT - " + i + ":", samples.getResults().get(i));
			if (samples.getResults().get(i).isFail()) {
				this.mResult = new Result(TEST.FAIL);
			}
		}
	}
		
	public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		String s;
		
		s = "ID:" + Integer.toString(resultRecord_id) + " - JOB_ID:" + Integer.toString(job_id)
				+ " - DEV_ID:" + Integer.toString(dev_id) + " - TEST_ID:" + Integer.toString(test_id);
		
		return s;
	}
    
	public void removeLast(){
		if(samples!=null)samples.removelast();
	}
 }
