package com.pietrantuono.sensors;

import com.pietrantuono.constants.NewMSensorResult;

import server.pojos.Test;


public interface SensorTestCallback {
	void onSensorTestCompleted(NewMSensorResult mSensorResult, Test testToBeParsed);
	void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, boolean isSensorTest, Test testToBeParsed);
	}
