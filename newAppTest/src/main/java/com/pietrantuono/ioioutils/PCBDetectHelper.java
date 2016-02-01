package com.pietrantuono.ioioutils;

import com.pietrantuono.activities.ActivtyWrapper;

import customclasses.PCBDetectHelperImpl;
import ioio.lib.api.DigitalInput;


public class PCBDetectHelper {
	public static PCBDetectHelperInterface getHelper(){
		return new PCBDetectHelperImpl();
	}
	
	

public interface PCBDetectHelperInterface {

	void startPCBSleepMonitor();

	void stopPCBSleepMonitor();

	void startCheckingIfConnectionDrops(DigitalInput digitalInput);

	void setPCBDetectCallback(ActivtyWrapper callback);

	void stopCheckingIfConnectionDrops();

	void stopWaitingForPCBDisconnected();

	void waitForPCBDetect(PCBConnectedCallback callback, DigitalInput digitalInput);

	void waitForPCBDisconneted(PCBConnectedCallback callback, DigitalInput digitalInput);

}
}