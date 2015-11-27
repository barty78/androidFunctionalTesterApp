package com.pietrantuono.ioioutils;

import com.pietrantuono.activities.ActivtyWrapper;

import ioio.lib.api.DigitalInput;


public class PCBDetectHelper {
	public static PCBDetectHelperInterface getHelper(){
		return new PCBDetectHelperImpl();
	}
	
	

public interface PCBDetectHelperInterface {

	void startCheckingIfConnectionDrops(DigitalInput digitalInput);

	void setPCBDetectCallback(ActivtyWrapper callback);

	void stopCheckingIfConnectionDrops();

	void waitForPCBDetect(PCBConnectedCallback callback, DigitalInput digitalInput);

	void waitForPCBDisconneted(PCBConnectedCallback callback, DigitalInput digitalInput);

}
}