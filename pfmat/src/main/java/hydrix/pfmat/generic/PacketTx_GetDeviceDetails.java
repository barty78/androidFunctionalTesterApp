package hydrix.pfmat.generic;


public class PacketTx_GetDeviceDetails extends Packet
{
	// Construction
	public PacketTx_GetDeviceDetails()
	{
		super(PFMAT.TX_GET_DEVICE_DETAILS);
	}

	// No need to override buildPayload as there is none for this packet type
}
