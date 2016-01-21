
package com.pietrantuono.activities;

import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import ioio.lib.api.IOIO;

public interface ActivtyWrapper {
	public void onPCBConnectionLostRestartSequence();
	
	public boolean isActivityFinishing();
	
	/**
	 * @param text
	 * @param lenght (Toast.LENGTH_LONG or Toast.LENGTH_SHORT)
	 */
	public void toast(String text, int lenght);

	public void goAndExecuteNextTest();

	public ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading);

	public void onIOIOLooperSetup(IOIO ioio);
	
	public void onIOIOLooperDisconnected();

	
	
}
