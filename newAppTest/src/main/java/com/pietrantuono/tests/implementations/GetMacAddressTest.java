package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.superclass.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ioio.lib.api.IOIO;
import server.service.ServiceDBHelper;

public class GetMacAddressTest extends Test {
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    private int retries = 0;
    private static final String key = "BT MAC ADDR: ";
    private static final int mac_length = 17;
    private AlertDialog alertDialog;
    private String mac = "";

    private String TAG = getClass().getSimpleName();

    public GetMacAddressTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Get BT Mac Address Test", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        if (IOIOUtils.getUtils().getUutMode(getActivity()) == IOIOUtils.Mode.bootloader) {
            IOIOUtils.getUtils().modeApplication((Activity) activityListener);
        }
        if (isinterrupted) return;
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


                Success();
                activityListener.addView("BT MAC ADDR: ", strFileContents, false);
//                activityListener.setSerial(strFileContents);
                activityListener.setMacAddress(strFileContents);
                activityListener.addFailOrPass(true, true, "");
                return;

            } else {
                activityListener.addFailOrPass(true, false, "");
                Log.d("MAC: ", "MAC INVALID.");
            }
            return;
        }
    }

    public String getBT_Addr() {
        return mac;
    }


    @Override
    public void interrupt() {
        super.interrupt();
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            executor.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
