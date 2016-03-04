
package server.pojos.records;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "Readings")
public class Readings extends MyModel{

	@Expose
	@Column(name = "Test")
    Test Test;
    
	private @Expose  
    @Column(name = "Sensors")
    Sensors Sensors;

    @Column(name = "TestRecord")
    private TestRecord foreignkey;
    /**
     * 
     * @return
     *     The Test
     */
    public Test getTest() {
        return Test;
    }

    /**
     * 
     * @param Test
     *     The Test
     */
    public void setTest(Test Test) {
        this.Test = Test;
    }

    /**
     * 
     * @return
     *     The Sensors
     */
    public Sensors getSensors() {
        return Sensors;
    }

    /**
     * 
     * @param Sensors
     *     The Sensors
     */
    public void setSensors(Sensors Sensors) {
        this.Sensors = Sensors;
    }
    
    

	@SuppressWarnings("unused")
	public TestRecord getForeignkey() {
		return foreignkey;
	}

	public void setForeignkey(TestRecord foreignkey) {
		this.foreignkey = foreignkey;
	}

	public Readings() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Sensors == null) ? 0 : Sensors.hashCode());
		result = prime * result + ((Test == null) ? 0 : Test.hashCode());
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
		Readings other = (Readings) obj;
		if (Sensors == null) {
			if (other.Sensors != null)
				return false;
		} else if (!Sensors.equals(other.Sensors))
			return false;
		if (Test == null) {
			if (other.Test != null)
				return false;
		} else if (!Test.equals(other.Test))
			return false;
		return true;
	}


}
