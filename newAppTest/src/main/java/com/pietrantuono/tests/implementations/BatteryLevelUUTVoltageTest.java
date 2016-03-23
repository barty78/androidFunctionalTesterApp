package com.pietrantuono.tests.implementations;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.IOIO;

public class BatteryLevelUUTVoltageTest extends Test {
    private final float voltage;
    private final float limit;
    private final IOIO ioio;
    private final float precision;

    /**
     * IMPORTANT: Bluetooth must be open using
     * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest}
     * Do not execute this test before opening Bluetooth
     *
     * @param voltage
     * @param activity
     * @param description
     */
    public BatteryLevelUUTVoltageTest(Activity activity, IOIO ioio, float limit, float precision, String description, float voltage) {
        super(activity, null, description, false, false, 0, 0, 0);
        this.ioio = ioio;
        this.voltage = voltage;
        this.limit = limit;
        this.precision = precision;
    }

    @Override
    public void execute() {
        new BatteryLevelUUTVoltageTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class BatteryLevelUUTVoltageTestAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted) return null;
            Log.d(TAG, "TEST Starting: " + description);
            if (getListener().getBtutility() == null) {
                report("BU utility is null");
                return null;
            }
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            // Set battery voltage based on parameter
            if (!IOIOUtils.getUtils().setBattVoltage(ioio, false, 37, 3f, voltage)) {
                getListener().addFailOrPass(true, false, "Fixture Fault - Battery Voltage Setpoint not reached", testToBeParsed);
                return null;
            }
            if (isinterrupted) return null;
            try {
                Thread.sleep(2 * 1000);
            } catch (Exception e) {
                getListener().addFailOrPass(true, false, "ERROR", "App Fault", testToBeParsed);

            }

            if (isinterrupted) return null;
            // Need to read battery level via BT from Pericoach PCB, not from
            // AnalogInput
            short battlevel = -1;
            try {
                battlevel = getListener().getBtutility().requestBatteryLevelAndWait();
            } catch (Exception e) {
                getListener().addFailOrPass(true, false, "ERROR", "UUT Comms Fault", testToBeParsed);
                return null;
            }
            setValue(battlevel);
            if (limit + (limit * precision) > battlevel
                    && battlevel > limit - (limit * precision)) {
                setSuccess(true);
                getListener().addFailOrPass(true, true, df.format(battlevel) + "%", description, testToBeParsed);
            } else {
                setSuccess(false);
                getListener().addFailOrPass(true, false, df.format(battlevel) + "%", description, testToBeParsed);
            }

            return null;
        }
    }


    @SuppressWarnings("unused")
    public interface Callback {
        public void onResultReceived(short battLevel);
    }
}
