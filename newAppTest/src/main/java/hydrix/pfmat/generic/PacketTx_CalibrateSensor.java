package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_CalibrateSensor extends Packet
{
	// Fields
	protected byte mSensorIndex;
	protected byte mReadOnly;
	protected short mCurrentLoad;
	
	// Construction
	public PacketTx_CalibrateSensor(byte sensorIndex, byte readOnly, short currentLoad)
	{
		super(PFMAT.TX_CALIBRATE_SENSOR);
		mSensorIndex = sensorIndex;
		mReadOnly = readOnly;
		mCurrentLoad = currentLoad;
	}

	// Accessors
	public final byte getSensorIndex() {return mSensorIndex;}
	public final short getCurrentLoad() {return mCurrentLoad;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is 4 bytes... 1 byte sensor index, 1 byte reserved, 2 byte current load
		byte[] payload = new byte [4];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(mSensorIndex);
		buf.put(mReadOnly);
		//buf.put((byte)0); // Reserved
		buf.putShort(mCurrentLoad);
		return payload;
	}
}
