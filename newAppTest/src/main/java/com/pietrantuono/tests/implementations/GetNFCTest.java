package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
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

@SuppressWarnings("unused")
public class GetNFCTest extends Test {

    private InputStream RX;
    private OutputStream TX;
    private static BufferedReader r;

    public int counter = 0;
    private int retries=0;
    private Boolean ready = false;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

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
                    0x00, (byte) 0xFF, 0x0F, (byte) 0xF1, (byte) 0xD5,
                    0x4B, 0x01, 0x01, 0x00, 0x44, 0x00, 0x04, 0x00,
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
        new GetNFCTestAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] ReadwithTimeout(int timeout, int len) {
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
                count += RX.read(buffer, count, len-count);
                Log.d(TAG, String.valueOf(count) + "|" + String.valueOf(len));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        t.cancel();

        if (buffer != null) return buffer;

        return null;
    }

    private void printBuffer(byte[] buffer){
        for (int i = 0; i < buffer.length; i++) {
            Log.d(TAG + " - CALL", String.valueOf(buffer[i]));
        }
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

    class GetNFCTestAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if (isinterrupted)
                return null;
            if (IOIOUtils.getUtils().getIOIOUart() != null) {
                RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
                TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream(); // Pin 13
            }

            try {
                TX.write(wake, 0, wake.length);
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (!Arrays.equals(ReadwithTimeout(200, wake_res.length), wake_res)) {
                activityListener.addFailOrPass("", true, false, description + " Read Failed");
                return null;
            }

            try {
                TX.write(tag, 0, tag.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] result = ReadwithTimeout(5000, std_ACK.length);
            byte[] tmp1 = Arrays.copyOfRange(std_ACK, 0, 18);
            byte[] tmp2 = Arrays.copyOfRange(result, 0, 18);

            printBuffer(tmp1);
            printBuffer(tmp2);
            if (!Arrays.equals(tmp1, tmp2)) {
                activityListener.addFailOrPass("", true, false, description + " Read Failed");
                return null;
            }

            activityListener.addFailOrPass(true, true, bytesToHex(Arrays.copyOfRange(result,19,result.length)), "");

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

            return null;
        }
    }

}