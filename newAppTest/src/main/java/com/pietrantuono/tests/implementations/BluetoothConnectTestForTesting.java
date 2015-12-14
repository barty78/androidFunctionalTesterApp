package com.pietrantuono.tests.implementations;
import android.app.Activity;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
public class BluetoothConnectTestForTesting extends Test {
	private BTUtility btUtility;
	private Boolean testing = false;
	public BluetoothConnectTestForTesting(Activity activity) {
		super(activity, null, "Bluetooth Connect", false, true, 0, 0, 0);
		setIdTest(11);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		if (testing)
			btUtility = new BTUtility((Activity)activityListener, "6707433948538265066CFF49", // TODO
					// TODO
					// change
					// serial
					// goes activityListener.getSerial()
					// here
					// ""
					// for
					// testing
					activityListener);
		else
			btUtility = new BTUtility((Activity)activityListener, activityListener.getSerial(), activityListener);
		activityListener.setBtutility(btUtility);
		btUtility.connectProbeViaBT(BluetoothConnectTestForTesting.this);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{btUtility.abort();}
		catch (Exception e){}
	}
}
