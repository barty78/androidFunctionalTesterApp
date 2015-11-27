package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class MyDialogs {
	private static ProgressDialog progressDialog = null;
	public static void showIndeterminateProgress(Context context, String title,
			String message) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		Activity activity=(Activity)context;
		if(activity.isFinishing())return;
		progressDialog.show();

	}
	static ProgressDialog getDeterminateProgress(Context context, String title,
			String message) {
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(false);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		
		return progressDialog;

	}

	public static void dismissProgress() {
		if (progressDialog == null || !progressDialog.isShowing() ||progressDialog.getContext()==null)
			return;
		try {progressDialog.dismiss();
		} catch (Exception e) {
		}
	}
	
	public static void showAlert(Context context, String title,
			String message) {
		AlertDialog.Builder alert= new Builder(context);
		alert.setTitle(title);
		alert.setNegativeButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alert.setMessage(message);
		alert.create().show();

	}
	
	
	
	
}
