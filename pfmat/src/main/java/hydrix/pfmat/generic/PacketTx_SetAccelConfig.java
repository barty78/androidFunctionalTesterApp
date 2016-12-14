package hydrix.pfmat.generic;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_SetAccelConfig extends Packet
{

	private final String TAG = getClass().getSimpleName();

	// Fields

	private final byte mAccelFSR;
	private final short mGyroFSR;

	// Construction
	public PacketTx_SetAccelConfig(byte accelFSR, short gyroFSR)
	{
		super(PFMAT.TX_SET_ACCEL_CONFIG);
		mAccelFSR = accelFSR;
		mGyroFSR = gyroFSR;
		Log.d(TAG, "Accel Config Packet TX, Acc_FSR " + accelFSR + " | Gy_FSR " + gyroFSR);
	}

	// Accessors
		public final byte getAccelFSR() {return mAccelFSR;}
		public final short getGyroFSR() {return mGyroFSR;}
	
	// Serialization
	@Override
		protected byte[] buildPayload()
		{
			// Payload is 3 bytes... 1 byte Accel FSR, 2 bytes Gyro FSR
			byte[] payload = new byte [3];
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);

			buf.put(mAccelFSR);
			buf.putShort(mGyroFSR);
			return payload;
	}
}