package server.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class Job implements Parcelable {

    @Expose
    private long id;

    @SerializedName("primaryjob_id")
    @Expose
    private long primaryJobId;

    @SerializedName("testtype_id")
    @Expose
    private long testtypeId;

    @SerializedName("test_id")
    @Expose
    private long testId;

    @SerializedName("firmware_id")
    @Expose
    private long firmwareId;


    @Expose
    private String jobno;

    @Expose
    private long quantity;

    @SerializedName("barcodeprefix")
    @Expose
    private String barcodeprefix;


    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @Expose
    private long islogging;

    @Expose
    private long isretestallowed;

    @Expose
    private long active;

    @Expose
    private String description;


//    Ok.  Stage_dep field is now sent from the web service in the job api..  stage_dep will tell us what needs to be run/passed before the current test can be executed for the device..  So a value of 0 is nothing (this is the first stage of testing).  A value of 1 means that open test needs to have been run, a value of 2 means closed test needs to have been run.  A value of 3 means both open and closed needs to have been run..  So right now, it will be only values of 0 or 1 because we have no stages after closed test..
//    So we now just need to work out when/where and what the app logic should be.
//    For open test it doesn't really need anything, but maybe for completeness we should use stage_dep and make sure status is 0
//    so status == stage_dep
//    for closed test, once we've IDed the device, we should check against local db that status & stage_dep == stage_dep
    @Expose
    @SerializedName("stage_dep")
    private int stage_dep;



    public Job() {}

    /**
     *
     * @return
     *     The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(long id) {
        this.id = id;
    }


    /**
     *
     * @return
     *     The primary job id
     */
    public long getPrimaryJobId() { return primaryJobId;}

    /**
     *
     * @param id
     *     The primary job id
     */
    public void setPrimaryJobId(long id) { this.primaryJobId = id;}

    /**
     *
     * @return
     *     The testtypeId
     */
    public long getTesttypeId() {
        return testtypeId;
    }

    /**
     *
     * @param testtypeId
     *     The testtype_id
     */
    public void setTesttypeId(long testtypeId) {
        this.testtypeId = testtypeId;
    }

    /**
     *
     * @return
     *     The testId
     */
    public long getTestId() {
        return testId;
    }

    /**
     *
     * @param testId
     *     The test_id
     */
    public void setTestId(long testId) {
        this.testId = testId;
    }

    /**
     *
     * @return
     *     The firmwareId
     */
    public long getFirmwareId() {
        return firmwareId;
    }

    /**
     *
     * @param firmwareId
     *     The firmware_id
     */
    public void setFirmwareId(long firmwareId) {
        this.firmwareId = firmwareId;
    }


    /**
     *
     * @return
     *     The jobno
     */
    public String getJobno() {
        return jobno;
    }

    /**
     *
     * @param jobno
     *     The jobno
     */
    public void setJobno(String jobno) {
        this.jobno = jobno;
    }

    /**
     *
     * @return
     *     The quantity
     */
    public long getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity
     *     The quantity
     */
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     *     The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     *     The islogging
     */
    public long getIslogging() {
        return islogging;
    }

    /**
     *
     * @param islogging
     *     The islogging
     */
    public void setIslogging(long islogging) {
        this.islogging = islogging;
    }

    /**
     * @param isretestallowed
     */
    public void setIsretestallowed(long isretestallowed) { this.isretestallowed = isretestallowed;}

    /**
     * @return
     * The isretestallowed
     */
    public long getIsretestallowed() {return isretestallowed;}

    /**
     *
     * @return
     *     The active
     */
    public long getActive() {
        return active;
    }

    /**
     *
     * @param active
     *     The active
     */
    public void setActive(long active) {
        this.active = active;
    }

    /**
     *
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcodeprefix() {
        return barcodeprefix;
    }

    public void setBarcodeprefix(String barcodeprefix) {
        this.barcodeprefix = barcodeprefix;
    }


    public int getStage_dep() {
        return stage_dep;
    }

    public void setStage_dep(int stage_dep) {
        this.stage_dep = stage_dep;
    }


    protected Job(Parcel in) {
        id = in.readLong();
        primaryJobId = in.readLong();
        testtypeId = in.readLong();
        testId = in.readLong();
        firmwareId = in.readLong();
        jobno = in.readString();
        quantity = in.readLong();
        barcodeprefix = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        islogging = in.readLong();
        isretestallowed = in.readLong();
        active = in.readLong();
        description = in.readString();
        stage_dep = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(primaryJobId);
        dest.writeLong(testtypeId);
        dest.writeLong(testId);
        dest.writeLong(firmwareId);
        dest.writeString(jobno);
        dest.writeLong(quantity);
        dest.writeString(barcodeprefix);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeLong(islogging);
        dest.writeLong(isretestallowed);
        dest.writeLong(active);
        dest.writeString(description);
        dest.writeInt(stage_dep);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
}