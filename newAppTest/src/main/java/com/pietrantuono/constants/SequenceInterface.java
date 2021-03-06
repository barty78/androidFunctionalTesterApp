package com.pietrantuono.constants;

import java.util.ArrayList;
import java.util.List;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.tests.superclass.Test;

@SuppressWarnings("unused")
public interface SequenceInterface {

	void executeCurrentTest();

	void executeLastTest();

	int getCurrentTestNumber();

	Test getCurrentTest();

	Test getNextTest();

	Boolean isSequenceStarted();

	String getCurrentTestDescription();

	String getNextTestDescription() throws Exception;

	void reset();

	int getNumberOfSteps();

	Boolean isSequenceEnded();

	void stopAll(MainActivity mainActivity);

	List<Test> getSequence();

	String getDuration();

	void setStarttime(long starttime);

	void setEndtime(long endtime);

	String getJobNo();

	void setJobNo(String jobNo);

	String getStartTime();

	long getOverallResult();

	boolean getOverallResultBool();

//	boolean isLog();

	void setLog(boolean log);

	void addTest(Test testt);

	ArrayList<Result> getEmptyResultsList();

	void deleteUnusedTests();

	String getBT_Addr();

//	Device getDevice();
//
//	void setDevice(Device device);
}