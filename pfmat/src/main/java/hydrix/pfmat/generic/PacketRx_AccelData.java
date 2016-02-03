package hydrix.pfmat.generic;


import hydrix.pfmat.generic.Motion.Acceleration;
import hydrix.pfmat.generic.Motion.Rotation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

public class PacketRx_AccelData extends Packet
{
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
	
	// Construction
	public PacketRx_AccelData()
	{
		super(PFMAT.RX_ACCEL_DATA);
	}
	
	// Accessors
	public final int getRequestTimestamp() {return mRequestTimestamp;}
	public final short getOrientation() {return mOrientation;}
	public final Acceleration getAccel() {return mAccel;}
	public final Rotation getGyro() {return mGyro;}
	public final Quaternion getQuat() {return mQuat;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
		{
		// We expect exactly 45 byte payload (32-bit RequestTimestamp, 8-bit Orientation, 3 x 32-bit Acceleration, 3 x 32-bit Gyroscope, 4 x 32-bit Quaternion)
		if ((payload == null) || (payload.length != 45))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mRequestTimestamp = buf.getInt();
		
		mOrientation = buf.get();
		Log.d("ORIENT:", String.valueOf(mOrientation));
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
		return true;
		}
}