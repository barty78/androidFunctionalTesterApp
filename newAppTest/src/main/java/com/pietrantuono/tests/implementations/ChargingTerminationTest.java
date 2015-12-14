package com.pietrantuono.tests.implementations;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
public class ChargingTerminationTest extends Test {
	private AlertDialog alertDialog;
	private Boolean CHGPin;
	private Boolean success = false;

	public ChargingTerminationTest(Activity activity, IOIO ioio,
								   String description) {
		super(activity, ioio, description, false, false, 0, 0, 0);
		setIdTest(5);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		byte[] writebyte;
		byte[] readbyte;
		Activity ac= (Activity)activityListener;
		writebyte = new byte[]{0x00, (byte) 0};		// Set voltage to high (~4.1v)
		readbyte = new byte[]{};

		if (IOIOUtils.getUtils().getMaster() != null)
			try {
				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
						readbyte, readbyte.length);
			} catch (Exception e1) {
				report(e1);
				activityListener.addFailOrPass(true, false, "IOIO Fault");
				return;
			}

		switch5vDC(true);

		try {
			CHGPin = IOIOUtils.getUtils().getCHGPinIn().read();
		} catch (Exception e2) {
			report(e2);
			activityListener.addFailOrPass(true, false, "IOIO Read Fault");
		}

		if (CHGPin) {
			showAlert(ac, true);
		} else {
			setSuccess(true);
			switch5vDC(false);
			activityListener.addFailOrPass(false, false, "");
		}

	}


	private void switch5vDC(Boolean state) {
		Boolean value;
		if (state) {
			value = false;
		} else {
			value = true;
		}

		try {
			IOIOUtils.getUtils().get_5V_DC().write(value);
		} catch (Exception e) {
			report(e);
			activityListener.addFailOrPass(true, false, "IOIO Fault");
		}
	}

	private void showAlert(Activity ac, final Boolean value) {
		if(isinterrupted)return;
		final AlertDialog.Builder builder = new AlertDialog.Builder(ac);
		builder.setTitle("Check Pink LED");
		builder.setMessage("Is It OFF?");
		builder.setPositiveButton("Yes, it's OFF", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(isinterrupted)return;
				if (value) {
					setSuccess(true);
					switch5vDC(false);
					activityListener.addFailOrPass(false, true, description);
				} else {
					setSuccess(false);
					switch5vDC(false);
					activityListener.addFailOrPass(false, false, description);
				}
			}
		});
		builder.setNegativeButton("No, it's ON", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(isinterrupted)return;
				if (value) {
					setSuccess(false);
					switch5vDC(false);
					activityListener.addFailOrPass(false, false, description);
				} else {
					setSuccess(false);
					switch5vDC(false);
					activityListener.addFailOrPass(false, true, description);
				}
			}
		});
		builder.setCancelable(true);
		builder.setOnCancelListener(new MyOnCancelListener(ac));
		alertDialog=builder.create();
		((Activity)activityListener).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isinterrupted) return;
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
