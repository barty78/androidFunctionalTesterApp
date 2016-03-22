package hydrix.pfmat.generic;

public class TestLimits
{
	
	// Has a growing cost each time the array is grown (implementation dependent, but typically doubled each time according to most doco)
	// Shouldn't be an issue with data sizes in this application
	
	private String mDesc;
	private Integer mTestId;
	private Integer mSeqNo;
	private Force mLowerLimits;
	private Force mUpperLimits;
	private Integer mStability;
	private String mCreatedDate;
	private String mModifiedDate;
	
	public final void setCreatedDate(String createdDate) {
		this.mCreatedDate = createdDate;
	}
	
	public final String getCreatedDate() {
		return mCreatedDate;
	}
	
	public final void setModifiedDate(String modifiedDate) {
		this.mModifiedDate = modifiedDate;
	}
	
	public final String getModifiedDate() {
		return mModifiedDate;
	}
	
	public final void setDesc(String desc) {
		this.mDesc = desc;
	}
	
	public final String getDesc() {
		return mDesc;
	}
	
	public final Integer getTestID() {
		return mTestId;
	}
	
	public final void setTestID(Integer testID) {
		this.mTestId = testID;
	}
	
	public final void setSeqNo(Integer seqNo) {
		this.mSeqNo = seqNo;
	}
	
	public final Integer getSeqNo() {
		return mSeqNo;
	}
	
	public final void setLowerLimits(Force lowerLimits) {
		this.mLowerLimits = lowerLimits;
	}
	
	public final Force getLowerLimits() {
		return mLowerLimits;
	}
	
	public final void setUpperLimits(Force upperLimits) {
		this.mUpperLimits = upperLimits;
	}
	
	public final Force getUpperLimits() {
		return mUpperLimits;
	}
	
	public final void setStability(Integer stability) {
		this.mStability = stability;
	}
	
	public final Integer getStability() {
		return mStability;
	}
}