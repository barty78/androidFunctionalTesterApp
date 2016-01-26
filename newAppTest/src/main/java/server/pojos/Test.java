package server.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pietrantuono.ioioutils.Units;

public class Test {

    @Expose
    private Long id;

    @SerializedName("testclass_id")
    @Expose
    private Long testclassId;

    @SerializedName("limit_id")
    @Expose
    private Long limitId;
    @Expose
    private Long number;
    @Expose
    private String name;
    @Expose
    private Long istest;
    @Expose
    private Long blocking;

    @SuppressWarnings("unused")
    @SerializedName("units")
    @Expose
    private String units;
    @Expose
    private Float scaling;
    @Expose
    private Integer ioiopinnum;
    @Expose
    private Long active;
    @Expose
    private Integer isNominal;
    @Expose
    private Float limitParam1;
    @Expose
    private Float limitParam2;
    @Expose
    private Float limitParam3;
//    @Expose
//    private Long Upper;
//    @Expose
//    private Long Lower;
//    @Expose
//    private Long Nominal;
//    @Expose
//    private Long Tolerance;

    /**
     *
     * @return
     *     The id
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    @SuppressWarnings("unused")
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The testclassId
     */
    public Long getTestclassId() {
        return testclassId;
    }

    /**
     *
     * @param testclassId
     *     The testclass_id
     */
    @SuppressWarnings("unused")
    public void setTestclassId(Long testclassId) {
        this.testclassId = testclassId;
    }

    /**
     *
     * @return
     *     The limitId
     */
    public Long getLimitId() {
        return limitId;
    }

    /**
     *
     * @param limitId
     *     The limit_id
     */
    @SuppressWarnings("unused")
    public void setLimitId(Long limitId) {
        this.limitId = limitId;
    }

    /**
     *
     * @return
     *     The number
     */
    public Long getNumber() {
        return number;
    }

    /**
     *
     * @param number
     *     The number
     */
    @SuppressWarnings("unused")
    public void setNumber(Long number) {
        this.number = number;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The istest
     */
    public Long getIstest() {
        return istest;
    }

    /**
     *
     * @param istest
     *     The istest
     */
    @SuppressWarnings("unused")
    public void setIstest(Long istest) {
        this.istest = istest;
    }

    /**
     *
     * @return
     *     The blocking
     */
    public Long getBlocking() {
        return blocking;
    }

    /**
     *
     * @param blocking
     *     The blocking
     */
    @SuppressWarnings("unused")
    public void setBlocking(Long blocking) {
        this.blocking = blocking;
    }

    /**
     *
     * @return
     *     The units
     */
    public @Units
    int getUnits() {
        if(units==null)return Units.NULL;
        switch (units){
            case "mA":
                return Units.mA;
            case "uA":
                return Units.uA;
            case "nA":
                return Units.nA;
            case "V":
                return Units.V;
            case "%":
                return Units.percent;
            default:
                return Units.NULL;
        }
    }

    /**
     *
     * @param scaling
     *     The scaling
     */
    @SuppressWarnings("unused")
    public void setScaling(Float scaling) {
        this.scaling = scaling;
    }

    /**
     *
     * @return
     *     The scaling
     */
    public Float getScaling() {
        return scaling;
    }

    /**
     *
     * @param units
     *     The units
     */
    @SuppressWarnings("unused")
    public void setUnits(@Units int units) {}


    /**
     *
     * @return
     *     The ioiopinnum
     */
    public Integer getIoiopinnum() {
        return ioiopinnum;
    }

    /**
     *
     * @param ioiopinnum
     *     The ioiopinnum
     */
    @SuppressWarnings("unused")
    public void setIoiopinnum(Integer ioiopinnum) {
        this.ioiopinnum = ioiopinnum;
    }

    /**
     *
     * @return
     *     The active
     */
    public Long getActive() {
        return active;
    }

    /**
     *
     * @param active
     *     The active
     */
    @SuppressWarnings("unused")
    public void setActive(Long active) {
        this.active = active;
    }

    public int getIsNominal() {
        return isNominal;
    }

    @SuppressWarnings("unused")
    public void setIsNominal(int isNominal) {this.isNominal = isNominal;}

    public Float getLimitParam1() {
        return limitParam1;
    }

    @SuppressWarnings("unused")
    public void setLimitParam1(Float limitParam1) {this.limitParam1 = limitParam1;}

    public Float getLimitParam2() {
        return limitParam2;
    }

    @SuppressWarnings("unused")
    public void setLimitParam2(Float limitParam2) {this.limitParam2 = limitParam2;}

//    /**
//     *
//     * @return
//     *     The Upper
//     */
//    public Long getUpper() {
//        return Upper;
//    }
//
//    /**
//     *
//     * @param Upper
//     *     The Upper
//     */
//    public void setUpper(Long Upper) {
//        this.Upper = Upper;
//    }
//
//    /**
//     *
//     * @return
//     *     The Lower
//     */
//    public Long getLower() {
//        return Lower;
//    }
//
//    /**
//     *
//     * @param Lower
//     *     The Lower
//     */
//    public void setLower(Long Lower) {
//        this.Lower = Lower;
//    }
//
//    /**
//     *
//     * @return
//     *     The Nominal
//     */
//    public Long getNominal() {
//        return Nominal;
//    }
//
//    /**
//     *
//     * @param Nominal
//     *     The Nominal
//     */
//    public void setNominal(Long Nominal) {
//        this.Nominal = Nominal;
//    }
//
//    /**
//     *
//     * @return
//     *     The Tolerance
//     */
//    public Long getTolerance() {
//        return Tolerance;
//    }
//
//    /**
//     *
//     * @param Tolerance
//     *     The Tolerance
//     */
//    public void setTolerance(Long Tolerance) {
//        this.Tolerance = Tolerance;
//    }

    public Float getLimitParam3() {
        return limitParam3;
    }

    @SuppressWarnings("unused")
    public void setLimitParam3(Float limitParam3) {
        this.limitParam3 = limitParam3;
    }

    @SuppressWarnings("unused")
    public void setIsNominal(Integer isNominal) {
        this.isNominal = isNominal;
    }
}
