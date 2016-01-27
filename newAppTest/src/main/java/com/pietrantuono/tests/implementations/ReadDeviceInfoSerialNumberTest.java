package com.pietrantuono.tests.implementations;

import server.service.ServiceDBHelper;

import android.app.Activity;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;

public class ReadDeviceInfoSerialNumberTest extends Test {
    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest}
     * Do not execute this test before opening Bluetooth
     */
    public ReadDeviceInfoSerialNumberTest(Activity activity) {
        super(activity, null, "Serial Number Check", false, false, 0, 0, 0);
    }

    @Override
    public void execute() {
        if (isinterrupted) return;
        BTUtility btUtility = activityListener.getBtutility();
        String serial = (btUtility == null) ? null : btUtility.getSerial();
        if (serial != null)
            activityListener.setSerialBT(serial, true);
        if (serial == null || activityListener.getSerial() == null
                || serial.length() != 24) {
            activityListener.addFailOrPass(true, false, serial, description);
        } else {
            if (serial.toLowerCase().equalsIgnoreCase(
                    activityListener.getSerial().toLowerCase())) {

                if (!PeriCoachTestApplication.getIsRetestAllowed() && ServiceDBHelper.isSerialAlreadySeen(activityListener.getBarcode(), serial)) {
                    try {
                        Toast.makeText((Activity) activityListener, "Barcode already tested! Aborting test", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                    activityListener
                            .addFailOrPass(true, false, serial, description);
                    return;
                }

                Success();
                ServiceDBHelper.saveSerial(activityListener.getBarcode(), serial);
                activityListener.addFailOrPass(true, true, serial, description);

            } else {
                activityListener
                        .addFailOrPass(true, false, serial, description);
            }
        }
    }
}
