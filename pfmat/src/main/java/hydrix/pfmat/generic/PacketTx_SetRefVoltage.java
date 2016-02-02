package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_SetRefVoltage extends Packet
{
	// Fields
	protected byte mSensorIndex;
	protected short mRefVoltage;
	
	// Construction
	public PacketTx_SetRefVoltage(byte sensorIndex, short refVoltage)
	{
		super(PFMAT.TX_SET_REF_VOLTAGE);
		mSensorIndex = sensorIndex;
		mRefVoltage = refVoltage;
	}

	// Accessors
	public final byte getSensorIndex() {return mSensorIndex;}
	public final short getRefVoltage() {return mRefVoltage;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is 2 bytes... 1 byte sensor index, 2 bytes ref voltage
		byte[] payload = new byte [3];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(mSensorIndex);
		buf.putShort(mRefVoltage);
		return payload;
	}
}
