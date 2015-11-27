package com.pietrantuono.activities;

import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

class MyLooper extends BaseIOIOLooper {	
	private final ActivtyWrapper callback;
	
	public MyLooper(ActivtyWrapper callback) {
		this.callback = callback;
	}

	@Override
	public void setup() throws ConnectionLostException {
		callback.onIOIOLooperSetup(ioio_);
	}
		

	@Override
	public void disconnected() {
	callback.onIOIOLooperDisconnected();
	}
}