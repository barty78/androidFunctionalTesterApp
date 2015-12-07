package hydrix.pfmat.generic;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PacketRx_CalibratedSensor extends Packet
{
	// Fields
	private byte mSensorIndex = 0;
	private float mCalibratedOffset = PFMAT.CALIBRATION_FAILED;
	private float mCalibratedCoeff = PFMAT.CALIBRATION_FAILED;
	
	// Construction
	public PacketRx_CalibratedSensor()
	{
		super(PFMAT.RX_CALIBRATED_SENSOR);
	}
	
	// Accessors
	public final byte getSensorIndex() {return mSensorIndex;}
	public final boolean calibrationFailed() {return (mCalibratedOffset == PFMAT.CALIBRATION_FAILED);}
	public final float getCalibratedOffset() {return mCalibratedOffset;}
	public final float getCalibratedCoeff() {return mCalibratedCoeff;}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
	{
		// We expect exactly 4 byte payload (byte sensor index, byte reserved, float offset)
		if ((payload == null) || (payload.length != 6))
			return false;
		
		// Wrap in ByteBuffer and specify byte order
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		// Grab the values
		mSensorIndex = buf.get();
		if (mSensorIndex < PFMAT.MIN_SENSOR_INDEX || mSensorIndex > PFMAT.MAX_SENSOR_INDEX)
			return false;
		buf.get(); // Discard the reserved field
		mCalibratedOffset = buf.getFloat();
		//mCalibratedOffset = 1.909f;
		if ((mCalibratedOffset != PFMAT.CALIBRATION_FAILED) && (mCalibratedOffset < -PFMAT.MAX_SENSOR_VALUE || mCalibratedOffset > PFMAT.MAX_SENSOR_VALUE))
			return false;
		
		// Sweet
		return true;
	}
}
