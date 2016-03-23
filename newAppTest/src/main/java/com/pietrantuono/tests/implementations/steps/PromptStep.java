package com.pietrantuono.tests.implementations.steps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.tests.superclass.Test;

public class PromptStep extends Test implements Step{

    private AlertDialog alertDialog;

    /**
     * @param activity
     * @param description
     */
    public PromptStep(Activity activity, String description) {
        super(activity, null, description, false, false, 0, 0, 0);
    }

    @Override
    public void execute() {
        if(isinterrupted)return;
        final AlertDialog.Builder builder = new AlertDialog.Builder((Activity)activityListener);
        builder.setTitle("ALERT");
        builder.setMessage(description);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isinterrupted) return;
                Success();
                activityListener.addFailOrPass(true, true, "");
            }
        });
        builder.setNegativeButton("No, it's OFF", new DialogInterface.OnClickListener() {
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
                if (isinterrupted) return;
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