package com.pietrantuono.tests.implementations;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.FirmWareUploader;
import com.pietrantuono.uploadfirmware.FirmWareUploader.UploaderListener;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;
public class UploadFirmwareTest extends Test {
	private ProgressAndTextView pet;
	private InputStream RX;
	private OutputStream TX;
	private BufferedInputStream BRX;
	private FirmWareUploader firmWareUploader;
	private Boolean initialised = false;
	private Boolean looping = true;
	private Boolean resetted = true;
	private AlertDialog alertDialog;
	boolean fileComparisonPassed=false;
	boolean fileMD5Passed=false;
	private int retries;
	public UploadFirmwareTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Upload Firmware", false, true, 0, 0, 0);			// Blocking Test, if fails - STOP
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		String version = PeriCoachTestApplication.getGetFirmware().getVersion();
		pet = activityListener.createUploadProgress(false, true,description + " (Version: " + version + ")");
		((Activity)activityListener).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				pet.getTextView().setText("WAIT");
				Resources res = ((Activity)activityListener).getResources();
				Drawable orange = res.getDrawable(R.drawable.orangeprogress);
				pet.getProgress().setProgressDrawable(orange);
				pet.getProgress().setProgress(0);
			}
		});
		if (IOIOUtils.getUtils().getIOIOUart() != null) {
			RX = IOIOUtils.getUtils().getIOIOUart().getInputStream();// Pin 14
			BRX = new BufferedInputStream(RX);
			TX = IOIOUtils.getUtils().getIOIOUart().getOutputStream();// Pin 13
		}
		firmWareUploader = new FirmWareUploader(TX, RX, (Activity)activityListener,
				pet.getProgress(), pet.getTextView(), activityListener, ioio);
		

		Log.e(TAG, "Initialization loop");
		Thread t = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				while (!initialised && looping && retries<3) {
					if (resetted) {
						Log.d(TAG,""+retries);

						try {
							TX.flush();
						} catch (IOException e) {
							e.printStackTrace();
						}
						initialised = firmWareUploader.deviceInit();
						Log.d(TAG, "Initialised = " + initialised);
						if (!initialised) {							
							resetted = false;
							if(retries<=2)onInitialiseFailed();	//issueAlert();
						}
					}
				}
				if(retries>=3){
					setSuccess(false);
					((Activity)activityListener).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Resources res = ((Activity)activityListener).getResources();
							Drawable background = res
									.getDrawable(R.drawable.redprogress);
							pet.getProgress().setProgressDrawable(background);
							pet.getProgress().setProgress(100);
							pet.getTextView().setText("FAIL");
							activityListener.goAndExecuteNextTest();
						}
					});
					return;
				}
				firmWareUploader.getInfo();


				firmWareUploader.upload(new UploaderListener() {
					@Override
					public void onUploadCompleted(final boolean success) {
						setSuccess(success);
						Log.d(TAG, "FW Upload Result is " + success);
						try {
							RX.close();
						} catch (Exception e) {
							Crashlytics.logException(e);
						}
						try {
							TX.close();
						} catch (Exception e) {
							Crashlytics.logException(e);
						}
						if(!success){
							((Activity)activityListener).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Resources res = ((Activity)activityListener).getResources();
									Drawable background = res
											.getDrawable(R.drawable.redprogress);
									pet.getProgress().setProgressDrawable(background);
									pet.getProgress().setProgress(100);
									pet.getTextView().setText("FAIL");
								}
							});
							try {
								Thread.sleep(2 * 1000 + 500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						((Activity)activityListener).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								activityListener.setResult(success);
								activityListener.goAndExecuteNextTest();
							}
						});


					}
				});
			}
		});
		t.start();
	}
	private void onInitialiseFailed() {
		final Activity activity=(Activity)activityListener;
		if(activity==null || activity.isFinishing())return;
		IOIOUtils.getUtils().resetDevice((Activity)activityListener);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "INIT FAILED, " + String.valueOf(3 - retries) + " Attempts Remaining",
						Toast.LENGTH_SHORT).show();
				resetted = true;
				initialised = false;
				retries++;
			}
		});
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{firmWareUploader.stop();}catch(Exception e){};
		try{alertDialog.dismiss();;}catch(Exception e){};
	}
}
