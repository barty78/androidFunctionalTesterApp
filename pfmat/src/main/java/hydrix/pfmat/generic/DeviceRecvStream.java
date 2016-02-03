package hydrix.pfmat.generic;

public class DeviceRecvStream extends RecvStream
{
	private static final int RECV_BUFSIZE = 1024;
	private final PacketHandler mPacketHandler;

	// Construction
	public DeviceRecvStream(PacketHandler packetHandler)
	{
		super(RECV_BUFSIZE);
		mPacketHandler = packetHandler;
	}
	
	// Mandatory override that has PFMAT device-specific packet knowledge
	@Override
	protected boolean consumePackets()
	{
		// May be multiple packets in the stream.. parse all available
		while (true)
		{
			// Peek at the stream
			Packet.PeekInfo peekInfo = Packet.peekStream(mBuffer, mStreamOffset, mStreamSize);
			
			// If we happened to notice a corrupt stream then abort with failure
			if ((peekInfo == null) || peekInfo.getResult() == Packet.PeekResult.STREAM_CORRUPT)
				return false;
			
			// A partial packet is a standard condition, not a failure case
			else if (peekInfo.getResult() == Packet.PeekResult.STREAM_INCOMPLETE)
				break;
			
			// Got enough data to parse a packet... create the appropriate packet object to do the rest
			Packet packet;
			switch (peekInfo.getPacketType())
			{
			case PFMAT.RX_SENSOR_DATA:
				packet = new PacketRx_SensorData();
				break;
			case PFMAT.RX_DEVICE_DETAILS:
				packet = new PacketRx_DeviceDetails();
				break;
			case PFMAT.RX_BATTERY_STATUS:
				packet = new PacketRx_BatteryStatus();
				break;
			//Outdated packet
//			case PFMAT.RX_CALIBRATED_SENSOR:
//				packet = new PacketRx_CalibratedSensor();
//				break;
			default:
				packet = null;
				break;
			}
			
			// If we don't know about the packet type then this suggests a corrupt stream that we should abort
			if (packet == null)
				return false;
			
			// Parse it
			if (!packet.fromStream(mBuffer, mStreamOffset, peekInfo.getPacketSize()))
				return false;
			
			// Handle the packet!!
			mPacketHandler.handlePacket(packet);
			
			// Advance the stream past what we just consumed
			mStreamOffset += peekInfo.getPacketSize();
			mStreamSize -= peekInfo.getPacketSize();
		}

		// If we happen to have consumed the entire stream then jump at the chance to reset the stream offset (to avoid a memory shift later on)
		if (mStreamSize == 0)
			mStreamOffset = 0;
		return true;
	}

}
