package server.pojos.records;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Sensor {

	public Sensor() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Expose
	//@Column(name = "IDTest")
    private List<Long> IDTest = new ArrayList<Long>();
    
	@Expose
	//@Column(name = "Min")
	private List<Long> Min = new ArrayList<Long>();
	
    @Expose
    //@Column(name = "Max")
    private List<Long> Max = new ArrayList<Long>();
    
    @Expose
    //@Column(name = "Avg")
    private List<Long> Avg = new ArrayList<Long>();
    
    @Expose
    //@Column(name = "Result")
    private List<Long> Result = new ArrayList<Long>();

	@Expose
	//@Column(name = "ErrorCodes")
	private List<Long> errorCodes= new ArrayList<>();

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
     *     The Min
     */
    public List<Long> getMin() {
        return Min;
    }

    /**
     * 
     * @param Min
     *     The Min
     */
    public void setMin(List<Long> Min) {
        this.Min = Min;
    }

    /**
     * 
     * @return
     *     The Max
     */
    public List<Long> getMax() {
        return Max;
    }

    /**
     * 
     * @param Max
     *     The Max
     */
    public void setMax(List<Long> Max) {
        this.Max = Max;
    }

    /**
     * 
     * @return
     *     The Avg
     */
    public List<Long> getAvg() {
        return Avg;
    }

    /**
     * 
     * @param Avg
     *     The Avg
     */
    public void setAvg(List<Long> Avg) {
        this.Avg = Avg;
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
		Sensor other = (Sensor) obj;
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

	public void setErrorCodes(List<Long> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public List<Long> getErrorCodes() {
		return errorCodes;
	}
}