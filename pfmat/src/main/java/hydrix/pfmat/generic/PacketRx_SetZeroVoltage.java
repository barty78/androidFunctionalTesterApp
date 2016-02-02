package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_SetZeroVoltage extends Packet
{
	// Fields
		private byte mSensorIndex = 0;
		private short mZeroVoltage = PFMAT.VOLTAGE_FAILED;
		
		// Construction
		public PacketRx_SetZeroVoltage()
		{
			super(PFMAT.RX_ZERO_VOLTAGE);
		}
		
		// Accessors
		public final byte getSensorIndex() {return mSensorIndex;}
		public final boolean zeroVoltageFailed() {return (mZeroVoltage == PFMAT.VOLTAGE_FAILED);}
		public final short getZeroVoltage() {return mZeroVoltage;}
		
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
			mZeroVoltage = buf.getShort();
			if ((mZeroVoltage != PFMAT.VOLTAGE_FAILED) && (mZeroVoltage < -PFMAT.MAX_VOLTAGE_VALUE || mZeroVoltage > PFMAT.MAX_VOLTAGE_VALUE))
				return false;
			
			// Sweet
			return true;
		}
}