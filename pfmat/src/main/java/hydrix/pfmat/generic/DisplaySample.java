package hydrix.pfmat.generic;

public class DisplaySample
{
	private String mTestDesc;
	private Force mUserMaxForce;
	private Force mUserMinForce;
	private Force mUserBaseline;
	private Result[] mTestResults;
	
	public DisplaySample (String testDesc, Force maxforce,
						Force minforce, Force avgforce, Result[] results) {
		mTestDesc = testDesc;
		mUserMaxForce = maxforce;
		mUserMinForce = minforce;
		mUserBaseline = avgforce;
		mTestResults = results;
	}
	
	public String getTestDesc() {
		return mTestDesc;
	}
	
	public void setTestDesc(String testdesc) {
		this.mTestDesc = testdesc;
	}
	
	public Force getMaxForce() {
		return mUserMaxForce;
	}
	
	public void setMaxForce(Force maxforce) {
		this.mUserMaxForce = maxforce;
	}
	
	public Force getMinForce() {
		return mUserMinForce;
	}
	
	public void setMinForce(Force minforce) {
		this.mUserMinForce = minforce;
	}
	
	public Force getAvgForce() {
		return mUserBaseline;
	}
	
	public void setAvgForce(Force avgforce) {
		this.mUserBaseline = avgforce;
	}
	
	public void setResults(Result[] result) {
		this.mTestResults = result;
	}
	
	public Result[] getResults() {
		return mTestResults;
	}
}