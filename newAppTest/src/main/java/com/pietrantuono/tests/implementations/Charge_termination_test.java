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

	public Charge_termination_test(Activity activity, IOIO ioio,
			String description) {
		super(activity, ioio, description, false, false);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		Activity ac= (Activity)activityListener;
		byte[] writebyte = new byte[] { 0x00, 0x00 };
		byte[] readbyte = new byte[] {};
		if (IOIOUtils.getUtils().getMaster() != null)
			try {
				IOIOUtils.getUtils().getMaster().writeRead(0x60, false, writebyte, writebyte.length,
						readbyte, readbyte.length);
			} catch (Exception e1) {
				report(e1);
				activityListener.addFailOrPass(true, false,description);
				return;
			}
		final AlertDialog.Builder builder = new AlertDialog.Builder(ac);
		builder.setTitle("Pink LED check");
		builder.setMessage("Please check pink LED it's OFF");
		builder.setPositiveButton("Yes, it's OFF", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setSuccess(true);
				activityListener.addFailOrPass(true, true,description);
			}
		});
		builder.setNegativeButton("No, it's ON", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setSuccess(false);
				activityListener.addFailOrPass(true, false,description);
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
