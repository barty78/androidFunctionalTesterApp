package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_SetRefVoltage extends Packet
{
	// Fields
	private byte mSensorIndex = 0;
	private short mRefVoltage = PFMAT.VOLTAGE_FAILED;
	
	// Construction
	public PacketRx_SetRefVoltage()
	{
		super(PFMAT.RX_REF_VOLTAGE);
	}
	
	// Accessors
	public final byte getSensorIndex() {return mSensorIndex;}
	public final boolean refVoltageFailed() {return (mRefVoltage == PFMAT.VOLTAGE_FAILED);}
	public final short getRefVoltage() {return mRefVoltage;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
	{
		// We expect exactly 3 byte payload (byte sensor index, byte voltage)
		if ((payload == null) || (payload.length != 3))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mSensorIndex = buf.get();
		if (mSensorIndex < PFMAT.MIN_SENSOR_INDEX || mSensorIndex > PFMAT.MAX_SENSOR_INDEX)
			return false;
		mRefVoltage = buf.getShort();
		if ((mRefVoltage != PFMAT.VOLTAGE_FAILED) && (mRefVoltage < -PFMAT.MAX_VOLTAGE_VALUE || mRefVoltage > PFMAT.MAX_VOLTAGE_VALUE))
			return false;
		
		// Sweet
		return true;
	}
}
