package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_SetZeroVoltage extends Packet
{
	// Fields
		protected byte mSensorIndex;
		protected short mZeroVoltage;
	
	// Construction
	public PacketTx_SetZeroVoltage(byte sensorIndex, short zeroVoltage)
	{
		super(PFMAT.TX_SET_ZERO_VOLTAGE);
		mSensorIndex = sensorIndex;
		mZeroVoltage = zeroVoltage;
	}

	// Accessors
		public final byte getSensorIndex() {return mSensorIndex;}
		public final short getRefVoltage() {return mZeroVoltage;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is 2 bytes... 1 byte sensor index, 2 bytes zero voltage
				byte[] payload = new byte [3];
				ByteBuffer buf = ByteBuffer.wrap(payload);
				buf.order(ByteOrder.LITTLE_ENDIAN);
				buf.put(mSensorIndex);
				buf.putShort(mZeroVoltage);
				return payload;
	}
}