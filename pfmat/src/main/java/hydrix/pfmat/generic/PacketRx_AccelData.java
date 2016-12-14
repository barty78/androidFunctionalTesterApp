package hydrix.pfmat.generic;


import hydrix.pfmat.generic.Motion.Acceleration;
import hydrix.pfmat.generic.Motion.Rotation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class PacketRx_AccelData extends Packet {
	// Fields
	private int mRequestTimestamp = 0;
	private short mOrientation = 0;
	private Acceleration mAccel = null;
	private Rotation mGyro = null;
	private Quaternion mQuat = null;
	private int mAccelx = 0;
	private int mAccely = 0;
	private int mAccelz = 0;
	private int mGyrox = 0;
	private int mGyroy = 0;
	private int mGyroz = 0;
	private int mQuatw = 0;
	private int mQuatx = 0;
	private int mQuaty = 0;
	private int mQuatz = 0;

	private float yaw = 0;
	private float pitch = 0;
	private float roll = 0;
	private byte mode = 0;
	private short mSensor0;
	private short mSensor1;
	private short mSensor2;


	// Construction
	public PacketRx_AccelData() {
		super(PFMAT.RX_ACCEL_DATA);
	}

	// Accessors
	public final int getRequestTimestamp() {
		return mRequestTimestamp;
	}

	public final short getOrientation() {
		return mOrientation;
	}

	public final Acceleration getAccel() {
		return mAccel;
	}

	public final Rotation getGyro() {
		return mGyro;
	}

	public final Quaternion getQuat() {
		return mQuat;
	}

	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload) {
		// We expect exactly 45 byte payload (32-bit RequestTimestamp, 8-bit Orientation, 3 x 32-bit Acceleration, 3 x 32-bit Gyroscope, 4 x 32-bit Quaternion)
		if ((payload == null) || (payload.length != 52))
			return false;

		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		// Grab the values
		mRequestTimestamp = buf.getInt();
		mode = buf.get();
		if ((mode & PFMAT.SENSORS) != 0) {

		}
		return parseSensorsAndAccel(buf);

	}

	private boolean parseSensorsAndAccel(ByteBuffer buf) {
		return (parseSensors(buf) && parseAccel(buf));
	}

	private boolean parseAccel(ByteBuffer buf) {
		mOrientation = buf.get();
		mAccelx = buf.getInt();
		mAccely = buf.getInt();
		mAccelz = buf.getInt();
		mGyrox = buf.getInt();
		mGyroy = buf.getInt();
		mGyroz = buf.getInt();
		mQuatw = buf.getInt();
		mQuatx = buf.getInt();
		mQuaty = buf.getInt();
		mQuatz = buf.getInt();

		mAccel = new Acceleration(mAccelx, mAccely, mAccelz);
		mGyro = new Rotation(mGyrox, mGyroy, mGyroz);
		mQuat = new Quaternion(mQuatw, mQuatx, mQuaty, mQuatz);

		/*Log.d("ACCELx:", String.valueOf(mAccel.mAccelX));
        Log.d("ACCELy:", String.valueOf(mAccel.mAccelY));
		Log.d("ACCELz:", String.valueOf(mAccel.mAccelZ));
		Log.d("GYROx:", String.valueOf(mGyro.mGyroX));
		Log.d("GYROY:", String.valueOf(mGyro.mGyroY));
		Log.d("GYROZ:", String.valueOf(mGyro.mGyroZ));
		Log.d("QUATw:", String.valueOf(mQuat.w));
		Log.d("QUATx:", String.valueOf(mQuat.x));
		Log.d("QUATy:", String.valueOf(mQuat.y));
		Log.d("QUATz:", String.valueOf(mQuat.z));*/

		// Sweet
		parseYPR();
		return true;
	}

	private boolean parseSensors(ByteBuffer buf){
		mSensor0 = buf.getShort();
		if (mSensor0 < PFMAT.MIN_SENSOR_VALUE || mSensor0 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		mSensor1 = buf.getShort();
		if (mSensor1 < PFMAT.MIN_SENSOR_VALUE || mSensor1 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		mSensor2 = buf.getShort();
		if (mSensor2 < PFMAT.MIN_SENSOR_VALUE || mSensor2 > PFMAT.MAX_SENSOR_VALUE)
			return false;
		// Sweet
		return true;
	}


	private void parseYPR() {
		float[] ypr = YawPitchRoll.calculateYawPitchRoll(this);
		yaw = ypr[0];
		pitch = ypr[1];
		roll = ypr[2];
	}

	public int getmAccelx() {
		return mAccelx;
	}

	public int getmAccely() {
		return mAccely;
	}

	public int getmAccelz() {
		return mAccelz;
	}

	public int getmGyrox() {
		return mGyrox;
	}

	public int getmGyroy() {
		return mGyroy;
	}

	public int getmGyroz() {
		return mGyroz;
	}

	public int getmQuatw() {
		return mQuatw;
	}

	public int getmQuatx() {
		return mQuatx;
	}

	public int getmQuaty() {
		return mQuaty;
	}

	public int getmQuatz() {
		return mQuatz;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}


	public short getmOrientation() {
		return mOrientation;
	}

	public Acceleration getmAccel() {
		return mAccel;
	}

	public Rotation getmGyro() {
		return mGyro;
	}

	public Quaternion getmQuat() {
		return mQuat;
	}

	public byte getMode() {
		return mode;
	}

	public short getmSensor0() {
		return mSensor0;
	}

	public short getmSensor1() {
		return mSensor1;
	}

	public short getmSensor2() {
		return mSensor2;
	}

	@Override
	public String toString() {
		return "PacketRx_AccelData{" +
				"mRequestTimestamp=" + mRequestTimestamp +
				", mOrientation=" + mOrientation +
				", mAccel=" + mAccel +
				", mGyro=" + mGyro +
				", mQuat=" + mQuat +
				", mAccelx=" + mAccelx +
				", mAccely=" + mAccely +
				", mAccelz=" + mAccelz +
				", mGyrox=" + mGyrox +
				", mGyroy=" + mGyroy +
				", mGyroz=" + mGyroz +
				", mQuatw=" + mQuatw +
				", mQuatx=" + mQuatx +
				", mQuaty=" + mQuaty +
				", mQuatz=" + mQuatz +
				", yaw=" + yaw +
				", pitch=" + pitch +
				", roll=" + roll +
				", mode=" + mode +
				", mSensor0=" + mSensor0 +
				", mSensor1=" + mSensor1 +
				", mSensor2=" + mSensor2 +
				'}';
	}
}