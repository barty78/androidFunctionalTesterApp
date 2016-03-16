package com.pietrantuono.tests.implementations;

import analytica.pericoach.android.Contract;
import server.pojos.Device;

import android.app.Activity;
import android.database.Cursor;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
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
                if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 2) {
                    Cursor c = ((Activity) activityListener).getContentResolver().query(DevicesContentProvider.CONTENT_URI, null, Contract.DevicesColumns.DEVICES_SERIAL+" = ?", new String[]{serial}, null);
                    if (c.getCount() > 0) {
                        c.moveToFirst();
                        Device device = DevicesContentProvider.reconstructDevice(c);
                        if (device != null && ((device.getExec_Tests() & 1) == 1 && (device.getStatus() & 1) == 1)) {
                            Success();
                            activityListener.setSequenceDevice(device);
                            activityListener.addFailOrPass(true, true, serial, description);
                        } else {
                            activityListener
                                    .addFailOrPass(true, false, serial, description);
                        }
                    } else {
                        activityListener
                                .addFailOrPass(true, false, serial, description);
                    }
                } else {

                    if (serial.toLowerCase().equalsIgnoreCase(
                            activityListener.getSerial().toLowerCase())) {

                        if (!PeriCoachTestApplication.getIsRetestAllowed() && DevicesContentProvider.isDeviceAlreadySeen(activityListener.getBarcode(), serial,(Activity)activityListener)) {
                            try {
                                Toast.makeText((Activity) activityListener, "Barcode already tested! Aborting test", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                            }
                            activityListener
                                    .addFailOrPass(true, false, serial, description);
                            return null;
                        }

                        Success();
                        activityListener.addFailOrPass(true, true, serial, description);
                        activityListener.setSequenceDevice(activityListener.getSequenceDevice().setSerial(serial));

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
