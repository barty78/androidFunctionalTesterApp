package hydrix.pfmat.generic;

import java.util.ArrayList;

public class TestSamples
{
	// Has a growing cost each time the array is grown (implementation dependent, but typically doubled each time according to most doco)
	// Shouldn't be an issue with data sizes in this application
	public ArrayList<String> mTestDesc;
	
	public ArrayList<Force> mMinSamples;
	public ArrayList<Force> mMaxSamples;
	public ArrayList<Force> mAvgSamples;
	
	public ArrayList<Result> mResults;
	
	// Construction
	public TestSamples(int initialCapacity)
	{
		// Create empty lists ready for adding samples to
		
		mTestDesc = new ArrayList<String>();
		mMinSamples = new ArrayList<Force>();
		mMaxSamples = new ArrayList<Force>();
		mAvgSamples = new ArrayList<Force>();
		mResults = new ArrayList<Result>();
	}
	
	// Always called from UI thread so no need for synchronisation
	public synchronized final void add(String testdesc, Force minforce, Force maxforce, Force avgforce, Result result)
	{
		mTestDesc.add(testdesc);
		mMinSamples.add(minforce);
		mMaxSamples.add(maxforce);
		mAvgSamples.add(avgforce);
		mResults.add(result);
	}
	
	public synchronized final void removelast()
	{
		if (mMinSamples.size() > 0) {
			mTestDesc.remove(mTestDesc.size() - 1);
			mMinSamples.remove(mMinSamples.size() - 1);
			mMaxSamples.remove(mMaxSamples.size() - 1);
			mAvgSamples.remove(mAvgSamples.size() - 1);
			mResults.remove(mResults.size() - 1);
		}
	}
		
	
	public synchronized final void clear()
	{
		mTestDesc.clear();
		mMinSamples.clear();
		mMaxSamples.clear();
		mAvgSamples.clear();
		mResults.clear();
		
	}
	
	// This function must not be called while the session is still in progress, i.e. there is no thread synchronization at this point
	public final ArrayList<String> getTestDescs() {return mTestDesc;}
	public final ArrayList<Force> getMinSamples() {return mMinSamples;}
	public final ArrayList<Force> getMaxSamples() {return mMaxSamples;}
	public final ArrayList<Force> getAvgSamples() {return mAvgSamples;}
	public final ArrayList<Result> getResults() {return mResults;}
	
	
	public String sampleSetString(Integer index) {
		
		return mMinSamples.get(index).getLiteralSensor0() + "/" + mMaxSamples.get(index).getLiteralSensor0() + "," + mAvgSamples.get(index).getLiteralSensor0() + " - "
				+ mMinSamples.get(index).getLiteralSensor1() + "/" + mMaxSamples.get(index).getLiteralSensor1() + "," + mAvgSamples.get(index).getLiteralSensor1() + " - "
				+ mMinSamples.get(index).getLiteralSensor2() + "/" + mMaxSamples.get(index).getLiteralSensor2() + "," + mAvgSamples.get(index).getLiteralSensor2() + " = " + mResults.get(index);
	}
	
	public synchronized final void addOneTest(TestSamples sample)
	{
		mTestDesc.add(sample.mTestDesc.get(0));
		mMinSamples.add(sample.mMinSamples.get(0));
		mMaxSamples.add(sample.mMaxSamples.get(0));
		mAvgSamples.add(sample.mAvgSamples.get(0));
		mResults.add(sample.mResults.get(0));
	}
	
}	
