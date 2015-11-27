package com.pietrantuono.tests.implementations;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.tests.superclass.Test;
public class ReadModelNumberTest extends Test{
	private String modelnumber;
	public String getModelnumber() {
		return modelnumber;
	}
	private AlertDialog alertDialog;
	/**
	 * IMPORTANT: Bluetooth must be open using
	 * {@link com.pietrantuono.tests.implementations.BluetoothConnectTest} 
	 * Do not execute this test before opening Bluetooth
	 */
	public ReadModelNumberTest(Activity activity) {
		super(activity, null, "Model Number Check", false, false);
	}
	@Override
	public void execute() {
		if(isinterrupted)return;
		BTUtility btUtility=activityListener.getBtutility();
		if(btUtility==null) {report("BTUtility is null");return;}
		try {
			modelnumber = btUtility.getModelNumber();
		} catch (Exception e) {
		}
		if (modelnumber == null) {
			activityListener.addView("Model #: ", "NOT AVAILABLE",false);
			activityListener.addFailOrPass(true, false,description);
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder((Activity)activityListener);
			builder.setTitle("Please check model number");
			builder.setMessage("Model number is: " + modelnumber);
			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Success();
					activityListener.addView("Model #: ", modelnumber,false);
					activityListener.addFailOrPass(modelnumber, true, true,description);
				}
			});
			builder.setNegativeButton("Not OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					activityListener.addView("Model #: ", modelnumber,false);
					activityListener.addFailOrPass(modelnumber, true, false,description);
				}
			});
			alertDialog=builder.create();
			alertDialog.show();
		}
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try {alertDialog.dismiss();}catch(Exception e){}
	}
}
