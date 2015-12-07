package hydrix.pfmat.generic;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import analytica.pericoach.android.ClosedTestResult;
import analytica.pericoach.android.DBManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//import android.util.Log;

// Singleton session engine
public class SessionRunner {
	Context context;

	// Constants
	public static final int SAMPLING_HZ = 10;
	private static final int CALIBRATION_MIN_SAMPLES = 20;
	private static final int INITIAL_FREEMODE_SAMPLE_CAPACITY = 512;

	// Members
	private static SessionRunner mInstance = null;

	private Device mDevice;
	private String mDeviceId;

	private SessionSamples mSamples;
	private TestSamples mTestSamples;

	private ArrayList<TestLimits> mTestLimits;
	private ArrayList<Result[]> mTestResults;

	public ClosedTestResult mTestResult;

	private Force mUserMaxForce;
	private Force mUserMinForce;
	private Force mUserBaseline;
	private Force mRunningAvg;
	private ArrayList<Force> mMinMaxAvg;
	private SessionPollingThread mPollingThread;
	private Date mStartTime;
	private long mStartTimeMS;

	private Integer mTestCounter;
	private Integer mInputCounter;
	private Integer mTestType;

	private ArrayList<Short> mSensorAvgTemp;
	private ArrayList<Short> mSensorMinTemp;
	private ArrayList<Short> mSensorMaxTemp;

	private DisplaySample mListSample;
	private ArrayList<DisplaySample> mListSamples;

	public SessionRunner(Context context) {
		this.context = context;

	}

	// Programme path can be null (for "free" mode)
	public static SessionRunner createSession(Integer testType,
			ArrayList<TestLimits> testLimits) {
		// Create instance and load any specified programme defn
		SessionRunner runner = new SessionRunner(testType, testLimits);

		if (!runner.prepare())
			return null;

		// Successfully started, track the singleton instance
		mInstance = runner;
		return mInstance;
	}

	// Returns null if there is no active session
	public static SessionRunner getActiveSession() {
		return mInstance;
	}

	// Protected construction (via startSession)
	protected SessionRunner(Integer testType, ArrayList<TestLimits> testLimits) {
		mTestType = testType;
		mTestCounter = 0;
		mTestLimits = testLimits;
		mTestResults = new ArrayList<Result[]>();
		mDeviceId = "";
		mSamples = null;
		mTestSamples = null;
		mListSamples = new ArrayList<DisplaySample>();
		// mUserMaxForce = userMaxForce;
		mUserMaxForce = new Force((short) 0, (short) 0, (short) 0);
		mUserBaseline = new Force((short) 0, (short) 0, (short) 0);
		mUserMinForce = new Force((short) 4095, (short) 4095, (short) 4095);
		// mMinMaxAvg = new ArrayList<Force>();
		mSensorAvgTemp = new ArrayList<Short>();
		mSensorMinTemp = new ArrayList<Short>();
		mSensorMaxTemp = new ArrayList<Short>();
		mPollingThread = null;
		mStartTime = null;
		mStartTimeMS = 0;
	}

	protected final boolean prepare() {
		// In all cases we prepare a buffer for force samples from the device
		int initialSampleCapacity = INITIAL_FREEMODE_SAMPLE_CAPACITY;
		mSamples = new SessionSamples(initialSampleCapacity);
		mTestCounter = 0;
		mInputCounter = 0;
		mTestSamples = new TestSamples(initialSampleCapacity);

		// Make sure we start with no samples
		mSamples.clear();
		Result[] tmpResult = new Result[3];
		for (int i = 0; i < 3; i++) {
			tmpResult[i] = new Result(TEST.CLEAR);
		}

		switch (mTestType) {
		case TEST.NO_TEST:
			break;
		case TEST.OPEN_TEST:
			mTestResult = new ClosedTestResult();
			Log.d("TEST: ", "NOT COMPLETE");
			mTestResult.setComplete(false);
			// mTestResult.setDevId(mDeviceId);
			mTestResult.setOperator("P.Bartlett");
			Log.d("TEST-ID: ",
					Integer.toString(mTestLimits.get(mTestCounter).getTestID()));
			mTestResult.setTestId(mTestLimits.get(mTestCounter).getTestID());
			break;
		case TEST.CLOSED_TEST:

			mTestResult = new ClosedTestResult();
			Log.d("TEST: ", "NOT COMPLETE");
			mTestResult.setComplete(false);
			// mTestResult.setDevId(mDeviceId);
			mTestResult.setOperator("P.Bartlett");
			Log.d("TEST-ID: ",
					Integer.toString(mTestLimits.get(mTestCounter).getTestID()));
			mTestResult.setTestId(mTestLimits.get(mTestCounter).getTestID());
			break;
		}

		return true;
	}

