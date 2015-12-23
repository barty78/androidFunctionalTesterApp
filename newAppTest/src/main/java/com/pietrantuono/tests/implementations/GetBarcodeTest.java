package com.pietrantuono.tests.implementations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Job;
import server.service.ServiceDBHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

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
            activityListener.addView("Barcode", barcode, false);
            if (!checkJob(barcode)) {
                try {
                    Toast.makeText((Activity) activityListener, "Invalid barcode! Aborting test", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                }
                activityListener.addFailOrPass(true, false, "Invalid barcode! Aborting test", description);
                return;
            } ;
            if (!PeriCoachTestApplication.getIsRetestAllowed()) {
                Log.d(TAG, "Retest is " + PeriCoachTestApplication.getIsRetestAllowed());
                if (ServiceDBHelper.isBarcodeAlreadySeen(barcode)) {
                    try {
                        Toast.makeText((Activity) activityListener, "Barcode already tested! Aborting test", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                    activityListener.onCurrentSequenceEnd();
                    return;
                } else {
                    activityListener.setBarcode(barcode);
                    ServiceDBHelper.saveBarcode(barcode);
                    setSuccess(true);
                    activityListener.addFailOrPass(true, true, barcode);

                    return;

                }
            } else {
                activityListener.setBarcode(barcode);
                setSuccess(true);
                ServiceDBHelper.saveBarcode(barcode);
                activityListener.addFailOrPass(true, true, barcode);
                return;
            }
        } else {
            if (counter >= limitParam1) {
                counter = 0;
                Toast.makeText((Activity) activityListener, "Unable to read barcode", Toast.LENGTH_LONG).show();
                activityListener.addFailOrPass(true, false, "Read Fail", description);
                return;
            } else {
                counter++;
                execute();
//				final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) activityListener);
//				builder.setTitle("Unable to read barcode");
//				builder.setMessage("Please check barcode reader and press OK to retry");
//				builder.setPositiveButton("OK", new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						execute();
//					}
//				});
//				builder.setNegativeButton("CLOSE TEST", new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						((Activity) activityListener).onBackPressed();
//					}
//				});
//				builder.setCancelable(true);
//				builder.setOnCancelListener(new MyOnCancelListener((Activity) activityListener));
//				alertDialog = builder.create();
//				((Activity) activityListener).runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						alertDialog.show();
//					}
//				});
            }
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
