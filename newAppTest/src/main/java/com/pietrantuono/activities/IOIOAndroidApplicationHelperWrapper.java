package com.pietrantuono.activities;

import android.app.Activity;
import android.content.ContextWrapper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;

class IOIOAndroidApplicationHelperWrapper {
	private boolean isStarted;	
	private IOIOAndroidApplicationHelper applicationHelper;

	@SuppressWarnings("ucd")
	public IOIOAndroidApplicationHelperWrapper(Activity activity) {
		applicationHelper=new IOIOAndroidApplicationHelper((ContextWrapper)activity, (IOIOLooperProvider)activity);
		
	}
	
	@SuppressWarnings("ucd")
	public void createAndStartHelperIfNotAlreadyStarted(){
		if(isStarted())return;
		isStarted = true;
		applicationHelper.create();
		applicationHelper.start();
		
	}
	
	@SuppressWarnings("ucd")
	public void stopAndDestroy(){
		try {
			applicationHelper.stop();
		} catch (Exception e) {
		}
		try {
			applicationHelper.destroy();
		} catch (Exception e) {
		}
		
	}
	
	private boolean isStarted() {
		return isStarted;
	}

	

	
	

}
