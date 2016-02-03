package com.pietrantuono.tests.implementations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Device;
import server.pojos.Job;
import server.service.ServiceDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

@SuppressWarnings("unused")
public class GetBarcodeTest extends Test {
    private Uart uart1;
    private InputStream RX1;
    private BufferedReader RD1;
    private DigitalInput barcodeOK;
    private DigitalOutput BarcodeTrgr;
    private DigitalOutput barcodeTRGR;
    public int counter = 0;
    private int retries;
    private static ExecutorService executor = Executors.newFixedThreadPool(1);
    private AlertDialog alertDialog;
    private String barcode = "";
    private Job job;


    /**
     * @param activity    - Activity Instance
     * @param ioio        - IOIO Instance
     * @param limitParam1 - Number of barcode read retries.
     */
    public GetBarcodeTest(Activity activity, IOIO ioio, Job job, float limitParam1) {
        super(activity, ioio, "Read PCB Barcode Label", false, true, limitParam1, 0, 0);
        this.retries = (int) limitParam1;
        this.job = job;

    }

    @Override
    public void execute() {
        if (isinterrupted)
            return;

        barcode = IOIOUtils.getUtils().readBarcode((Activity) activityListener);

        if (barcode != null && !barcode.isEmpty()) {
            counter = 0;
//            activityListener.addView("Barcode", barcode, false);
            if (!checkJob(barcode)) {
                activityListener.addFailOrPass("", true, false, description + " - Invalid Barcode");
                return;
            }
            ;
            if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 2) {//OPEN TEST
                // For open test it doesn't really need anything, but maybe for completeness we should use stage_dep and make sure status is 0
                // so status == stage_dep
                if (!checkDeviceStatus(barcode)) {
                    activityListener.addFailOrPass("", true, false, description + " - Device must pass OPEN TEST, current test is CLOSED TEST");
                    return;
                } else {/*IS CLOSED TEST, perform different check*/ }
            }
            if (!PeriCoachTestApplication.getIsRetestAllowed()) {
                Log.d(TAG, "Retest is " + PeriCoachTestApplication.getIsRetestAllowed());
                if (ServiceDBHelper.isBarcodeAlreadySeen(barcode)) {
                    activityListener.addFailOrPass("", true, false, description + " - Barcode already tested");
                    return;
                } else {
                    activityListener.setBarcode(barcode);
                    ServiceDBHelper.saveBarcode(barcode);
                    setSuccess(true);
                    activityListener.addFailOrPass(true, true, barcode, description, testToBeParsed);
                    return;

                }
            } else {
                activityListener.setBarcode(barcode);
                setSuccess(true);
                ServiceDBHelper.saveBarcode(barcode);
                activityListener.addFailOrPass(true, true, barcode, description, testToBeParsed);
                return;
            }
        } else {
            if (counter >= limitParam1) {
                counter = 0;
                activityListener.addFailOrPass("", true, false, description + " Read Failed");
                return;
            } else {
                counter++;
                execute();
            }
        }
    }

    private boolean checkDeviceStatus(String barcode) {
        Device device=new Select().from(Device.class).where("Barcode = ?",barcode).executeSingle();
        if(device==null)return false;
        return device.getStatus()==PeriCoachTestApplication.getCurrentJob().getStage_dep();
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

    public String getBarcode() {
        return barcode;
    }

    public boolean checkJob(String code) {

        if (job == null
                || job.getBarcodeprefix() == null
                || job.getBarcodeprefix().length() <= 0
                || job.getQuantity() <= 0)
//				|| job.getTestId() == 999)				// Special test sequence, ignore barcode contents
        {
            return true;
        }
        String barcodeprefix = null;
        try {
            barcodeprefix = code.substring(0, job.getBarcodeprefix().length());

            Log.d(TAG, "Expected Prefix is " + job.getBarcodeprefix() + " | " +
                    "Actual Prefix is " + code.substring(0, job.getBarcodeprefix().length()));
        } catch (Exception e) {
            return false;
        }
        if (barcodeprefix == null) {
            return false;
        }
        if (!barcodeprefix.equalsIgnoreCase(job.getBarcodeprefix())) {
            return false;
        }
        String quantityString = null;
        try {
            quantityString = code.substring(job.getBarcodeprefix().length(), code.length());
            Log.d(TAG, "Expected Number is " + job.getQuantity() + " | " +
                    "Actual Number is " + code.substring(job.getBarcodeprefix().length(), code.length()));
        } catch (Exception e) {
            return false;
        }
        if (quantityString == null) {
            return false;
        }
        int quantity = -1;
        try {
            quantity = Integer.parseInt(quantityString);
        } catch (Exception e) {

            return false;
        }
        if (quantity <= 0) {
            return false;
        }
        if (quantity > job.getQuantity()) {
            return false;
        }
        return true;
    }

}