	public final float sensorAvg(int sensor, int windowSize) {

		ArrayList<SessionSamples.Sample> samples = mSamples.getSamples();

		if (samples == null || samples.size() < CALIBRATION_MIN_SAMPLES)
			return -1;

		// Total all the samples and compute the average
		float sensor0 = 0.0f;
		float sensor1 = 0.0f;
		float sensor2 = 0.0f;
		int numSamples = samples.size();
		for (int i = numSamples - windowSize; i < numSamples; i++) {
			Force force = samples.get(i).mForce;
			sensor0 += force.getLiteralSensor0();
			sensor1 += force.getLiteralSensor1();
			sensor2 += force.getLiteralSensor2();
		}

		mRunningAvg = new Force((short) (sensor0 / windowSize),
				(short) (sensor1 / windowSize), (short) (sensor2 / windowSize));

		return mRunningAvg.getLiteralSensor(sensor);
	}

	public final void beginBaselineCalibration(Device device) {
		// Make sure we start with no samples
		mSamples.clear();

		if (mInputCounter == -1) {
			mInputCounter = 0;
		}

		if (mPollingThread == null) {

			// Store the device ID used for this session
			mDeviceId = device.getDeviceId();

			// Kick off the polling thread to get some samples!
			mPollingThread = new SessionPollingThread(device,
					System.currentTimeMillis(), 1000 / SAMPLING_HZ);
			mPollingThread.start();
		}
	}

	public final boolean endBaselineCalibration() {
		// Make sure we got a minimum number of samples to be able to calculate
		// the baseline
		ArrayList<SessionSamples.Sample> samples = mSamples.getSamples();
		Log.d("SAMPLES SIZE: ", Integer.toString(samples.size()));
		if (samples == null || samples.size() < CALIBRATION_MIN_SAMPLES)
			return false;

		// Total all the samples and compute the average
		float sensor0 = 0.0f;
		float sensor1 = 0.0f;
		float sensor2 = 0.0f;
		int numSamples = samples.size();
		for (int i = 0; i < numSamples; i++) {
			Force force = samples.get(i).mForce;
			sensor0 += force.getLiteralSensor0();
			sensor1 += force.getLiteralSensor1();
			sensor2 += force.getLiteralSensor2();
		}
		if (mTestCounter == 0) {
			// Test 0 should always be at rest test, so we can sample all at
			// once
			sampleAll(sensor0, sensor1, sensor2, numSamples);
			nextTest();

		} else {
			switch (mTestType) {
			// Open Test - All sensors samples simultaneously
			case TEST.OPEN_TEST:
				sampleAll(sensor0, sensor1, sensor2, numSamples);
				nextTest();
				break;

			case TEST.CLOSED_TEST:
				// Closed Test - Sensors sampled individually when weight
				// applied (mTestCounter > 0)

				sampleIndividually(sensor0, sensor1, sensor2, numSamples, false);

				if (mInputCounter == 2) {
					nextTest();
					break;
				}

				mInputCounter++;
				break;
			}
		}

		// Now clear the samples now that we've consumed them
		mSamples.clear();
		return true;
	}

