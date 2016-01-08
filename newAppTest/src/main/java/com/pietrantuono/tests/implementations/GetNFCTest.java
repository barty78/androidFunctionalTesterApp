package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Job;
import server.service.ServiceDBHelper;

public class GetNFCTest extends Test {

    private InputStream RX;
    private OutputStream TX;
    private static BufferedReader r;

    public int counter = 0;
    private int retries=0;
    private Boolean ready = false;
    private int timeout = 200;

    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    private AlertDialog alertDialog;
    private String tagid = "";

    private final static byte[] wake = new byte[]
            {0x55, 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xff, 0x03,
                    (byte) 0xfd, (byte) 0xd4, 0x14, 0x01, 0x17, 0x00};
    private final static byte[] wake_res = new byte[]
            {0x00, 0x00, (byte)0xFF, 0x00, (byte)0xFF, 0x00, 0x00, 0x00,
                    (byte)0xFF, 0x02, (byte)0xFE, (byte)0xD5, 0x15, 0x16, 0x00};

    private final static byte firmware[] = new byte[]
            {0x00, 0x00, (byte) 0xFF, 0x02, (byte) 0xFE,
                    (byte) 0xD4, 0x02, 0x2A, 0x00};//

    private final static byte tag[] = new byte[]
            {0x00, 0x00, (byte) 0xFF, 0x04, (byte) 0xFC, (byte) 0xD4,
                    0x4A, 0x01, 0x00, (byte) 0xE1, 0x00};//detecting tag command

    private final static byte std_ACK[] = new byte[]
            {0x00, 0x00, (byte) 0xFF, 0x00, (byte) 0xFF, 0x00, 0x00,
                    0x00, (byte) 0xFF, 0x0C, (byte) 0xF4, (byte) 0xD5,
                    0x4B, 0x01, 0x01, 0x00, 0x04, 0x08, 0x04, 0x00,
                    0x00, 0x00, 0x00, 0x4b, 0x00};

    /**
     * @param activity    - Activity Instance
     * @param ioio        - IOIO Instance
     * @param limitParam1 - Number of barcode read retries.
     */
    public GetNFCTest(Activity activity, IOIO ioio, float limitParam1) {
        super(activity, ioio, "Read NFC Tag", false, false, limitParam1, 0, 0);
        this.retries = (int) limitParam1;

    }

    @Override
    public void execute() {
        if (isinterrupted)
            return;
        if (IOIOUtils.getUtils().getIOIOUart() != null) {
            RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
            TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream(); // Pin 13
        }

        try {
            TX.write(wake, 0, wake.length);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (!Arrays.equals(ReadwithTimeout(wake_res.length), wake_res)) {
            activityListener.addFailOrPass("", true, false, description + " Read Failed");
            return;
        }

        try {
            TX.write(tag, 0, tag.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!Arrays.equals(ReadwithTimeout(std_ACK.length), std_ACK)) {
            activityListener.addFailOrPass("", true, false, description + " Read Failed");
            return;
        }

//        if (barcode != null && !barcode.isEmpty()) {
//            counter = 0;
//            activityListener.addView("Barcode", barcode, false);
//            if (!checkJob(barcode)) {
//                activityListener.addFailOrPass("", true, false, description + " - Invalid Barcode");
//                return;
//            } ;
//            if (!PeriCoachTestApplication.getIsRetestAllowed()) {
//                Log.d(TAG, "Retest is " + PeriCoachTestApplication.getIsRetestAllowed());
//                if (ServiceDBHelper.isBarcodeAlreadySeen(barcode)) {
//                    activityListener.addFailOrPass("", true, false, description + " - Barcode already tested");
//                    return;
//                } else {
//                    activityListener.setBarcode(barcode);
//                    ServiceDBHelper.saveBarcode(barcode);
//                    setSuccess(true);
//                    activityListener.addFailOrPass(true, true, barcode);
//                    return;
//
//                }
//            } else {
//                activityListener.setBarcode(barcode);
//                setSuccess(true);
//                ServiceDBHelper.saveBarcode(barcode);
//                activityListener.addFailOrPass(true, true, barcode);
//                return;
//            }
//        } else {
//            if (counter >= limitParam1) {
//                counter = 0;
//                activityListener.addFailOrPass("", true, false, description + " Read Failed");
//                return;
//            } else {
//                counter++;
//                execute();
//            }
//        }


    }

    public byte[] ReadwithTimeout(int len) {
        byte[] buffer = new byte[len];
        int count = 0;

        final Thread readThread = Thread.currentThread();
        Timer t = new Timer();
        final TimerTask readTask = new TimerTask() {
            @Override
            public void run() {
                readThread.interrupt();
                this.cancel();
            }
        };
        t.schedule(readTask, timeout);

        while (count < len) {
            try {
                count += RX.read(buffer, count, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        t.cancel();

        if (buffer != null) {
            for (int i = 0; i < buffer.length; i++) {
                Log.d(TAG + " - CALL", String.valueOf(buffer[i]));
            }

            return buffer;
        }

        return null;
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