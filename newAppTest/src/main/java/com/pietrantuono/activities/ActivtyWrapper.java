
package com.pietrantuono.activities;

import ioio.lib.api.IOIO;

public interface ActivtyWrapper {
	public void onPCBConnectionLostRestartSequence();
	
	public boolean isActivityFinishing();

	public void onIOIOLooperSetup(IOIO ioio);
	
	public void onIOIOLooperDisconnected();

	
	
}
