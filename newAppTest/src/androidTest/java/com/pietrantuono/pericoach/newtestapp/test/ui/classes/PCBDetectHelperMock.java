package com.pietrantuono.pericoach.newtestapp.test.ui.classes;
import com.pietrantuono.activities.ActivtyWrapper;
import com.pietrantuono.ioioutils.PCBConnectedCallback;
import ioio.lib.api.DigitalInput;


public class PCBDetectHelperMock implements com.pietrantuono.ioioutils.PCBDetectHelper.PCBDetectHelperInterface {
	
	public  PCBDetectHelperMock() {}

	@Override
	public void startCheckingIfConnectionDrops(DigitalInput digitalInput) {
	}

	@Override
	public void setPCBDetectCallback(ActivtyWrapper callback) {
	}
	@Override
	public void stopCheckingIfConnectionDrops() {
		
	}

	

	@Override
	public  void waitForPCBDetect(PCBConnectedCallback callback,
			DigitalInput digitalInput) {
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		callback.onPCBConnectedStartNewSequence();

	}

	@Override
	public  void waitForPCBDisconneted(PCBConnectedCallback callback,
			DigitalInput digitalInput) {
		callback.onPCBDisconnected();

	}


}
