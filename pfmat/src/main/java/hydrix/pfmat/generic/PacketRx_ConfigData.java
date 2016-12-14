package hydrix.pfmat.generic;

public class PacketRx_ConfigData extends Packet
{
	// Fields
	private short mWaitTime = PFMAT.VOLTAGE_FAILED;

	// Construction
	public PacketRx_ConfigData()
	{
		super(PFMAT.RX_CONFIG_DATA);
	}
	
	// Serialization
	@Override
	protected boolean parsePayload(byte[] payload)
	{
		// Sweet
		return true;
	}
}
