package hydrix.pfmat.generic;

public class Force
{
	// public static final short SENSOR_MAX = 1023; // old 10-bit implementation
	public static final short SENSOR_MAX = 4095; // Now using full 12-bit ADC values
	
	// Members
	public short mSensor0;
	public short mSensor1;
	public short mSensor2;
	
	// Dumb structure to represent the three sensors as percentages of the corresponding values in the user's recorded max force 
	static public class RelativeForce
	{
		public final float mSensor0;
		public final float mSensor1;
		public final float mSensor2;
		public RelativeForce(float p0, float p1, float p2)
		{
			mSensor0 = p0;
			mSensor1 = p1;
			mSensor2 = p2;
		}
		public final float getAverage() {return (mSensor0 + mSensor1 + mSensor2) / 3.0f;}
	}
	
	// Construction
	public Force(short sensor0, short sensor1, short sensor2)
	{
		mSensor0 = sensor0;
		mSensor1 = sensor1;
		mSensor2 = sensor2;
	}
	
	public final boolean isNull() {return (mSensor0 == 0 && mSensor1 == 0 && mSensor2 == 0);}
	
	public final short getLiteralSensor(Integer sensor)
	{
		switch(sensor) {
		case 0: 
			return mSensor0;		
		case 1:
			return mSensor1;
		case 2:
			return mSensor2;
		}
		return 0;
	
	}
	
	// Literal field access
	public final short getLiteralSensor0() {return mSensor0;}
	public final short getLiteralSensor1() {return mSensor1;}
	public final short getLiteralSensor2() {return mSensor2;}

	// Returns the average of the three sensors
	//public final short getSensorsAverageShort() {return (short)(((int)mSensor0 + (int)mSensor1 + (int)mSensor2) / 3);}
	//public final float getSensorsAverageFloat() {return ((float)mSensor0 + (float)mSensor1 + (float)mSensor2) / 3;}
	
	// Percent of max
	public final RelativeForce getAsRelativeForce(Force userBaseline, Force userMaxForce)
	{
		// Floor and ceiling must both be present to be able to compute a relative force!
		if ((userBaseline == null) || (userMaxForce == null))
			return null;
		return new RelativeForce
				(
				getRelativeForce(mSensor0, userBaseline.mSensor0, userMaxForce.mSensor0),
				getRelativeForce(mSensor1, userBaseline.mSensor1, userMaxForce.mSensor1),
				getRelativeForce(mSensor2, userBaseline.mSensor2, userMaxForce.mSensor2)
				);
	}
	
	// Optimisation for analysis, which is currently only interested in primary sensor - no point doing the floating point math for lateral sensors if we're not going to use the values
	public final float getPrimarySensorAsRelativeForce(Force userBaseline, Force userMaxForce)
	{
		// Floor and ceiling must both be present to be able to compute a relative force!
		if ((userBaseline == null) || (userMaxForce == null))
			return 0.0f;
		return getRelativeForce(mSensor0, userBaseline.mSensor0, userMaxForce.mSensor0);
	}
	
	// Helper for getRelativeForce
	private static final float getRelativeForce(short value, short baseline, short max)
	{
		// Don't return negative percentages.. if the force happens to be less than the calibated 'zero' then we'll treat that as zero relative force
		if (value < baseline)
			return 0.0f;
		
		// It's an error for the calibrated rest baseline to be higher than the the recorded max force.. return 0 in this case too
		if (baseline >= max)
			return 0.0f;
		
		// Return % of max (it's valid for this to exceed 100)
		return 100.0f * (float)(value - baseline) / (float)(max - baseline);
	}
	
	// Comparison - All three sensors against min and max
	public final int[] compare(Force min, Force max)
	{
		int[] i = new int[3];
		
		if (mSensor0 < min.mSensor0)
			i[0] = -1;
		else if (mSensor0 > max.mSensor0)
			i[0] = +1;
		else
			i[0] = 0;
		
		if (mSensor1 < min.mSensor1)
			i[1] = -1;
		else if (mSensor1 > max.mSensor1)
			i[1] = +1;
		else
			i[1] = 0;
		
		if (mSensor2 < min.mSensor2)
			i[2] = -1;
		else if (mSensor2 > max.mSensor2)
			i[2] = +1;
		else
			i[2] = 0;
		
		return i;
	}
	
}
