package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ioio.lib.api.IOIO;

public class GetFirmwareVersionUARTTest extends Test {
    private static final ExecutorService executor = Executors.newFixedThreadPool(1);
    private static final String key = "Firmware Version: ";
    private String firmwarever;
    private static final int version_length = 7;
    private static final String VERSION_PATTERN =
            "^\\d+(\\.\\d+){3}$";


    private final String TAG = getClass().getSimpleName();

    public GetFirmwareVersionUARTTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Firmware Version Check TEST", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        Executed();
        new GetFirmwareVersionUARTTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public String getVersion() {
        return firmwarever;
    }


    @Override
    public void interrupt() {
        super.interrupt();

        try {
            executor.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetFirmwareVersionUARTTestAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if (IOIOUtils.getUtils().getUutMode(getActivity()) == IOIOUtils.Mode.bootloader) {
                IOIOUtils.getUtils().modeApplication((Activity) activityListener);
            }
            if (isinterrupted) return null;
            String strFileContents = "";

            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (IOIOUtils.getUtils().getUartLog().length() != 0) {
                if (IOIOUtils.getUtils().getUartLog().indexOf(key) != -1) {
                    strFileContents = IOIOUtils.getUtils().getUartLog()
                            .substring(IOIOUtils.getUtils().getUartLog().indexOf(key) + key.length(),
                                    IOIOUtils.getUtils().getUartLog().indexOf(key) + (key.length() + version_length))
                            .toString();
                }

                Log.d(TAG, "FW_VER " + strFileContents);

                Pattern pattern = Pattern.compile(VERSION_PATTERN);
                Matcher matcher = pattern.matcher(strFileContents);
                if (matcher.matches()) {
                    Log.d("VER: ", "Version VALID.");
                    firmwarever = strFileContents;
                    if (firmwarever.equals(PeriCoachTestApplication.getGetFirmware().getVersion())) {
                        Success();
                        activityListener.setSequenceDevice(activityListener.getSequenceDevice().setFwver(firmwarever));
                        activityListener.addFailOrPass(true, true, firmwarever, description);
                    } else {
                        activityListener.addFailOrPass(true, false, firmwarever, description);
                    }
                    return null;

                } else {
                    activityListener.addFailOrPass(true, false, "VERSION INVALID", description);
                    Log.d("VER: ", "VERSION INVALID.");
                }
                return null;
            }
            return null;
        }
    }
}
