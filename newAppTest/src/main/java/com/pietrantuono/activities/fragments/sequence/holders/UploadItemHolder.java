package com.pietrantuono.activities.fragments.sequence.holders;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.activities.fragments.sequence.SequenceRowElement;
import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UploadItemHolder extends SequenceItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;
    private final IconicsImageView result;
    private final DonutProgress donutProgress;

    public UploadItemHolder(View v, Context context) {
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
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        donutProgress.setProgress(0);
    }

    public void setFail(String text) {
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_cancel);
        result.setColor(Color.RED);
        testName.setText(text);
    }

    public void setPass() {
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
        result.setColor(Color.GREEN);
    }

    public void setProgress(int progress) {
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.VISIBLE);
        donutProgress.setProgress(progress);
    }

    public void setWait() {
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_hourglass_empty);
        result.setColor(context.getResources().getColor(R.color.primary));
    }

    @Override
    public int hashCode() {
        int result1 = testSeqNum != null ? testSeqNum.hashCode() : 0;
        result1 = 31 * result1 + (testName != null ? testName.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (donutProgress != null ? donutProgress.hashCode() : 0);
        return result1;
    }
}
