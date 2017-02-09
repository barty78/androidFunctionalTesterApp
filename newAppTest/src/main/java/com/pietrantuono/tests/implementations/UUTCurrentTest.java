package com.pietrantuono.tests.implementations;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;

import java.text.DecimalFormat;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.SimpleAsyncTask;
import com.pietrantuono.tests.superclass.Test;

import android.app.Activity;
import android.util.Log;

public class UUTCurrentTest extends Test {
    private AnalogInput I_UUT = null;
    private final AnalogInput V_TRGT = null;

    public UUTCurrentTest(Activity activity, IOIO ioio, String description) {
        super(activity, ioio, description, false, false, 0, 0, 0);
    }

    private final String TAG = getClass().getSimpleName();

    @Override
    public void execute() {
        Executed();
        new UUTCurrentTestAsyncTask().executeParallel();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            I_UUT.close();
        } catch (Exception e) {
        }
        try {
            V_TRGT.close();
        } catch (Exception e) {
        }
    }

    private class UUTCurrentTestAsyncTask extends SimpleAsyncTask {
        @Override
        protected Void doInBackground(Void... params) {
            if (isInterupted()) return null;
            DecimalFormat df = new DecimalFormat("##.##");
            Log.e(TAG, "UUTCurrent");
            Log.e(TAG, "UUTCurrent setStatusMSG passed");
            byte[] writebyte = new byte[]{0x00, (byte) 100};
            byte[] readbyte = new byte[]{};
            if (IOIOUtils.getUtils().getMaster() != null)
                try {
                    IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
                            readbyte, readbyte.length);
                } catch (Exception e1) {
                    report(e1);
                    Log.e("UUTCurrent", e1.toString());
                    activityListener.addFailOrPass(true, false, description);
                    return null;
                }
            try {
                IOIOUtils.getUtils().getPOWER().write(false);
            } catch (Exception e) {
                Log.e("UUTCurrent", e.toString());
                report(e);
                activityListener.addFailOrPass(true, false, description);
                return null;
            }
            try {
                I_UUT = ioio.openAnalogInput(42);
            } catch (Exception e) {
                Log.e("UUTCurrent", e.toString());
                report(e);
                activityListener.addFailOrPass(true, false, description);
                return null;
            }
            if (I_UUT == null) {
                activityListener.addFailOrPass(true, false, description);
                return null;
            }

            float total = 0;
            int numsamples = 30;
            for (int i = 0; i < numsamples; i++) {
                if (isinterrupted) return null;
                try {
                    total = total
                            + ((I_UUT.getVoltage() / (50 * 1002)) * (float) 1e6); // Conversion
                    // for
                    // uA
                    // range
                    // (Gain
                    // =
                    // 50,
                    // Rsense
                    // =
                    // 1.02k)
                } catch (Exception e) {
                    Log.e("UUTCurrent", e.toString());
                    report(e);
                    activityListener.addFailOrPass(true, false, description);
                    return null;
                }
            }
            float average = total / numsamples;
            I_UUT.close();
            setValue(average);
            String reading = "";
            if (average != 0)
                reading = df.format(average) + "uA";
            if (average == 0)
                reading = "0uA";
            if (average >= 0 && average < 100) {
                Log.e("UUTCurrent", "average > 0 && average < 10)");
                activityListener.addFailOrPass(true, true, reading, description);
                Success();
                //activityListener.goAndExecuteNextTest();
            } else {
                Log.e("UUTCurrent", "else");
                try {
                    IOIOUtils.getUtils().getPOWER().write(true); // Too much or no current draw, turn power
                    // off - stop test sequence
                } catch (Exception e) {
                    report(e);
                }
                activityListener.addFailOrPass(true, false, reading, description);
                activityListener.setStatusMSG("OVERCURRENT \nTEST STOPPED", false);

            }
            return null;
        }
    }
}
