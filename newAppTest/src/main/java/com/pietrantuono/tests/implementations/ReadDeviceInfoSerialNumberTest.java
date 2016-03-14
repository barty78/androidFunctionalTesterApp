package com.pietrantuono.tests.implementations;

import server.pojos.Device;
import server.service.ServiceDBHelper;

import android.app.Activity;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

public class ReadDeviceInfoSerialNumberTest extends Test {

    private String serial = "";
    private String barcode = "";


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
        new ReadDeviceInfoSerialNumberTestAsyncTask().executeParallel();
    }

    class ReadDeviceInfoSerialNumberTestAsyncTask extends SimpleAsyncTask {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted) return null;
            BTUtility btUtility = activityListener.getBtutility();
            serial = (btUtility == null) ? null : btUtility.getSerial();
//        if (serial != null)
//            activityListener.setSerialBT(serial, true);
            if (serial == null || (activityListener.getSerial() == null && PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1)
                    || serial.length() != 24) {
                activityListener.addFailOrPass(true, false, serial, description);
            } else {

                //TODO - If testtype is not first stage test, need to check if device is already seen.  Check if it passed first stage testing.
                if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 2) {
                    Device device = ServiceDBHelper.weHaveItAlready(false, serial);
                    if (device != null && (device.getExec_Tests() == 1 && (device.getStatus() == device.getExec_Tests()))) {
                        barcode = device.getBarcode();
//                        activityListener.setBarcode(device.getBarcode());
//                        activityListener.setSerial(serial);
                        Success();
                        activityListener.addFailOrPass(true, true, serial, description);
                    } else {
                        activityListener
                                .addFailOrPass(true, false, serial, description);
                    }
                } else {

                    if (serial.toLowerCase().equalsIgnoreCase(
                            activityListener.getSerial().toLowerCase())) {

                        if (!PeriCoachTestApplication.getIsRetestAllowed() && ServiceDBHelper.isDeviceAlreadySeen(activityListener.getBarcode(), serial)) {
                            try {
                                Toast.makeText((Activity) activityListener, "Barcode already tested! Aborting test", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                            }
                            activityListener
                                    .addFailOrPass(true, false, serial, description);
                            return null;
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
            return null;
        }
    }

    public String getSerial() {
        return serial;
    }

    public String getBarcode() {
        return barcode;
    }

}
