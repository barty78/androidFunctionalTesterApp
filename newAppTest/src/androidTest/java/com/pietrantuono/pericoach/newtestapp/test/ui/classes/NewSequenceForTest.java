package com.pietrantuono.pericoach.newtestapp.test.ui.classes;


import ioio.lib.api.IOIO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import server.pojos.Job;
import server.pojos.Sequence;
import android.app.Activity;
import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.ioioutils.Current.Scale;
import com.pietrantuono.tests.implementations.AccelerometerSelfTest;
//import com.pietrantuono.tests.implementations.ClosedTestWrapper;
import com.pietrantuono.tests.implementations.CurrentTest;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.pietrantuono.tests.implementations.MagnetWakeDeviceTest;
import com.pietrantuono.tests.implementations.UUTCurrentTest;
import com.pietrantuono.tests.implementations.UploadFirmwareTest;
import com.pietrantuono.tests.implementations.VoltageTest;
import com.pietrantuono.tests.superclass.Test;

public class NewSequenceForTest implements NewSequenceInterface {
	private List<Test> sequence = null;
	private volatile int currentStepNumber = -1;
	private volatile Test currentStep = null;
	private long starttime = 0;
	private long endtime = 0;
	private long jobNo = 0;
	private boolean log = true;

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#Next()
	 */
	@Override
	public synchronized void Next() {
		try {
		} catch (Exception e) {
		}
		currentStepNumber++;
		currentStep = sequence.get(currentStepNumber);

	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#executeCurrentTest()
	 */
	@Override
	public void executeCurrentTest() {
		currentStep.execute();
	}

	public NewSequenceForTest(Activity activity, IOIO ioio, Sequence downlaodedsequence) {
		sequence = new ArrayList<Test>();
		for (int i = 0; i < downlaodedsequence.getTests().size(); i++) {


		}


	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getCurrentTestNumber()
	 */
	@Override
	public synchronized int getCurrentTestNumber() {
		try {
		} catch (Exception e) {
		}
		return currentStepNumber;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getCurrentTest()
	 */
	@Override
	public synchronized Test getCurrentTest() {
		currentStep = sequence.get(currentStepNumber);
		return currentStep;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getNextTest()
	 */
	@Override
	public synchronized Test getNextTest() {
		return sequence.get(currentStepNumber + 1);
	}

	private synchronized int getNexttTestNumber() {
		return currentStepNumber + 1;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getCurrentTestNumberAsString()
	 */
	public synchronized String getCurrentTestNumberAsString() {
		if (currentStepNumber >= 0)
			return Integer.toString(getCurrentTestNumber());
		else
			return Integer.toString(0);
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getCurrentTestDescription()
	 */
	@Override
	public synchronized String getCurrentTestDescription() {
		if (currentStepNumber >= 0)
			return getCurrentTest().getDescription();
		else
			return sequence.get(0).getDescription();
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getNextTestDescription()
	 */
	@Override
	public synchronized String getNextTestDescription() throws Exception {
		if (currentStepNumber >= 0)
			return getNextTest().getDescription();
		else
			return sequence.get(1).getDescription();
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#reset()
	 */
	@Override
	public synchronized void reset() {
		currentStepNumber = -1;
		// currentStep=sequence.get(currentStepNumber);
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getNexttTestNumberAsAString()
	 */
	public synchronized String getNexttTestNumberAsAString() {
		if (currentStepNumber >= 0)
			return Integer.toString(getNexttTestNumber());
		else
			return ("" + 1);
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getEmptyResultsList()
	 */
	public ArrayList<NewMResult> getEmptyResultsList() {
		ArrayList<NewMResult> results = new ArrayList<NewMResult>();
		for (int i = 0; i < sequence.size(); i++) {
			if (!sequence.get(i).isSensorTest())
				results.add(new NewMResult(sequence.get(i)));
			// else if(sequence.get(i) instanceof
			// ClosedTestWrapper)results.add(new
			// ClosedTestResult(sequence.get(i)));
			else
				results.add(new NewMSensorResult(sequence.get(i)));
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getNumberOfSteps()
	 */
	@Override
	public int getNumberOfSteps() {
		return sequence.size();
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#isSequenceEnded()
	 */
	@Override
	public Boolean isSequenceEnded() {
		return currentStepNumber >= sequence.size() - 1;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#isSequenceStarted()
	 */
	public Boolean isSequenceStarted() {
		if (currentStepNumber == -1)
			return false;
		else
			return true;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#stopAll(com.pietrantuono.activities.MainActivity)
	 */
	@Override
	public void stopAll(MainActivity mainActivity) {
		if (sequence == null || sequence.size() <= 0)
			return;
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				for (Test test : sequence) {
					try {
						test.interrupt();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getSequence()
	 */
	@Override
	public List<Test> getSequence() {
		return sequence;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getDuration()
	 */
	@Override
	public long getDuration() {
		return endtime - starttime;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#setStarttime(long)
	 */
	@Override
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#setEndtime(long)
	 */
	@Override
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getJobNo()
	 */
	@Override
	public long getJobNo() {
		return jobNo;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#setJobNo(long)
	 */
	@Override
	public void setJobNo(long jobNo) {
		this.jobNo = jobNo;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getStartTime()
	 */
	@Override
	public String getStartTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss.SSS");
		return dateFormat.format(starttime);
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#getOverallResult()
	 */
	@Override
	public long getOverallResult() {
		long overallresult = 1;
		for (int i = 0; i < sequence.size(); i++) {
			if (sequence.get(i).isSuccess())
				overallresult = 0;
		}

		return overallresult;
	}

	/**
	 * ATTENTION!!! CREATES RANDOM SEQUENCE, FOR TEST ONLY!
	 * 
	 * @param activity
	 * @param ioio
	 */
	public NewSequenceForTest(Activity activity, IOIO ioio) {
		setLog(false);
		sequence = new ArrayList<Test>();
		Job job= new Job(); 
		
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#isLog()
	 */
	@Override
	public boolean isLog() {
		return log;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.constants.NewSequenceInterface#setLog(boolean)
	 */
	@Override
	public void setLog(boolean log) {
		this.log = log;
	}

	

	@Override
	public void addTest(Test test) {
		sequence.add(test);
		
	}
}
