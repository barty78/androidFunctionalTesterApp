package com.pietrantuono.sensors;

import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import server.pojos.Test;


public interface SensorTestCallback {
	public void onSensorTestCompleted(NewMSensorResult mSensorResult, Test testToBeParsed);

	public ProgressAndTextView addFailOrPass(Boolean istest, Boolean success,
			String reading, String description);

	public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, boolean isSensorTest,server.pojos.Test testToBeParsed);

	}