	public final void sampleAll(float sensor0, float sensor1, float sensor2,
			int numSamples) {
			
		mUserMaxForce = mSamples.getMaxSampleSeen();
		mUserMinForce = mSamples.getMinSampleSeen();
		mUserBaseline = new Force((short) (sensor0 / numSamples),
				(short) (sensor1 / numSamples), (short) (sensor2 / numSamples));
		// mTestSamples.add("Rest", mUserMinForce, mUserMaxForce, mUserBaseline,
		// new Result());
		Result[] tmpResult = new Result[3];
		for (mInputCounter = 0; mInputCounter < 3; mInputCounter++) {

			mSensorMinTemp.add(mUserMinForce.getLiteralSensor(mInputCounter));
			mSensorMaxTemp.add(mUserMaxForce.getLiteralSensor(mInputCounter));
			mSensorAvgTemp.add(mUserBaseline.getLiteralSensor(mInputCounter));

			// Check individual sensor samples against sensor test limits
			tmpResult[mInputCounter]	 = compareForces();
			Log.d("RESULT: ", tmpResult[mInputCounter].toString());

		}
		mTestResults.add(tmpResult);

		mListSample = new DisplaySample(mTestLimits.get(mTestCounter).getDesc(), mUserMaxForce,	mUserMinForce, mUserBaseline, tmpResult);
		//mListSamples.add(mTestCounter, mListSample); //NOTA BENE tolta
		mListSamples.set(mTestCounter, mListSample);//NOTA BENE aggiunta
		
	}

