package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.os.Handler;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

public class ReadFirmwareversionTest extends Test {
    private String firmwarever;
    private BTUtility btUtility;

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} Do
     * not execute this test before opening Bluetooth
     */
    public ReadFirmwareversionTest(Activity activity) {
        super(activity, null, "Firmware Version Check", false, false, 0, 0, 0);
    }

    @Override
    public void execute() {
        new ReadFirmwareversionTestAsyncTask().executeParallel();
    }

    public String getVersion() {
        return firmwarever;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    class ReadFirmwareversionTestAsyncTask extends SimpleAsyncTask {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted)
                return null;
            if (isinterrupted) {
                activityListener.addFailOrPass("", true, false,
                        description);
                return null;
            }
            btUtility = activityListener.getBtutility();
            if (btUtility == null) {
                report("BTUtility is null");
                activityListener.addFailOrPass("", true, false,
                        description);
                return null;
            }
            try {
                firmwarever = btUtility.getFirmWareVersion();
            } catch (Exception e) {
            }
            if (firmwarever == null) {
                activityListener.addFailOrPass(true, false, "NULL",
                        description);
            } else {
                if (firmwarever.equals(PeriCoachTestApplication.getGetFirmware().getVersion())) {
                    Success();
                    activityListener.setSequenceDevice(activityListener.getSequenceDevice().setFwver(firmwarever));
                    activityListener.addFailOrPass(true, true, firmwarever, description);
                } else {
                    activityListener.addFailOrPass(true, false, firmwarever, description);
                }
            }
            return null;
        }
    }
}