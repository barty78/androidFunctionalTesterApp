package com.pietrantuono.constants;

import java.util.ArrayList;
import java.util.List;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.tests.superclass.Test;

public interface NewSequenceInterface {

	void Next();

	void executeCurrentTest();

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

	long getDuration();

	void setStarttime(long starttime);

	void setEndtime(long endtime);

	long getJobNo();

	void setJobNo(long jobNo);

	String getStartTime();

	long getOverallResult();

	boolean getOverallResultBool();

	boolean isLog();

	void setLog(boolean log);

	void addTest(Test testt);

	ArrayList<NewMResult> getEmptyResultsList();

}