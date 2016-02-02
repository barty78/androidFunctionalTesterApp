package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_GetCalOffset extends Packet {

	//Fields
	protected Byte mSensorIndex;
	
	//Construction
	public PacketTx_GetCalOffset(byte sensorIndex)
	{
		super(PFMAT.TX_GET_CAL_OFFSET);
		mSensorIndex = sensorIndex;
	}
	
	// Accessors
	public final byte getSensorIndex() {return mSensorIndex;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is 2 bytes... 1 byte sensor index, 1 byte reserved, 2 byte current load
		byte[] payload = new byte [2];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(mSensorIndex);
		buf.put((byte)0); // Reserved
		return payload;
	}
}