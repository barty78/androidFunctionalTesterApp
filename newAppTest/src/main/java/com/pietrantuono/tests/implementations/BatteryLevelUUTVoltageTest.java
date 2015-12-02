package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

public class BatteryLevelUUTVoltageTest extends Test {
    private static final int WAIT_TIME_IN_SECS = 10;
    private int voltage;
    private float limit;
    private float precision;
    private boolean resultreceived;
    private short batt = -1;

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest}
     * Do not execute this test before opening Bluetooth
     *
     * @param voltage
     * @param activity
     * @param description
     */
    public BatteryLevelUUTVoltageTest(Activity activity, float limit, float precision, String description, int voltage) {
        super(activity, null, description, false, false);
        this.voltage = voltage;
        this.limit = limit;
        this.precision = precision;
    }

    @Override
    public void execute() {
        if(isinterrupted)return;
        Log.d(TAG, "Test Starting: " + description);
        if(getListener().getBtutility()==null){report("BU utility is null");return;}
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        byte[] writebyte = new byte[] { 0x00, (byte) voltage }; // Value of 170 =
        // 3.5v
        byte[] readbyte = new byte[] {};
        if (IOIOUtils.getUtils().getMaster() != null)
            try {
                IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
                        readbyte, readbyte.length);
            } catch (Exception e1) {
                report(e1);
                getListener().addFailOrPass(true, false, "Fixture Fault");
                return;
            }
        if(isinterrupted)return;
        try {
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
            getListener().addFailOrPass(true, false, "ERROR", "App Fault");

        }

        if(isinterrupted)return;
        // Need to read battery level via BT from Pericoach PCB, not from
        // AnalogInput
        short battlevel = -1;
        try {
            battlevel = getListener().getBtutility().requestBatteryLevelAndWait();
        } catch (Exception e) {
            getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault");
            return;
        }
        setValue(battlevel);
        if (limit + (limit * precision) > battlevel
                && battlevel > limit - (limit * precision)) {
            setSuccess(true);
            getListener().addFailOrPass(true, true, df.format(battlevel) + "%",description);
        } else {
            setSuccess(false);
            getListener().addFailOrPass(true, false, df.format(battlevel) + "%",description);
        }
    }

    public interface Callback {
        public void onResultReceived(short battLevel);
    }
}
