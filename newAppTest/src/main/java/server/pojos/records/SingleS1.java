package server.pojos.records;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "SingleS1")
public class SingleS1  {

	@Expose
	@Column(name = "IDTest")
    private Long IDTest;
    
	@Expose
	@Column(name = "Min")
	private Long Min;
	
    @Expose
    @Column(name = "Max")
    private Long Max;
    
    @Expose
    @Column(name = "Avg")
    private Long Avg;
    
    @Column(name = "S1")
    private S1 foreignkey;
    
    @Expose
    @Column(name = "Result")
    private Long Result;

	public Long getErrorCode() {
		return errorCode;
	}

	@Expose
	@Column(name = "ErrorCode")
	private Long errorCode;


	public Long getResult() {
		return Result;
	}

	public void setResult(Long result) {
		Result = result;
	}

	public Long getIDTest() {
		return IDTest;
	}

	public void setIDTest(Long iDTest) {
		IDTest = iDTest;
	}

	public Long getMin() {
		return Min;
	}

	public void setMin(Long min) {
		Min = min;
	}

	public Long getMax() {
		return Max;
	}

	public void setMax(Long max) {
		Max = max;
	}

	public Long getAvg() {
		return Avg;
	}

	public void setAvg(Long avg) {
		Avg = avg;
	}

	@SuppressWarnings("unused")
	public S1 getForeignkey() {
		return foreignkey;
	}

	public void setForeignkey(S1 foreignkey) {
		this.foreignkey = foreignkey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Avg == null) ? 0 : Avg.hashCode());
		result = prime * result + ((IDTest == null) ? 0 : IDTest.hashCode());
		result = prime * result + ((Max == null) ? 0 : Max.hashCode());
		result = prime * result + ((Min == null) ? 0 : Min.hashCode());
		result = prime * result + ((Result == null) ? 0 : Result.hashCode());
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
		SingleS1 other = (SingleS1) obj;
		if (Avg == null) {
			if (other.Avg != null)
				return false;
		} else if (!Avg.equals(other.Avg))
			return false;
		if (IDTest == null) {
			if (other.IDTest != null)
				return false;
		} else if (!IDTest.equals(other.IDTest))
			return false;
		if (Max == null) {
			if (other.Max != null)
				return false;
		} else if (!Max.equals(other.Max))
			return false;
		if (Min == null) {
			if (other.Min != null)
				return false;
		} else if (!Min.equals(other.Min))
			return false;
		if (Result == null) {
			if (other.Result != null)
				return false;
		} else if (!Result.equals(other.Result))
			return false;
		return true;
	}


	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}
}