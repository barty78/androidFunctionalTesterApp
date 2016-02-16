package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketTx_SetAllVoltage extends BigPacket
{
	// Fields

	protected short[] mZeroVoltages;
	protected short[] mRefVoltages;

	// Construction
	public PacketTx_SetAllVoltage(short[] zeroVoltages, short[] refVoltages)
	{
		super(PFMAT.TX_SET_ALL_VOLTAGE);
		mRefVoltages = refVoltages;
		mZeroVoltages = zeroVoltages;
	}

	// Accessors
//		public final byte getSensorIndex() {return mSensorIndex;}
//		public final short getRefVoltage() {return mZeroVoltage;}
	
	// Serialization
	@Override
	protected byte[] buildPayload()
	{
		// Payload is 12 bytes... 6 voltages x 2 bytes each
		byte[] payload = new byte [12];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);

		buf.putShort(mRefVoltages[0]);
		buf.putShort(mRefVoltages[1]);
		buf.putShort(mRefVoltages[2]);
		buf.putShort(mZeroVoltages[0]);
		buf.putShort(mZeroVoltages[1]);
		buf.putShort(mZeroVoltages[2]);
		return payload;
	}
}