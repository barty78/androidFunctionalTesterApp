package hydrix.pfmat.generic;

import java.util.ArrayList;

public class SessionSamples
{
	// Represents a single sample as sent by the PFMAT device
	public class Sample
	{
		public final int mTimeOffsetMS;
		public final Force mForce;
		public Sample(int timeOffsetMS, Force force)
		{
			mTimeOffsetMS = timeOffsetMS;
			mForce = force;
		}
	}
	
	// Has a growing cost each time the array is grown (implementation dependent, but typically doubled each time according to most doco)
	// Shouldn't be an issue with data sizes in this application
	public ArrayList<Sample> mSamples;
	public Force mMaxSampleSeen;
	public Force mLastSampleSeen;
	public Force mMinSampleSeen;
	public int mLastSampleMS;
	
	// Construction
	public SessionSamples(int initialCapacity)
	{
		// Create an empty list ready for adding samples to
		mSamples = new ArrayList<Sample>(initialCapacity);
		mMaxSampleSeen = new Force((short)0, (short)0, (short)0);
		mMinSampleSeen = new Force((short)4095, (short)4095, (short)4095);
		mLastSampleSeen = new Force((short)0, (short)0, (short)0);
	}
	
	// Always called from UI thread so no need for synchronisation
	public synchronized final void add(int timeOffsetMS, Force force)
	{
		int[] result = force.compare(mMinSampleSeen, mMaxSampleSeen);
		for (int i = 0; i < result.length; i++) {
			switch (i) {
			case 0:
				if (result[i] > 0)
					mMaxSampleSeen = new Force(force.getLiteralSensor(0), mMaxSampleSeen.getLiteralSensor(1), mMaxSampleSeen.getLiteralSensor(2));
				if (result[i] < 0)
					mMinSampleSeen = new Force(force.getLiteralSensor(0), mMinSampleSeen.getLiteralSensor(1), mMinSampleSeen.getLiteralSensor(2));				
				break;
			case 1:
				if (result[i] > 0)
					mMaxSampleSeen = new Force(mMaxSampleSeen.getLiteralSensor(0), force.getLiteralSensor(1), mMaxSampleSeen.getLiteralSensor(2));
				if (result[i] < 0)
					mMinSampleSeen = new Force(mMinSampleSeen.getLiteralSensor(0), force.getLiteralSensor(1), mMinSampleSeen.getLiteralSensor(2));				
				break;
			case 2:
				if (result[i] > 0)
					mMaxSampleSeen = new Force(mMaxSampleSeen.getLiteralSensor(0), mMaxSampleSeen.getLiteralSensor(1), force.getLiteralSensor(2));
				if (result[i] < 0)
					mMinSampleSeen = new Force(mMinSampleSeen.getLiteralSensor(0), mMinSampleSeen.getLiteralSensor(1), force.getLiteralSensor(2));				
				break;
			}
			
		}
		//Log.d("COMPARE: ", result[0] +"," + result[1] + "," + result[2]);
		mLastSampleSeen = force;
		mSamples.add(new Sample(timeOffsetMS, force));
	}
	
	public synchronized final void clear()
	{
		mMaxSampleSeen = new Force((short)0, (short)0, (short)0);
		mMinSampleSeen = new Force((short)4095, (short)4095, (short)4095);
		mLastSampleSeen = new Force((short)0, (short)0, (short)0);
		mSamples.clear();
	}
	
	// This function must not be called while the session is still in progress, i.e. there is no thread synchronization at this point
	public final ArrayList<Sample> getSamples() {return mSamples;}
	
	public synchronized final Force getMaxSampleSeen() {return mMaxSampleSeen;}
	public synchronized final Force getMinSampleSeen() {return mMinSampleSeen;}
	public synchronized final Force getLastSampleSeen() {return mLastSampleSeen;}
	public synchronized final int getLastSampleMS() {return mLastSampleMS;}
	
	// Returned samples may include one to either side of the specified window
		public synchronized final Sample[] getSamplesForWindow(int windowStartMS, int windowEndMS)
		{
			// Quick check for empty list
			int size = mSamples.size();
			if (size == 0)
				return null;
			
			// Most common - and requiring best performance - use of this method is for displaying samples on the graph, so
			// we'll start at the end of the list, i.e. most recent samples
			int index = size - 1;
			
			// Walk back past all samples that are after the specified range
			while (index >= 0 && mSamples.get(index).mTimeOffsetMS > windowEndMS)
				index--;
			if (index < 0)
				return null;
			
			// Remember the index of the last item that we want to include... we want to include one sample after the window (if available)
			// for the sake of graph interpolation etc
			int last = ((index == size - 1) || (mSamples.get(index).mTimeOffsetMS == windowEndMS)) ? index : index + 1;

			// Keep walking down until we're at/before the start of the window
			while (index > 0 && mSamples.get(index).mTimeOffsetMS > windowStartMS)
				index--;

			// Allocate an array to return the relevant samples in
			int numResults = last - index + 1;
			Sample results[] = new Sample[numResults];
			for (int i = 0; i < numResults; i++)
				results[i] = mSamples.get(index + i);
			return results;
		}
	
}
