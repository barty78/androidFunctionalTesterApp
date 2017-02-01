
package server.pojos.records;


import com.google.gson.annotations.Expose;

//@Table(name = "Sensors")
public class Sensors {

    public Sensors() {
        super();
        // TODO Auto-generated constructor stub
    }

    //@Column(name = "S0")
    @Expose
    private Sensor S0;

    //@Column(name = "S1")
    @Expose
    private Sensor S1;

    //@Column(name = "S2")
    @Expose
    private Sensor S2;

    public void setSensor(int sensorNumber, Sensor sensor) {
        switch(sensorNumber) {
            case 0:
                this.S0 = sensor;
                break;
            case 1:
                this.S1 = sensor;
                break;
            case 2:
                this.S2 = sensor;
                break;
            default:
                break;
        }
    }

    /**
     * @return The S0
     */
    public Sensor getS0() {
        return S0;
    }

    /**
     * @param S0 The S0
     */
    public void setS0(Sensor S0) {
        this.S0 = S0;
    }

    /**
     * @return The S1
     */
    public Sensor getS1() {
        return S1;
    }

    /**
     * @param S1 The S1
     */
    public void setS1(Sensor S1) {
        this.S1 = S1;
    }

    /**
     * @return The S2
     */
    public Sensor getS2() {
        return S2;
    }

    /**
     * @param S2 The S2
     */
    public void setS2(Sensor S2) {
        this.S2 = S2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((S0 == null) ? 0 : S0.hashCode());
        result = prime * result + ((S1 == null) ? 0 : S1.hashCode());
        result = prime * result + ((S2 == null) ? 0 : S2.hashCode());
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
        Sensors other = (Sensors) obj;
        if (S0 == null) {
            if (other.S0 != null)
                return false;
        } else if (!S0.equals(other.S0))
            return false;
        if (S1 == null) {
            if (other.S1 != null)
                return false;
        } else if (!S1.equals(other.S1))
            return false;
        if (S2 == null) {
            if (other.S2 != null)
                return false;
        } else if (!S2.equals(other.S2))
            return false;
        return true;
    }

}
