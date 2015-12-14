package com.pietrantuono.tests.implementations;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.tests.superclass.Test;
public class LedCheckTest extends Test {
	private String nameOfLed;
	private AlertDialog alertDialog;
	public LedCheckTest(Activity activity,String nameOfLed ,String description) {
		super(activity, null, description, false, false, 0, 0, 0);
		this.nameOfLed=nameOfLed;
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		final AlertDialog.Builder builder = new AlertDialog.Builder((Activity)activityListener);
		builder.setTitle(nameOfLed+" LED check");
		builder.setMessage("Please check if "+nameOfLed+" LED is ON");
		builder.setPositiveButton("Yes, it's ON", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(isinterrupted)return;
				Success();
				activityListener.addFailOrPass(true, true, "");
			}
		});
		builder.setNegativeButton("No, it's OFF", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(isinterrupted)return;
				activityListener.addFailOrPass(true, false, "");
			}
		});
		builder.setCancelable(true);
		builder.setOnCancelListener(new MyOnCancelListener((Activity)activityListener));
		alertDialog=builder.create();
		((Activity)activityListener).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(isinterrupted)return;
				alertDialog.show();
			}
		});
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {alertDialog.dismiss();}catch(Exception e){}
	}
}
