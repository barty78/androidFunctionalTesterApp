
package server.pojos.records;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

//@Table(name = "TEST")
public class Test  {

    public Test() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    //@Column(name = "IDTest")
	@Expose
    private List<Long> IDTest = new ArrayList<Long>();
    
    //@Column(name = "Value")
    @Expose
    private List<Double> Value = new ArrayList<Double>();
    
    //@Column(name = "Result")
    @Expose
    private List<Long> Result = new ArrayList<Long>();

	//@Column(name = "ErrorCode")
	@Expose
	private List<Long> ErrorCode = new ArrayList<Long>();

    //@Column(name = "Readings")
    private Readings foreignkey;
    
    @SuppressWarnings("unused")
	public Readings getForeignkey() {
		return foreignkey;
	}

	public void setForeignkey(Readings foreignkey) {
		this.foreignkey = foreignkey;
	}

	/**
     * 
     * @return
     *     The IDTest
     */
    public List<Long> getIDTest() {
        return IDTest;
    }

    /**
     * 
     * @param IDTest
     *     The IDTest
     */
    public void setIDTest(List<Long> IDTest) {
        this.IDTest = IDTest;
    }

    /**
     * 
     * @return
     *     The Value
     */
    public List<Double> getValue() {
        return Value;
    }

    /**
     * 
     * @param Value
     *     The Value
     */
    public void setValue(List<Double> Value) {
        this.Value = Value;
    }

    /**
     * 
     * @return
     *     The Result
     */
    public List<Long> getResult() {
        return Result;
    }

    /**
     * 
     * @param Result
     *     The Result
     */
    public void setResult(List<Long> Result) {
        this.Result = Result;
    }

	/**
	 *
	 * @return
	 *     The ErrorCode
	 */
	public List<Long> getErrorCode() { return ErrorCode; }

	/**
	 *
	 * @param ErrorCode
	 *     The ErrorCode
	 */
	public void setErrorCode(List<Long> ErrorCode) {
		this.ErrorCode = ErrorCode;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDTest == null) ? 0 : IDTest.hashCode());
		result = prime * result + ((Result == null) ? 0 : Result.hashCode());
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
		result = prime * result + ((ErrorCode == null) ? 0 : ErrorCode.hashCode());
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
		Test other = (Test) obj;
		if (IDTest == null) {
			if (other.IDTest != null)
				return false;
		} else if (!IDTest.equals(other.IDTest))
			return false;
		if (Result == null) {
			if (other.Result != null)
				return false;
		} else if (!Result.equals(other.Result))
			return false;
		if (Value == null) {
			if (other.Value != null)
				return false;
		} else if (!Value.equals(other.Value))
			return false;
		if (ErrorCode == null) {
			if (other.ErrorCode != null)
				return false;
		} else if (!Result.equals(other.ErrorCode))
			return false;
		return true;
	}
}
