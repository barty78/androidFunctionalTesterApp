package com.pietrantuono.tests.implementations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import ioio.lib.api.IOIO;
import server.service.ServiceDBHelper;

public class GetDeviceSerialTest extends Test {
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    public int retries = 0;
    private String serial = "";

    public GetDeviceSerialTest(Activity activity, IOIO ioio) {
        super(activity, ioio, "Read UUT Serial Number", false, true, 0, 0, 0);
    }

    @Override
    public void execute() {
        if (isinterrupted)
            return;
        Log.d(TAG, "Get Device Serial Test Starting");
        if (IOIOUtils.getUtils().getUutMode(getActivity()) == IOIOUtils.Mode.bootloader) {
            IOIOUtils.getUtils().modeApplication((Activity) activityListener);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        IOIOUtils.getUtils().clearUartLog();    // Clear the UART log buffer

        IOIOUtils.getUtils().resetDevice((Activity) activityListener);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((Activity) activityListener).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityListener.setSerial("");
                String strFileContents = "";
                if (IOIOUtils.getUtils().getUartLog().length() != 0) {        // Did we receive anything at all

                    if (IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") != -1) {
                        strFileContents = IOIOUtils.getUtils().getUartLog()
                                .substring(IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 8,
                                        IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 32)
                                .toString();
                        retries = 0;

                        Pattern pattern = Pattern.compile("^[\\p{Alnum}]+$");
                        Matcher matcher = pattern.matcher(strFileContents);
                        if (matcher.matches()) {
                            Log.d("SERIAL: ", "MATCH!.");
                            serial = strFileContents;

                            if (!PeriCoachTestApplication.getIsRetestAllowed()) {
                                Log.d(TAG, "Retest is " + PeriCoachTestApplication.getIsRetestAllowed());
                                if (!ServiceDBHelper.isSerialAlreadySeen(activityListener.getBarcode(), serial)) {
                                    Success();
//                                    activityListener.addView("Serial (HW reading):", strFileContents, false);
                                    activityListener.setSerial(strFileContents);
                                    activityListener.addFailOrPass(true, true, serial, description);
                                    ServiceDBHelper.saveSerial(activityListener.getBarcode(), serial);
                                    return;
                                } else {
                                    try {
                                        Toast.makeText((Activity) activityListener, "Serial number already tested! Aborting test", Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                    }
                                    activityListener.addFailOrPass(true, false, serial, description);

                                    activityListener.onCurrentSequenceEnd();
                                    return;
                                }
                            } else {
                                Success();
//                                activityListener.addView("Serial (HW reading):", strFileContents, false);
                                activityListener.setSerial(strFileContents);
                                activityListener.addFailOrPass(true, true, serial, description);
                                return;
                            }

                        }
                    } else {
                        if (retries > 2) {
                            setSuccess(false);
                            activityListener.addView("Serial (HW reading):", "ERROR", Color.RED, true);
                            activityListener.addFailOrPass(true, false, "Retries Exceeded");

                        } else {
                            retries++;
                            execute();
                        }
                    }
                } else {
                    if (retries > 2) {
                        setSuccess(false);
                        activityListener.addView("Serial (HW reading):", "ERROR", Color.RED, true);
                        activityListener.addFailOrPass(true, false, "No Comms");

                    } else {
                        retries++;
                        execute();
                    }
                }
                return;
            }
        });

    }

    public String getSerial() {
        return serial;
    }

    @Override
    public void interrupt() {

        try {
            executor.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
