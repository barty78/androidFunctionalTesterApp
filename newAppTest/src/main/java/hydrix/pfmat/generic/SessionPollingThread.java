package hydrix.pfmat.generic;

public class SessionPollingThread extends Thread
{
	// Members
	private final Device mDevice;
	private final long mBaseTimeMS;
	private final int mPollFreqMS;
	private final Object mCancelEvent;
	private volatile boolean mCancel;
	
	// Construction
	public SessionPollingThread(Device device, long baseTimeMS, int pollFreqMS)
	{
		mDevice = device;
		mBaseTimeMS = baseTimeMS;
		mPollFreqMS = pollFreqMS;
		mCancelEvent = new Object();
		mCancel = false;
	}
	
	public void run()
	{
		// Raise our priority to keep our sampling rate as deterministic as can be hoped for on Android/Java platform
		setPriority(Thread.MAX_PRIORITY);
		
		// Track the expected sampling time to eliminate drift in the sampling times because of execution time and the non-realtime nature of the platform
		long nextSampleDueMS = System.currentTimeMillis();
		
		// Run until we're told to stop
		while (!mCancel)
		{
			// Relative timestamp
			long nowMS = System.currentTimeMillis();
			int offsetMS = (int)(nowMS - mBaseTimeMS);
			
			// Send a request for a sample to the device
			if (!mDevice.sendGetSensorData(offsetMS))
				break;

			// Wait for a period of time before polling again (keeping an eye on stop event)
			try
			{
				// Tweak the wait time to allow for the elapsed time of our sending the previous sample
				nextSampleDueMS += mPollFreqMS;
				synchronized(mCancelEvent) {mCancelEvent.wait(Math.max(1, nextSampleDueMS - System.currentTimeMillis()));}
			}
			catch (InterruptedException e)
			{
				// Standard case when we're stopped, not an error condition...
				break;
			}
		}
	}
	
	public void cancel()
	{
		// Signal the cancel event, which will cause the worker thread to drop out
		mCancel = true;
		synchronized(mCancelEvent) {mCancelEvent.notify();}
	}
}
