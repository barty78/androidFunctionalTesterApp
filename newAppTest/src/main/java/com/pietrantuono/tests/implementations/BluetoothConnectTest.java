package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.os.AsyncTask;

import com.pietrantuono.activities.IOIOActivityListener;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;

public class BluetoothConnectTest extends Test {
    private BTUtility btUtility;
    private boolean connectUsingMac = false;

    public BluetoothConnectTest(Activity activity, boolean connectUsingMac) {
        super(activity, null, "Bluetooth Connect", false, false, 0, 0, 0);
        this.connectUsingMac = connectUsingMac;
    }

    @Override
    public void execute() {
        Executed();
        new BluetoothConnectTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            btUtility.abort();
        } catch (Exception e) {
        }
    }

    private class BluetoothConnectTestAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted) return null;
            btUtility = new BTUtility((Activity) activityListener, connectUsingMac,
                    activityListener.getSerial(), // TODO
                    // change
                    // serial
                    // goes activityListener.getSerial()
                    // here
                    // ""
                    // for
                    // testing
                    activityListener.getMac());
            activityListener.setBtutility(btUtility);
            if (!btUtility.connectProbeViaBT(BluetoothConnectTest.this)) {
                ((IOIOActivityListener) getActivity()).addFailOrPass(
                        false, false, "CONNECT FAILED", getDescription(), testToBeParsed);
            }
            return null;
        }
    }
}
