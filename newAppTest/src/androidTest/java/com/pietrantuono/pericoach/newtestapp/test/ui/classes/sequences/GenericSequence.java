package com.pietrantuono.pericoach.newtestapp.test.ui.classes.sequences;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.tests.superclass.Test;

public class GenericSequence implements NewSequenceInterface {
	private List<Test> sequence = null;
	private volatile int currentStepNumber = -1;
	private volatile Test currentStep = null;
	private long starttime = 0;
	private long endtime = 0;
	private long jobNo = 0;
	private boolean log = true;

	
	@Override
	public synchronized void Next() {
		try {
		} catch (Exception e) {
		}
		currentStepNumber++;
		currentStep = sequence.get(currentStepNumber);

	}

	
	@Override
	public void executeCurrentTest() {
		currentStep.execute();
	}

	
	@Override
	public synchronized int getCurrentTestNumber() {
		try {
		} catch (Exception e) {
		}
		return currentStepNumber;
	}
	@Override
	public synchronized Test getCurrentTest() {
		currentStep = sequence.get(currentStepNumber);
		return currentStep;
	}

	@Override
	public synchronized Test getNextTest() {
		return sequence.get(currentStepNumber + 1);
	}

	private synchronized int getNexttTestNumber() {
		return currentStepNumber + 1;
	}

	@Override
	public synchronized String getCurrentTestDescription() {
		if (currentStepNumber >= 0)
			return getCurrentTest().getDescription();
		else
			return sequence.get(0).getDescription();
	}

	@Override
	public synchronized String getNextTestDescription() throws Exception {
		if (currentStepNumber >= 0)
			return getNextTest().getDescription();
		else
			return sequence.get(1).getDescription();
	}

	@Override
	public synchronized void reset() {
		currentStepNumber = -1;
		// currentStep=sequence.get(currentStepNumber);
	}


	public ArrayList<NewMResult> getEmptyResultsList() {
		ArrayList<NewMResult> results = new ArrayList<NewMResult>();
		for (int i = 0; i < sequence.size(); i++) {
			if (!sequence.get(i).isSensorTest())
				results.add(new NewMResult(sequence.get(i)));
			else
				results.add(new NewMSensorResult(sequence.get(i)));
		}
		return results;
	}

	@Override
	public int getNumberOfSteps() {
		return sequence.size();
	}

	@Override
	public Boolean isSequenceEnded() {
		return currentStepNumber >= sequence.size() - 1;
	}

	public Boolean isSequenceStarted() {
		if (currentStepNumber == -1)
			return false;
		else
			return true;
	}

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

	@Override
	public List<Test> getSequence() {
		return sequence;
	}

	@Override
	public long getDuration() {
		return endtime - starttime;
	}

	@Override
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	@Override
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	@Override
	public long getJobNo() {
		return jobNo;
	}

	@Override
	public void setJobNo(long jobNo) {
		this.jobNo = jobNo;
	}

	@Override
	public String getStartTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss.SSS");
		return dateFormat.format(starttime);
	}

	@Override
	public long getOverallResult() {
		long overallresult = 1;
		for (int i = 0; i < sequence.size(); i++) {
			if (sequence.get(i).isSuccess())
				overallresult = 0;
		}

		return overallresult;
	}


	@Override
	public void setLog(boolean log) {
		this.log = log;
	}
	@Override
	public void addTest(Test test){
		sequence= new ArrayList<Test>();
		sequence.add(test);
	}
}
