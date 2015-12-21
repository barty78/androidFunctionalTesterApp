
package server.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @Expose
    private Long units;
    @Expose
    private Integer ioiopinnum;
    @Expose
    private Long active;
    @Expose
    private int isNominal;
    @Expose
    private Double limitParam1;
    @Expose
    private Double limitParam2;
    @Expose
    private Long Upper;
    @Expose
    private Long Lower;
    @Expose
    private Double Nominal;
    @Expose
    private Double Tolerance;

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
    public void setBlocking(Long blocking) {
        this.blocking = blocking;
    }

    /**
     * 
     * @return
     *     The units
     */
    public Long getUnits() {
        return units;
    }

    /**
     * 
     * @param units
     *     The units
     */
    public void setUnits(Long units) {
        this.units = units;
    }

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
    public void setActive(Long active) {
        this.active = active;
    }

    public int getIsNominal() {
        return isNominal;
    }

    public void setIsNominal(int isNominal) {this.isNominal = isNominal;}

    public Double getLimitParam1() {
        return limitParam1;
    }

    public void setLimitParam1(Double limitParam1) {this.limitParam1 = limitParam1;}

    public Double getLimitParam2() {
        return limitParam2;
    }

    public void setLimitParam2(Double limitParam2) {this.limitParam2 = limitParam2;}

    /**
     * 
     * @return
     *     The Upper
     */
    public Long getUpper() {
        return Upper;
    }

    /**
     * 
     * @param Upper
     *     The Upper
     */
    public void setUpper(Long Upper) {
        this.Upper = Upper;
    }

    /**
     * 
     * @return
     *     The Lower
     */
    public Long getLower() {
        return Lower;
    }

    /**
     * 
     * @param Lower
     *     The Lower
     */
    public void setLower(Long Lower) {
        this.Lower = Lower;
    }

    /**
     * 
     * @return
     *     The Nominal
     */
    public Double getNominal() {
        return Nominal;
    }

    /**
     * 
     * @param Nominal
     *     The Nominal
     */
    public void setNominal(Double Nominal) {
        this.Nominal = Nominal;
    }

    /**
     * 
     * @return
     *     The Tolerance
     */
    public Double getTolerance() {
        return Tolerance;
    }

    /**
     * 
     * @param Tolerance
     *     The Tolerance
     */
    public void setTolerance(Double Tolerance) {
        this.Tolerance = Tolerance;
    }

}
