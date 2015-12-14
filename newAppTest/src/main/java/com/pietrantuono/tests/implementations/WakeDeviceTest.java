package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
public class WakeDeviceTest extends Test{
	private AnalogInput V_3V0_SW;
	private AlertDialog alertDialog;
	private Thread t; 
	public WakeDeviceTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Wake Device", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		try {
			IOIOUtils.getUtils().getSensor_Low().write(false);
		} catch (Exception e1) {
			report(e1);
			activityListener.addFailOrPass(true,false, e1.toString());
			return;
		}
		IOIOUtils.getUtils().resetDevice((Activity)activityListener);
		try {
			V_3V0_SW = ioio.openAnalogInput(39);
		} catch (Exception e2) {
			report(e2);
			activityListener.addFailOrPass(true,false, e2.toString());
			return;
		}
		if (V_3V0_SW == null)
			return;
		final int numsamples = 10;
		final int duration = 1 * 1000; // Duration of wake test
		final int waketime = 2 * 1000; // Hold time to wake device
		t= new Thread(new Runnable() {
			@Override
			public void run() {
				boolean awake = true;
				try {
					Thread.sleep(waketime);
				} catch (Exception e1) {
					report(e1);
					activityListener.addFailOrPass(true,false, e1.toString());
					return;
				}
				for (int i = 0; i < numsamples; i++) {
					try {
						if (V_3V0_SW.getVoltage() < 2.7) // Every sample must be
															// < 2.7v.
							awake = false;
					} catch (Exception e) {
						report(e);
						activityListener.addFailOrPass(true,false, e.toString());
						return;
					}
					try {
						Thread.sleep(duration / numsamples);
					} catch (Exception e1) {
						report(e1);
						activityListener.addFailOrPass(true,false, e1.toString());
						return;
					}
				}
				if (!awake) {
					final AlertDialog.Builder builder = new AlertDialog.Builder((Activity)activityListener);
					builder.setTitle("Waking device");
					builder.setMessage("Wake device failed");
					builder.setCancelable(true);
					builder.setOnCancelListener(new MyOnCancelListener((Activity)activityListener));
					builder.setPositiveButton("Retry", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							V_3V0_SW.close();
							execute();
						}
					});
					((Activity)activityListener).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							alertDialog=builder.create();
							alertDialog.show();
						}
					});
				} else {
					V_3V0_SW.close();
					// Device is awake, we can release sensor_low
					try {
						IOIOUtils.getUtils().getSensor_Low().write(true);
					} catch (Exception e) {
						report(e);
					}
					Success();
					activityListener.addFailOrPass(false, true, description);
				}
			}
		});
		t.start();
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {alertDialog.dismiss();}catch(Exception e){};
		try {V_3V0_SW.close();}catch(Exception e){};
		try {t.stop();}catch(Exception e){};
	}
}
