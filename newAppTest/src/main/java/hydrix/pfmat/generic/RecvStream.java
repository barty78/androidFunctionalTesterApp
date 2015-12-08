package hydrix.pfmat.generic;

abstract public class RecvStream
{
	protected byte[] mBuffer;
	protected int mStreamOffset;
	protected int mStreamSize;
	private final int MIN_BUFSIZE = 512;
	
	// Construction
	public RecvStream(int bufferSize)
	{
		mBuffer = new byte [Math.max(MIN_BUFSIZE, bufferSize)];
		mStreamOffset = 0;
		mStreamSize = 0;
	}

	// Called when data is received. Don't use newBytes.length, as this may be an entire read buffer, not the actual data length in it!
	public final boolean feed(byte[] newBytes, int numNewBytes)
	{
		// Quietly ignore 0 feeds
		if (numNewBytes < 1)
			return true;
		
		// Make sure we're not given more data than we can handle
		int remainingSpace = mBuffer.length - mStreamSize;
		if (numNewBytes > remainingSpace)
			return false;
		
		// May need to move current contents down if there isn't enough contiguous space at the end
		if (numNewBytes > mBuffer.length - (mStreamOffset + mStreamSize))
		{
			// Assuming arraycopy copies from "left to right", i.e. "downhill", should be OK to use it to move contents within same src/dst array
			System.arraycopy(mBuffer, mStreamOffset, mBuffer, 0, mStreamSize);
			mStreamOffset = 0;
		}
		
		// Append to the buffer
		System.arraycopy(newBytes,  0, mBuffer, mStreamOffset + mStreamSize, numNewBytes);
		mStreamSize += numNewBytes;
	
		// We may now have 1 or more complete packets in the buffer
		return consumePackets();
	}
	
	// Clear the contents
	public final void reset()
	{
		mStreamOffset = 0;
		mStreamSize = 0;
	}
	
	// Mandatory override
	abstract protected boolean consumePackets();
}
