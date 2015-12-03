package com.pietrantuono.tests.implementations;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
public class Charge_termination_test extends Test {
	private AlertDialog alertDialog;
	private Boolean isCharging;
	private Boolean CHGPin;
	private Boolean success = false;

	public Charge_termination_test(Activity activity, IOIO ioio,
			String description) {
		super(activity, ioio, description, false, false);
		this.isCharging = isCharging;
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		byte[] writebyte;
		byte[] readbyte;
		Activity ac= (Activity)activityListener;
		if (!isCharging) {
			writebyte = new byte[]{0x00, (byte) 0};		// Set voltage to high (~4.1v)
			readbyte = new byte[]{};
		} else {
			writebyte = new byte[]{0x00, (byte) 100};		// Set voltage to normal (~3.5v)
			readbyte = new byte[]{};

		}
		if (IOIOUtils.getUtils().getMaster() != null)
			try {
				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
						readbyte, readbyte.length);
			} catch (Exception e1) {
				report(e1);
				activityListener.addFailOrPass(true, false, "IOIO Fault");
				return;
			}

		try {
			CHGPin = IOIOUtils.getUtils().getCHGPin().read();
		} catch (Exception e2) {
			report(e2);
			activityListener.addFailOrPass(true, false, "IOIO Read Fault");
		}

		if (isCharging) {
			if (!CHGPin) {
				showAlert(ac, true);
			}
		} else {
			if (CHGPin) {
				showAlert(ac, true);
			}
		}
		setSuccess(true);
		activityListener.addFailOrPass(false, false, description);

	}

	private void showAlert(Activity ac, final Boolean value) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(ac);
		builder.setTitle("Pink LED check");
		builder.setMessage("Please check pink LED it's OFF");
		builder.setPositiveButton("Yes, it's OFF", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (value) {
					setSuccess(true);
					activityListener.addFailOrPass(false, true, description);
				} else {
					setSuccess(false);
					activityListener.addFailOrPass(false, false, description);
				}
			}
		});
		builder.setNegativeButton("No, it's ON", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (value) {
					setSuccess(false);
					activityListener.addFailOrPass(false, false, description);
				} else {
					setSuccess(false);
					activityListener.addFailOrPass(false, true, description);
				}
			}
		});
		builder.setCancelable(true);
		builder.setOnCancelListener(new MyOnCancelListener(ac));
		alertDialog=builder.create();
		ac.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				alertDialog.show();
			}
		});
	}

	@Override
	public void interrupt() {
		super.interrupt();
		try{alertDialog.dismiss();}catch(Exception e){}
	}
}
