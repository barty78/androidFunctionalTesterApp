package com.pietrantuono.pericoach.newtestapp.test.ui.classes;

import com.pietrantuono.activities.ActivtyWrapper;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class MyLooperForTest extends BaseIOIOLooper {	
	private ActivtyWrapper callback;
	private IOIO ioio=null;
	
	public MyLooperForTest(ActivtyWrapper callback, IOIO ioio) {
		this.callback = callback;
		this.ioio=ioio;
	}

	@Override
	public void setup() throws ConnectionLostException {
		callback.onIOIOLooperSetup(ioio);
	}
		

	@Override
	public void disconnected() {
	callback.onIOIOLooperDisconnected();
	}
}