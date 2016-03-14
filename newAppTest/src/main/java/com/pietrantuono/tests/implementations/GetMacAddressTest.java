package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
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

public class GetMacAddressTest extends Test {
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    private static final String key = "BT MAC ADDR: ";
    private static final int mac_length = 17;
    private String mac = "";

    private String TAG = getClass().getSimpleName();

    public GetMacAddressTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Get BT Mac Address Test", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        new GetMacAddressTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    public String getBT_Addr() {
        return mac;
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

    class GetMacAddressTestAsyncTask extends AsyncTask<Void,Void,Void>{
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
                                    IOIOUtils.getUtils().getUartLog().indexOf(key) + (key.length() + mac_length))
                            .toString();
                }

                Log.d(TAG, "BT ADDR " + strFileContents);

                Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
                Matcher matcher = pattern.matcher(strFileContents);
                if (matcher.matches()) {
                    Log.d("MAC: ", "MAC VALID.");
                    mac = strFileContents;

                    if(DevicesContentProvider.isMacAlreadySeen(activityListener.getBarcode(), mac,(Activity)activityListener) && !PeriCoachTestApplication.getIsRetestAllowed()){
                        Toast.makeText((Activity)activityListener,"DEVICE ALRREDY TESTED, ABORTING !",Toast.LENGTH_LONG).show();
                        setSuccess(false);
                        activityListener.addFailOrPass(true, false, mac, description);
                        return null;
                    }

                    Success();
//                activityListener.addView("BT ADDR: ", strFileContents, false);
//                activityListener.setSerial(strFileContents);
                    activityListener.setMacAddress(strFileContents);
                    activityListener.addFailOrPass(true, true, mac, description);
                    return null;

                } else {
                    activityListener.addFailOrPass(true, false, "MAC INVALID", description);
                    Log.d("MAC: ", "MAC INVALID.");
                }
                return null;
            }
            return null;
        }
    }
}
