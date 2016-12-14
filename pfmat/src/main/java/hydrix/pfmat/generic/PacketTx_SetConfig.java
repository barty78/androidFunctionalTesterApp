package hydrix.pfmat.generic;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_SetConfig extends Packet
{
	private final String TAG = getClass().getSimpleName();

	// Fields
	private final byte mSampleRate;
	private final byte mConfigByte;
	private final byte mSensorTestFlagByte;

	// Construction
	public PacketTx_SetConfig(byte configByte, byte sampleRate, byte sensorTestFlag)
	{
		super(PFMAT.TX_SET_CONFIG);
		mSampleRate = sampleRate;
		mConfigByte = configByte;
		mSensorTestFlagByte = sensorTestFlag;

		Log.d(TAG, "TX Config Packet, Config Byte " + mConfigByte);
	}
	
	// Serialization
	@Override
		protected byte[] buildPayload()
		{
			// Payload is 3 bytes... 1 Config Byte, 1 SampleRate Byte, 1 sensorTestFlag Byte
			byte[] payload = new byte [3];
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put(mConfigByte);
			buf.put(mSampleRate);
			buf.put(mSensorTestFlagByte);
			return payload;
	}
}