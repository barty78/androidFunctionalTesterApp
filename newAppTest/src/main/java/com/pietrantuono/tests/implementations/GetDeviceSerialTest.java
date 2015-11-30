package com.pietrantuono.tests.implementations;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import ioio.lib.api.IOIO;
import server.service.ServiceDBHelper;

public class GetDeviceSerialTest extends Test {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	public int counter2 = 0;
	private AlertDialog alertDialog;
	private String serial = "";

	public GetDeviceSerialTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Read UUT Serial Number", false, true);
	}

	@Override
	public void execute() {
		if (isinterrupted)
			return;
		Log.d(TAG, "Get Device Serial Test Starting");
		IOIOUtils.getUtils().modeApplication((Activity) activityListener);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		IOIOUtils.getUtils().clearUartLog();	// Clear the UART log buffer

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

				
				if (IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") != -1) {
					strFileContents = IOIOUtils.getUtils().getUartLog()
							.substring(IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 8,
									IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 32)
							.toString();
					counter2 = 0;

					Pattern pattern = Pattern.compile("^[\\p{Alnum}]+$");
					Matcher matcher = pattern.matcher(strFileContents);
					if (matcher.matches()) {
						Log.d("SERIAL: ", "MATCH!.");
						serial = strFileContents;
						if(!ServiceDBHelper.isSerialAlreadySeen(serial)){
						Success();
						activityListener.addView("Serial (HW reading):", strFileContents, false);
						activityListener.setSerial(strFileContents);
						activityListener.addFailOrPass(true, true, "");
						return;
						}
						else {
							try {
								Toast.makeText((Activity) activityListener, "Barcode already tested! Aborting test", Toast.LENGTH_LONG).show();
								} catch (Exception e){}
								activityListener.onCurrentSequenceEnd();
								return;
						}
					}
				}

				if (counter2 > 2) {
					
					activityListener.addView("Serial (HW reading):", "ERROR", Color.RED, true);
					Toast.makeText((Activity) activityListener, "Unable to read serial number", Toast.LENGTH_LONG)
					.show();
				} else {
					counter2++;
					final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) activityListener);
					builder.setTitle("Unable to read serial number");
					builder.setMessage("Click OK to retry");
					builder.setCancelable(true);
					builder.setOnCancelListener(new MyOnCancelListener((Activity) activityListener));
					builder.setPositiveButton("OK", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// uart1.close();
							execute();
						}
					});
					builder.setNegativeButton("CLOSE TEST", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							((Activity) activityListener).onBackPressed();
						}
					});

					alertDialog = builder.create();
					alertDialog.show();

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
		super.interrupt();
		try {
			alertDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// try{RD1.close();}catch (Exception e){e.printStackTrace();}
		// try{RX1.close();}catch (Exception e){e.printStackTrace();}
		// try{uart1.close();}catch (Exception e){e.printStackTrace();}
		try {
			executor.shutdownNow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
