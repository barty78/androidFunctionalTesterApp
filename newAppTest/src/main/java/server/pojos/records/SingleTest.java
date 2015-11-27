package server.pojos.records;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "SingleTest")
public class SingleTest extends MyModel {

	@Column(name = "IDTest")
	@Expose
	private Long IDTest;

	@Column(name = "Value")
	@Expose
	private Double Value;

	@Column(name = "Result")
	@Expose
	private Long Result;
	
	@Column(name = "Test")
	@Expose
	private Test foreignkey;

	public Long getIDTest() {
		return IDTest;
	}

	public void setIDTest(Long iDTest) {
		IDTest = iDTest;
	}

	public Double getValue() {
		return Value;
	}

	public void setValue(Double value) {
		Value = value;
	}

	public Long getResult() {
		return Result;
	}

	public void setResult(Long result) {
		Result = result;
	}

	public Test getForeignkey() {
		return foreignkey;
	}

	public void setForeignkey(Test foreignkey) {
		this.foreignkey = foreignkey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDTest == null) ? 0 : IDTest.hashCode());
		result = prime * result + ((Result == null) ? 0 : Result.hashCode());
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
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
		SingleTest other = (SingleTest) obj;
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
		return true;
	}

}
