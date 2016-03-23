
package com.pietrantuono.activities;

import ioio.lib.api.IOIO;

public interface ActivtyWrapper {
	void onPCBConnectionLostRestartSequence();
	
	boolean isActivityFinishing();

	void onIOIOLooperSetup(IOIO ioio);
	
	void onIOIOLooperDisconnected();

	
	
}
