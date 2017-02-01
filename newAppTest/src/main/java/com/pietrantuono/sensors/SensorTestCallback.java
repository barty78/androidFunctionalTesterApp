package com.pietrantuono.sensors;

import com.pietrantuono.constants.SensorResult;

import server.pojos.Test;


public interface SensorTestCallback {
	void onSensorTestCompleted(SensorResult mSensorResult, Test testToBeParsed);
	void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, boolean isSensorTest, Test testToBeParsed);
	}
