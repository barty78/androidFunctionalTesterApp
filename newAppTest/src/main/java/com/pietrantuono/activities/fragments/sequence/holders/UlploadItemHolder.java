package com.pietrantuono.activities.fragments.sequence.holders;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.activities.fragments.sequence.SequenceRowElement;
import com.pietrantuono.activities.uihelper.UIHelper;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UlploadItemHolder extends SequenceItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;
    private final IconicsImageView result;
    private final DonutProgress donutProgress;
    private RotateAnimation animation;

    public UlploadItemHolder(View v, Context context) {
        super(v, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        result = (IconicsImageView) itemView.findViewById(R.id.result);
        donutProgress = (DonutProgress) itemView.findViewById(R.id.progress);
    }

    @Override
    public void setData(SequenceRowElement.RowElement element) {
        if (!(element instanceof SequenceRowElement.UploadRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.UploadRowElement uploadRowElement = (SequenceRowElement.UploadRowElement) element;
        try {
            testSeqNum.setText("" + (uploadRowElement.getSequence().getCurrentTestNumber() + 1));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        try {
            String testdescription = uploadRowElement.getDescription();
            if (testdescription != null)
                testName.setText(testdescription);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.VISIBLE);
    }

    public void reset() {
        if(animation!=null)animation.cancel();
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        donutProgress.setProgress(0);
    }

    public void setFail() {
        if(animation!=null)animation.cancel();
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_cancel);
        result.setColor(Color.RED);
    }

    public void setPass() {
        if(animation!=null)animation.cancel();
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
        result.setColor(Color.GREEN);
    }

    public void setProgress(int progress) {
        if(animation!=null)animation.cancel();
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.VISIBLE);
        donutProgress.setProgress(progress);
    }

    public void setWait(){
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_hourglass_empty);
        result.setColor(context.getResources().getColor(R.color.primary));
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        result.measure(display.getWidth(), display.getHeight());
        float pivotX = result.getMeasuredWidth() / 2f;
        float pivotY = result.getMeasuredHeight()/ 2f;
        animation= new RotateAnimation(0,360,pivotX,pivotY);
        animation.setDuration(1*1000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        result.startAnimation(animation);
    }
}
