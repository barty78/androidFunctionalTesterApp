package com.pietrantuono.ioioutils;

public interface PCBConnectedCallback {
	
	void onPCBConnectedStartNewSequence();
	void onPCBDisconnected();
	void onPCBSleep();

}
