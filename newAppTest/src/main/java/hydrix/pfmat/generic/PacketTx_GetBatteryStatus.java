package hydrix.pfmat.generic;


public class PacketTx_GetBatteryStatus extends Packet
{
	// Construction
	public PacketTx_GetBatteryStatus()
	{
		super(PFMAT.TX_GET_BATTERY_STATUS);
	}

	// No need to override buildPayload as there is none for this packet type
}
