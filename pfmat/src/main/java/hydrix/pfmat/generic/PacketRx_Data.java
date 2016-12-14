package hydrix.pfmat.generic;


import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import hydrix.pfmat.generic.Motion.Acceleration;
import hydrix.pfmat.generic.Motion.Rotation;
import hydrix.pfmat.generic.YawPitchRoll;

public class PacketRx_Data extends Packet {

	private final String TAG = getClass().getSimpleName();

	// Fields
	private byte mSeqNbr = 0;
	private byte mBattery = 0;
	private byte mRssi = 0;
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
	private byte mConfig = 0;
	private short mSensor0;
	private short mSensor1;
	private short mSensor2;

	private boolean hasAccel = false;


	// Construction
	public PacketRx_Data() {
		super(PFMAT.RX_DATA);
	}

	// Accessors
	public final byte getSeqNbr() {
		return mSeqNbr;
	}

	public final byte getBattery() { return mBattery;}

	public final byte getRssi() { return mRssi;}

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
		// We expect a size upwards of 2 bytes (Seq# & Config), then any combination of 3 x 16-bit sensors,
		// 8-bit Orientation, 3 x 32-bit Acceleration, 3 x 32-bit Gyroscope, 4 x 32-bit Quaternion
		if (payload == null && (payload.length < 2)) {
			Log.d(TAG, "Insufficient Payload");
			return false;
		}

		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		// Grab the values
		mSeqNbr = buf.get();
		mConfig = buf.get();

		int length = 2;		// Packet always contains seqnbr and config bytes
		if ((mConfig & PFMAT.BATTERY) != 0)
			length++;

		if ((mConfig & PFMAT.RSSI) != 0)
			length++;

		if ((mConfig & PFMAT.SENSORS) != 0)
			length+=6;

		if ((mConfig & PFMAT.ACCEL) != 0) {
			hasAccel = true;
			length+=12;
		}
		if ((mConfig & PFMAT.GYRO) != 0) {
			hasAccel = true;
			length+=12;
		}
		if ((mConfig & PFMAT.QUAT) != 0) {
			hasAccel = true;
			length+=16;
		}

		if (hasAccel) length++;

		if (payload.length != length) {
			Log.d(TAG, "Payload size mismatch");
			return false;
		}

		if ((mConfig & PFMAT.BATTERY) != 0)
			mBattery = buf.get();

		if ((mConfig & PFMAT.RSSI) != 0)
			mRssi = buf.get();

		if ((mConfig & PFMAT.SENSORS) != 0) {
			mSensor0 = buf.getShort();
			mSensor1 = buf.getShort();
			mSensor2 = buf.getShort();
			parseSensors(mSensor0, mSensor1, mSensor2);

		}
		if (hasAccel) {
			mOrientation = buf.get();

			if ((mConfig & PFMAT.ACCEL) != 0) {
				mAccelx = buf.getInt();
				mAccely = buf.getInt();
				mAccelz = buf.getInt();
				mAccel = new Acceleration(mAccelx, mAccely, mAccelz);
			}
			if ((mConfig & PFMAT.GYRO) != 0) {
				mGyrox = buf.getInt();
				mGyroy = buf.getInt();
				mGyroz = buf.getInt();
				mGyro = new Rotation(mGyrox, mGyroy, mGyroz);
			}
			if ((mConfig & PFMAT.QUAT) != 0) {
				mQuatw = buf.getInt();
				mQuatx = buf.getInt();
				mQuaty = buf.getInt();
				mQuatz = buf.getInt();
				mQuat = new Quaternion(mQuatw, mQuatx, mQuaty, mQuatz);
				parseYPR();
			}
		}
		return true;
	}


	private boolean parseSensors(short sensor0, short sensor1, short sensor2){
		mSensor0 = sensor0;
		if (mSensor0 < PFMAT.MIN_SENSOR_VALUE || mSensor0 > PFMAT.MAX_SENSOR_VALUE) {
			Log.d(TAG, "S0 out of range");
			return false;
		}
		mSensor1 = sensor1;
		if (mSensor1 < PFMAT.MIN_SENSOR_VALUE || mSensor1 > PFMAT.MAX_SENSOR_VALUE) {
			Log.d(TAG, "S1 out of range");
			return false;
		}
		mSensor2 = sensor2;
		if (mSensor2 < PFMAT.MIN_SENSOR_VALUE || mSensor2 > PFMAT.MAX_SENSOR_VALUE) {
			Log.d(TAG, "S2 out of range");
			return false;
		}
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

	public byte getConfig() {
		return mConfig;
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
				"mSeqNumber=" + mSeqNbr +
				", config=" + mConfig +
				", mBattery=" + mBattery +
				", mRssi=" + mRssi +
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
				", mSensor0=" + mSensor0 +
				", mSensor1=" + mSensor1 +
				", mSensor2=" + mSensor2 +
				'}';
	}
}
