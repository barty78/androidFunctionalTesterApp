package com.pietrantuono.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.*;

public class MyOnCancelListener implements OnCancelListener {
	private final Activity myActivity;
	private final Callback callback;

	public MyOnCancelListener(Activity activity) {
		this.myActivity = activity;
		this.callback=(Callback)activity;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		AlertDialog.Builder builder= new Builder(myActivity);
		builder.setTitle("Operation Cancelled");
		builder.setMessage("Do you want to close the app?");
		builder.setPositiveButton("Yes, let's close", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callback.closeActivity();
			}
		});
		builder.setNegativeButton("No, I don't want to close", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	}
	
	static interface Callback{
		void closeActivity();
	}
	
}
