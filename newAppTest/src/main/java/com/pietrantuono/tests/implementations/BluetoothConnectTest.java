package com.pietrantuono.tests.implementations;
import android.app.Activity;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
public class BluetoothConnectTest extends Test {
	private BTUtility btUtility;
	public BluetoothConnectTest(Activity activity) {
		super(activity, null, "Bluetooth Connect", false, false, 0, 0, 0);
		setIdTest(11);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		btUtility = new BTUtility((Activity)activityListener, activityListener.getSerial(), // TODO
				// change
				// serial
				// goes activityListener.getSerial()
				// here
				// ""
				// for
				// testing
				activityListener);
		activityListener.setBtutility(btUtility);
		btUtility.connectProbeViaBT(BluetoothConnectTest.this);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{btUtility.abort();}
		catch (Exception e){}
	}
}
