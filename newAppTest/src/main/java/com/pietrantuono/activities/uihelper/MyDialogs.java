package com.pietrantuono.activities.uihelper;

import com.pietrantuono.activities.MyOnCancelListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class MyDialogs {

	public static void createAlertDialog(Activity activity,String title,String message,String positivetext,String negativetext,  MyOnCancelListener oncancellistener,final MyDialogInterface callback){
		AlertDialog.Builder builder= new Builder(activity);
		if(title!=null)builder.setTitle(title);
		if(message!=null)builder.setMessage(message);
		if(positivetext!=null)builder.setPositiveButton(positivetext, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback!=null)callback.yes();	
			}
		});
		if(negativetext!=null)builder.setNegativeButton(negativetext, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(callback!=null)callback.no();
			}
		});
		builder.setCancelable(false);
		if(oncancellistener!=null)builder.setCancelable(true);
		if(oncancellistener!=null)builder.setOnCancelListener(oncancellistener);
		if(activity.isFinishing())return;
		builder.create().show();
	}


}
