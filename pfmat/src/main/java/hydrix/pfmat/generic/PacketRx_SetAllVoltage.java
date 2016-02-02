package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_SetAllVoltage extends Packet
{
	// Fields

		private short mVoltageStatus = PFMAT.NACK;

		// Construction
		public PacketRx_SetAllVoltage()
		{
			super(PFMAT.RX_ALL_VOLTAGE);
		}
		
		// Accessors
		public final boolean VoltageFailed() {return (mVoltageStatus == PFMAT.NACK);}
		
		// Serialization
		@Override
		protected boolean parsePayload(byte[] payload)
		{
			// We expect exactly 1 byte payload (byte sensor index, byte voltage)
			if ((payload == null) || (payload.length != 1))
				return false;
			
			// Wrap in ByteBuffer and specify byte order
			ByteBuffer buf = ByteBuffer.wrap(payload);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			
			// Grab the values
			mVoltageStatus = buf.get();

			if (mVoltageStatus != PFMAT.ACK) return false;
			
			// Sweet
			return true;
		}
}