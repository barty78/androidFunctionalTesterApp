package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public class MagnetWakeDeviceTest extends Test{
	private AnalogInput V_3V0_SW;
	private AlertDialog alertDialog;
	private Thread t; 
	public MagnetWakeDeviceTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Wake Device", false, true);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		if(IOIOUtils.getUtils().getEmag() != null) {
			IOIOUtils.getUtils().toggleEMag((Activity)activityListener);
		}
		if(isinterrupted)return;

		try {
			Voltage.Result result = Voltage.checkVoltage(ioio, 38, 1f, true, 1.5f, 0.1f );
            if (result.isSuccess()){
				Success();
				activityListener.addFailOrPass(true, true, "",  description);
			} else {
				activityListener.addFailOrPass(true, false, "",  description);
			}
//            try {
//                if (V_3V0_SW != null) {
//                    V_3V0_SW = ioio.openAnalogInput(39);
//                }
//            } catch (Exception e2) {
//                report(e2);
//                activityListener.addFailOrPass(true, false, "Fixture Fault");
//                return;
//            }
		} catch (Exception e) {
			e.printStackTrace();
		}

//		if (V_3V0_SW == null){
//			activityListener.addFailOrPass(true, false, "Fixture Fault");
//			return;}
//		final int numsamples = 10;
//		final int duration = 1 * 1000; // Duration of wake test
//		final int waketime = 2 * 1000; // Hold time to wake device
//		t= new Thread(new Runnable() {
//			@Override
//			public void run() {
//				boolean awake = true;
//				try {
//					Thread.sleep(waketime);
//				} catch (Exception e1) {
//					report(e1);
//					activityListener.addFailOrPass(true, false, "App Fault");
//					return;
//				}
//				for (int i = 0; i < numsamples; i++) {
//					try {
//						Float tmp = V_3V0_SW.getVoltage();
//						if (tmp < 1.5) {
////						if (V_3V0_SW.getVoltage() < 1.5) // Every sample must be
//							// < 2.7v.
//							awake = false;
//							Log.d(TAG, "Voltage " + String.valueOf(tmp));
//						}
//					} catch (Exception e) {
//						report(e);
//						activityListener.addFailOrPass(true, false, "Reading Fault");
//						return;
//					}
//					try {
//						Thread.sleep(duration / numsamples);
//					} catch (Exception e1) {
//						report(e1);
//						activityListener.addFailOrPass(true, false, "App Fault");
//						return;
//					}
//				}
//				if (!awake) {
//					final AlertDialog.Builder builder = new AlertDialog.Builder((Activity)activityListener);
//					builder.setTitle("Waking device");
//					builder.setMessage("Wake device failed");
//					builder.setCancelable(true);
//					builder.setOnCancelListener(new MyOnCancelListener((Activity)activityListener));
//					builder.setPositiveButton("Retry", new OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.dismiss();
//							try {
//								//IOIOUtils.getUtils().getHallInt().
//							} catch (Exception e1) {
//								report(e1);
//								activityListener.addFailOrPass(false, false, description);
//								return;
//							}
//							try {
//								Thread.sleep(1000);
//							} catch (Exception e1) {
//								report(e1);
//								activityListener.addFailOrPass(false, false, description);
//								return;
//							}
//							V_3V0_SW.close();
//							execute();
//						}
//					});
//					builder.setNegativeButton("Close test", new OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							activityListener.addFailOrPass(false, false, description);
//						}
//					});
//					((Activity)activityListener).runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							alertDialog=builder.create();
//							alertDialog.show();
//						}
//					});
//				} else {
//					V_3V0_SW.close();
//					Success();
//					activityListener.addFailOrPass(true, true, "",  description);
//				}
//			}
//		});
//		t.start();
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {alertDialog.dismiss();}catch(Exception e){};
		try {V_3V0_SW.close();}catch(Exception e){};
		try {t.stop();}catch(Exception e){};
	}
}
