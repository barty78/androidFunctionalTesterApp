package hydrix.pfmat.generic;

/**
 * Created by mauriziopietrantuono on 19/02/16.
 */
public class YPR {
    int timestamp;
    float yaw;
    float pitch;
    float roll;

    public YPR(int timestamp,float yaw, float pitch, float roll) {
        this.timestamp=timestamp;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