	public final void sampleIndividually(float sensor0, float sensor1,
			float sensor2, int numSamples, Boolean display) {
		//display is TRUE when called by ClosedForceActivity
		int tmpCounter;
		// Result tmpResult = new Result();
		if (display) {
			if (mInputCounter > -1) {
				tmpCounter = mInputCounter - 1;
			} else {
				tmpCounter = mInputCounter;
			}
		} else {
			tmpCounter = mInputCounter;
		}

		// Log.d("Input:", Integer.toString(tmpCounter));
		switch (tmpCounter) {
		case -1: // Not Started
			mListSample.setMaxForce(new Force(getSamples().getMaxSampleSeen()
					.getLiteralSensor(0), getSamples().getMaxSampleSeen()
					.getLiteralSensor(1), getSamples().getMaxSampleSeen()
					.getLiteralSensor(2)));
			mListSample.setMinForce(new Force(getSamples().getMinSampleSeen()
					.getLiteralSensor(0), getSamples().getMinSampleSeen()
					.getLiteralSensor(1), getSamples().getMinSampleSeen()
					.getLiteralSensor(2)));
			mListSample.setAvgForce(new Force(getSamples().getLastSampleSeen()
					.getLiteralSensor(0), getSamples().getLastSampleSeen()
					.getLiteralSensor(1), getSamples().getLastSampleSeen()
					.getLiteralSensor(2)));
			break;
		case 0: // Sensor 0

			// mTestResults.add(new Result[3]);

			if (!display) {

				mSensorMinTemp.add(mSamples.getMinSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorMaxTemp.add(mSamples.getMaxSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorAvgTemp.add((short) (sensor0 / numSamples));

				// Check individual sensor samples against sensor test limits
				mTestResults.set(
						mTestCounter,
						new Result[] { compareForces(),
								mTestResults.get(mTestCounter)[1],
								mTestResults.get(mTestCounter)[2] });

			}

			mUserMinForce = (new Force(mSensorMinTemp.get(0), (short) 0,
					(short) 0));
			mUserMaxForce = (new Force(mSensorMaxTemp.get(0), (short) 0,
					(short) 0));
			mUserBaseline = (new Force(mSensorAvgTemp.get(0), (short) 0,
					(short) 0));

			mListSample.setMaxForce(new Force(getUserMaxForce()
					.getLiteralSensor(0), getSamples().getMaxSampleSeen()
					.getLiteralSensor(1), getSamples().getMaxSampleSeen()
					.getLiteralSensor(2)));
			mListSample.setMinForce(new Force(getUserMinForce()
					.getLiteralSensor(0), getSamples().getMinSampleSeen()
					.getLiteralSensor(1), getSamples().getMinSampleSeen()
					.getLiteralSensor(2)));
			mListSample.setAvgForce(new Force(getUserBaseline()
					.getLiteralSensor(0), getSamples().getLastSampleSeen()
					.getLiteralSensor(1), getSamples().getLastSampleSeen()
					.getLiteralSensor(2)));

			break;

		case 1: // Sensor 1
			if (!display) {

				mSensorMinTemp.add(mSamples.getMinSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorMaxTemp.add(mSamples.getMaxSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorAvgTemp.add((short) (sensor1 / numSamples));

				// Check individual sensor samples against sensor test limits
				mTestResults.set(mTestCounter,
						new Result[] { mTestResults.get(mTestCounter)[0],
						compareForces(),
						mTestResults.get(mTestCounter)[2] });

			}

			mUserMinForce = (new Force(mSensorMinTemp.get(0),
					mSensorMinTemp.get(1), (short) 0));
			mUserMaxForce = (new Force(mSensorMaxTemp.get(0),
					mSensorMaxTemp.get(1), (short) 0));
			mUserBaseline = (new Force(mSensorAvgTemp.get(0),
					mSensorAvgTemp.get(1), (short) 0));

			mListSample.setMaxForce(new Force(getUserMaxForce()
					.getLiteralSensor(0),
					getUserMaxForce().getLiteralSensor(1), getSamples()
					.getMaxSampleSeen().getLiteralSensor(2)));
			mListSample.setMinForce(new Force(getUserMinForce()
					.getLiteralSensor(0),
					getUserMinForce().getLiteralSensor(1), getSamples()
					.getMinSampleSeen().getLiteralSensor(2)));
			mListSample.setAvgForce(new Force(getUserBaseline()
					.getLiteralSensor(0),
					getUserBaseline().getLiteralSensor(1), getSamples()
					.getLastSampleSeen().getLiteralSensor(2)));

			break;

		case 2: // Sensor 2
			if (!display) {

				mSensorMinTemp.add(mSamples.getMinSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorMaxTemp.add(mSamples.getMaxSampleSeen()
						.getLiteralSensor(tmpCounter));
				mSensorAvgTemp.add((short) (sensor2 / numSamples));

				// Check individual sensor samples against sensor test limits
				mTestResults.set(mTestCounter,
						new Result[] { mTestResults.get(mTestCounter)[0],
						mTestResults.get(mTestCounter)[1],
						compareForces() });

			}

			mUserMinForce = (new Force(mSensorMinTemp.get(0),
					mSensorMinTemp.get(1), mSensorMinTemp.get(2)));
			mUserMaxForce = (new Force(mSensorMaxTemp.get(0),
					mSensorMaxTemp.get(1), mSensorMaxTemp.get(2)));
			mUserBaseline = (new Force(mSensorAvgTemp.get(0),
					mSensorAvgTemp.get(1), mSensorAvgTemp.get(2)));

			mListSample.setMaxForce(new Force(getUserMaxForce()
					.getLiteralSensor(0),
					getUserMaxForce().getLiteralSensor(1), getUserMaxForce()
					.getLiteralSensor(2)));
			mListSample.setMinForce(new Force(getUserMinForce()
					.getLiteralSensor(0),
					getUserMinForce().getLiteralSensor(1), getUserMinForce()
					.getLiteralSensor(2)));
			mListSample.setAvgForce(new Force(getUserBaseline()
					.getLiteralSensor(0),
					getUserBaseline().getLiteralSensor(1), getUserBaseline()
					.getLiteralSensor(2)));

			break;
		}
		if (!display) {
			mListSample.setResults(getTestResults().get(mTestCounter));
		}
		if (mInputCounter != -1) {
			mListSamples.set(mTestCounter, mListSample);
		}
	}

	public final Boolean clearLast() {

		Log.d("INPUT#", Integer.toString(mInputCounter));
		// if (mTestType == TEST.OPEN_TEST) {}
		if (false) {
		} else {
			if (mTestCounter > 0) {
				if (mInputCounter == -1) {
					//mTestSamples.removelast(); //NB Commentata
					mTestCounter--;
					mListSamples.clear();
					// mListSamples.remove(mListSamples.size() - 1);
				} else {
					// TODO - Need to clear the last result in mTestResults.
					// mTestResults.set(mTestCounter);
					try {
						mSensorMinTemp.remove(mSensorMinTemp.size() - 1);
						mSensorMaxTemp.remove(mSensorMaxTemp.size() - 1);
						mSensorAvgTemp.remove(mSensorAvgTemp.size() - 1);
					} catch (Exception e) {
						Log.e("", e.toString());
					}
					if (!mTestResult.isComplete()) {
						mInputCounter--;
					}

					Log.d("INPUT: ", Integer.toString(mInputCounter));
					switch (mInputCounter) {
					case 0:
						mTestResults.set(mTestCounter, new Result[] {
								new Result(TEST.CLEAR), new Result(TEST.CLEAR),
								new Result(TEST.CLEAR) });
						mInputCounter = -1;
						break;
					case 1:
						mTestResults.set(mTestCounter,
								new Result[] {
								mTestResults.get(mTestCounter)[0],
								new Result(TEST.CLEAR),
								new Result(TEST.CLEAR) });
						break;
					case 2:
						mTestResults.set(mTestCounter,
								new Result[] {
								mTestResults.get(mTestCounter)[0],
								mTestResults.get(mTestCounter)[1],
								new Result(TEST.CLEAR) });
						break;
					}
					mListSample.setResults(getTestResults().get(mTestCounter));
				}
			}
		}
		mTestResult.setComplete(false);

		return true;
	}

	public final Result compareForces() {
		// Check samples are
		/*
		 * Log.d("INPUT COUNTER: ", Integer.toString(mInputCounter));
		 * Log.d("TEST COUNTER: ", Integer.toString(mTestCounter));
		 * Log.d("LIMITS: ", Integer.toString(mTestLimits.size()));
		 * Log.d("LIMITS DESC: ", mTestLimits.get(mTestCounter).getDesc());
		 * Log.d("LIMITS LOWER: ",
		 * Integer.toString(mTestLimits.get(mTestCounter)
		 * .getLowerLimits().getLiteralSensor(mInputCounter)));
		 * Log.d("LIMITS UPPER: ",
		 * Integer.toString(mTestLimits.get(mTestCounter)
		 * .getUpperLimits().getLiteralSensor(mInputCounter)));
		 * Log.d("LIMITS STABILITY: ",
		 * Integer.toString(mTestLimits.get(mTestCounter).getStability()));
		 * Log.d("SAMPLEAVG: ", Integer.toString(mSensorAvgTemp.size()));
		 * Log.d("SAMPLEMIN: ", Integer.toString(mSensorMinTemp.size()));
		 * Log.d("SAMPLEMAX: ", Integer.toString(mSensorMaxTemp.size()));
		 */

		if ((mSensorAvgTemp.get(mInputCounter) < mTestLimits.get(mTestCounter)
				.getUpperLimits().getLiteralSensor(mInputCounter))
				&& (mSensorAvgTemp.get(mInputCounter) > mTestLimits
						.get(mTestCounter).getLowerLimits()
						.getLiteralSensor(mInputCounter))
						&& Math.abs((int) ((mSensorMaxTemp.get(mInputCounter) - mSensorMinTemp
								.get(mInputCounter)))) < mTestLimits.get(mTestCounter)
								.getStability()) {
			return new Result(TEST.PASS);
		} else {
			return new Result(TEST.FAIL);
		}
	}

	public final void nextTest() {

		//
		// TODO - Populate the test result properly - Hard Coded for the moment
		if (mTestResults.get(mTestCounter)[0].isPass()
				&& mTestResults.get(mTestCounter)[1].isPass()
				&& mTestResults.get(mTestCounter)[2].isPass()) 
		{
			
			mTestSamples.add(mTestLimits.get(mTestCounter).getDesc(),
					mUserMinForce, mUserMaxForce, mUserBaseline, new Result(
							TEST.PASS));
		} 
		
		else {
			// TODO - Populate the test result properly - Hard Coded for the
			// moment
			//NOTA BENE
			if(mTestType==TEST.OPEN_TEST)mTestSamples.add(mTestLimits.get(mTestCounter).getDesc(),mUserMinForce, mUserMaxForce, mUserBaseline, new Result(TEST.FAIL));
		}
		Log.d("TEST#: ", "Test# " + Integer.toString(mTestCounter + 1) + " of "
				+ Integer.toString(mTestLimits.size()));

		// NOTA BENE
		if (mTestType == 2)// Closed Test
		{
			if (mTestCounter < mTestLimits.size() - 1) {

				mTestCounter++;
				mInputCounter = -1;

				mSensorMinTemp.clear();
				mSensorMaxTemp.clear();
				mSensorAvgTemp.clear();
				Result[] tmpResult = new Result[3];
				for (int i = 0; i < 3; i++) {
					tmpResult[i] = new Result(TEST.CLEAR);
				}

				mListSample = new DisplaySample(mTestLimits.get(mTestCounter)
						.getDesc(), new Force((short) 0, (short) 0, (short) 0),
						new Force((short) 0, (short) 0, (short) 0), new Force(
								(short) 0, (short) 0, (short) 0), tmpResult);
				mListSamples.add(mListSample);
				mTestResults.add(tmpResult);

			} else {
				Log.d("TEST: ", "Set Complete");
				mTestResult.setComplete(true);

				// Log.d("TESTING:", "FINALISE");
				// finaliseTesting();
			}
		}
		if (mTestType == 1)// If Open Test
			{
			if (mTestCounter >= 2){
			mTestCounter++;
			mInputCounter = -1;
			}
			if (mTestCounter < 2)// Limits of Open Test 3: rest, low, high
			{
				mTestCounter++;
				mInputCounter = -1;
				
				mSensorMinTemp.clear();
				mSensorMaxTemp.clear();
				mSensorAvgTemp.clear();
				Result[] tmpResult = new Result[3];
				for (int i = 0; i < 3; i++) {
					tmpResult[i] = new Result(TEST.CLEAR);
				}

				mListSample = new DisplaySample(mTestLimits.get(mTestCounter)
						.getDesc(), new Force((short) 0, (short) 0, (short) 0),
						new Force((short) 0, (short) 0, (short) 0), new Force(
								(short) 0, (short) 0, (short) 0), tmpResult);
				//NOTA BENE
				mListSamples.add(mListSample);//Added
				mTestResults.add(tmpResult);//added
				
			} 
			//if (mTestCounter <= 2)// Limits of Open Test 3: rest, low, high
			{
				mSensorMinTemp.clear();
				mSensorMaxTemp.clear();
				mSensorAvgTemp.clear();
				Result[] tmpResult = new Result[3];
				for (int i = 0; i < 3; i++) {
					tmpResult[i] = new Result(TEST.CLEAR);
				}
				
				mTestResults.add(tmpResult);
				
			} 
			
			if (mTestCounter > 2) 
			{
				Log.d("TEST: ", "Set Complete");
				mTestResult.setComplete(true);
				// Log.d("TESTING:", "FINALISE");
				// finaliseTesting();
			}
			
		}
	}

	public final void finaliseTesting() {

		// Stop the polling
		if (mPollingThread != null) {
			mPollingThread.cancel();
			mPollingThread = null;
		}
		// mTestResult = new ClosedTestResult();
		for (int i = 0; i < mTestSamples.getMinSamples().size(); i++) {
			Log.d("SAMPLES " + Integer.toString(i) + ": ",
					mTestSamples.sampleSetString(i));
		}
		//NOTA BENE
		if(mTestType==TEST.CLOSED_TEST)mTestSamples.add(mTestLimits.get(mTestCounter).getDesc(),mUserMinForce, mUserMaxForce, mUserBaseline, new Result(TEST.FAIL));
		// mTestResult.setDevId(mDeviceId);
		mTestResult.setSamples(mTestSamples);
		mTestResult.setFinalResult();
	}

	public final boolean start(Device device) {
		// In all cases we prepare a buffer for force samples from the device
		int initialSampleCapacity = INITIAL_FREEMODE_SAMPLE_CAPACITY;
		mSamples = new SessionSamples(initialSampleCapacity);

		// Store the device ID used for this session
		mDeviceId = device.getDeviceId();

		// Make sure we start with no samples
		mSamples.clear();
		Result[] tmpResult = new Result[3];
		for (int i = 0; i < 3; i++) {
			tmpResult[i] = new Result(TEST.CLEAR);
		}

		switch (mTestType) {
		case TEST.NO_TEST:
			break;
		case TEST.OPEN_TEST:
			mListSample = new DisplaySample(mTestLimits.get(mTestCounter)
					.getDesc(), new Force((short) 0, (short) 0, (short) 0),
					new Force((short) 0, (short) 0, (short) 0), new Force(
							(short) 0, (short) 0, (short) 0), tmpResult);
			mListSamples.add(mListSample);

			break;
		case TEST.CLOSED_TEST:
			mListSample = new DisplaySample(mTestLimits.get(mTestCounter)
					.getDesc(), new Force((short) 0, (short) 0, (short) 0),
					new Force((short) 0, (short) 0, (short) 0), new Force(
							(short) 0, (short) 0, (short) 0), tmpResult);
			mListSamples.add(mListSample);

			mTestResult = new ClosedTestResult();
			Log.d("TEST: ", "NOT COMPLETE");
			mTestResult.setComplete(false);
			// mTestResult.setDevId(mDeviceId);
			mTestResult.setOperator("P.Bartlett");
			Log.d("TEST-ID: ",
					Integer.toString(mTestLimits.get(mTestCounter).getTestID()));
			mTestResult.setTestId(mTestLimits.get(mTestCounter).getTestID());
			break;
		}

		// Kick off the polling thread to get some samples!
		mStartTimeMS = System.currentTimeMillis();
		mStartTime = new Date(mStartTimeMS);
		mPollingThread = new SessionPollingThread(device, mStartTimeMS,
				1000 / SAMPLING_HZ);
		mPollingThread.start();

		return true;
	}

	public final void stop() {
		// Stop polling
		if (mPollingThread != null) {
			mPollingThread.cancel();
			mPollingThread = null;
		}
	}

	public final ArrayList<Boolean> prepareResults(
			ArrayList<TestLimits> testLimits, TestSamples testSamples) {

		return null;
	}

	public final void removeLast() {
		mSensorMaxTemp.remove(mSensorMaxTemp.size() - 1);
		mSensorMinTemp.remove(mSensorMinTemp.size() - 1);
		mSensorAvgTemp.remove(mSensorAvgTemp.size() - 1);
		mTestResults.remove(mTestResults.size() - 1);
		mInputCounter--;
	}

	public final String getDeviceId() {
		return mDeviceId;
	}
	
	public final SessionSamples getSamples() {
		return mSamples;
	}

	public final TestSamples getTestSamples() {
		return mTestSamples;
	}

	public final ArrayList<Result[]> getTestResults() {
		return mTestResults;
	}

	public final ArrayList<DisplaySample> getDisplaySamples() {
		return mListSamples;
	}

	public final Force getUserBaseline() {
		return mUserBaseline;
	}

	public final Force getUserMaxForce() {
		return mUserMaxForce;
	}

	public final Force getUserMinForce() {
		return mUserMinForce;
	}

	public final long getStartTimeMS() {
		return mStartTimeMS;
	}

	public final Integer getInputCount() {
		return mInputCounter;
	}

	public final void setInputCount(Integer inputCount) {
		mInputCounter = inputCount;
	}

	public final Integer getTestCount() {
		return mTestCounter;
	}

	public final void setTestCount(Integer testCount) {
		mTestCounter = testCount;
	}

	public final void onSample(int timeOffsetMS, short sensor0, short sensor1,
			short sensor2) {
		if (mPollingThread != null && mSamples != null)
			mSamples.add(timeOffsetMS, new Force(sensor0, sensor1, sensor2));
	}
	public int getmTestCounter(){
		return mTestCounter;
	}
	
	public  void loadTestresults() {

		// Stop the polling
		/*
		if (mPollingThread != null) {
			mPollingThread.cancel();
			mPollingThread = null;
		}
		*/
		// mTestResult = new ClosedTestResult();
		for (int i = 0; i < mTestSamples.getMinSamples().size(); i++) {
			Log.d("SAMPLES " + Integer.toString(i) + ": ",
					mTestSamples.sampleSetString(i));
		}
		// mTestResult.setDevId(mDeviceId);
		mTestResult.setSamples(mTestSamples);
		
		mTestResult.setFinalResult();
		
	}
	public void setCounteToZero(){
		mTestCounter =0;
	}

	public void updateSamples() {
		
		mListSample.setMaxForce(new Force(getSamples().getMaxSampleSeen()
				.getLiteralSensor(0), getSamples().getMaxSampleSeen()
				.getLiteralSensor(1), getSamples().getMaxSampleSeen()
				.getLiteralSensor(2)));
		mListSample.setMinForce(new Force(getSamples().getMinSampleSeen()
				.getLiteralSensor(0), getSamples().getMinSampleSeen()
				.getLiteralSensor(1), getSamples().getMinSampleSeen()
				.getLiteralSensor(2)));
		mListSample.setAvgForce(new Force(getSamples().getLastSampleSeen()
				.getLiteralSensor(0), getSamples().getLastSampleSeen()
				.getLiteralSensor(1), getSamples().getLastSampleSeen()
				.getLiteralSensor(2)));
		
	}
	
	public void startSession(){
		mSamples.clear();
		Result[] tmpResult = new Result[3];
		for (int i = 0; i < 3; i++) {
			tmpResult[i] = new Result(TEST.CLEAR);
		}
		mListSample = new DisplaySample(mTestLimits.get(mTestCounter)
				.getDesc(), new Force((short) 0, (short) 0, (short) 0),
				new Force((short) 0, (short) 0, (short) 0), new Force(
						(short) 0, (short) 0, (short) 0), tmpResult);
		mListSamples.add(mListSample);
	}
	public void setmTestCounter(int i){
		mTestCounter =i;
	}
	
	
	public final Boolean clearLastOpenTest() {
		Result[] tmpResult = new Result[3];
		for (int i = 0; i < 3; i++) {
			tmpResult[i] = new Result(TEST.CLEAR);
		}
		
		//mListSamples.add(mListSample);
	
		if(mTestCounter >=1){
			//mTestSamples.removelast();
			mTestResult.removeLast();
			if(mTestCounter==2)
			{
				mListSample = new DisplaySample(mTestLimits.get(0)
						.getDesc(), new Force((short) 0, (short) 0, (short) 0),
						new Force((short) 0, (short) 0, (short) 0), new Force(
								(short) 0, (short) 0, (short) 0), tmpResult);
			mListSamples.remove(2);
			mListSamples.remove(1);
			mListSamples.add(mListSample);
			}
			
			if(mTestCounter==1)
			{
			mListSample = new DisplaySample(mTestLimits.get(0)
						.getDesc(), new Force((short) 0, (short) 0, (short) 0),
						new Force((short) 0, (short) 0, (short) 0), new Force(
								(short) 0, (short) 0, (short) 0), tmpResult);
			mListSamples.remove(1);
			mListSamples.remove(0);
			mListSamples.add(mListSample);
			}
			mTestCounter--;
			mInputCounter = -1;
			try {
				mSensorMinTemp.remove(mSensorMinTemp.size() - 1);
				mSensorMaxTemp.remove(mSensorMaxTemp.size() - 1);
				mSensorAvgTemp.remove(mSensorAvgTemp.size() - 1);
			} catch (Exception e) {
				Log.e("", e.toString());
			}
			mTestResult.setComplete(false);
			mTestResults.set(mTestCounter, new Result[] {
					new Result(TEST.CLEAR), new Result(TEST.CLEAR),
					new Result(TEST.CLEAR) });
			mTestResults.set(mTestCounter,
					new Result[] {
					mTestResults.get(mTestCounter)[0],
					new Result(TEST.CLEAR),
					new Result(TEST.CLEAR) });
			mTestResults.set(mTestCounter,
					new Result[] {
					mTestResults.get(mTestCounter)[0],
					mTestResults.get(mTestCounter)[1],
					new Result(TEST.CLEAR) });
			mListSample.setResults(getTestResults().get(mTestCounter));
		}

		return true;
	}
}
