package com.pietrantuono.sensors;

import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;


public interface SensorTestCallback {
	
	@SuppressWarnings("ucd")
	public void onSensorTestCompleted(NewMSensorResult mSensorResult);

	
	@SuppressWarnings("ucd")
	public ProgressAndTextView addFailOrPass(Boolean istest, Boolean success,
			String reading, String description);
}
