package com.pietrantuono.fragments.sequence.holders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.fragments.sequence.SequenceRowElement;
import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UploadItemHolder extends SequenceItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;
    private final TextView reading;
    private final IconicsImageView result;
    private final DonutProgress donutProgress;
    private SequenceRowElement.UploadRowElement uploadRowElement;

    public UploadItemHolder(View v, Context context) {
        super(v, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        result = (IconicsImageView) itemView.findViewById(R.id.result);
        donutProgress = (DonutProgress) itemView.findViewById(R.id.progress);
        reading = (TextView) itemView.findViewById(R.id.reading);
    }

    @Override
    public void setData(SequenceRowElement.RowElement element, int position) {
        if (!(element instanceof SequenceRowElement.UploadRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        uploadRowElement = (SequenceRowElement.UploadRowElement) element;
        try {
            testSeqNum.setText("" + (position + 1));
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
        if (uploadRowElement.getState() != SequenceRowElement.UploadRowElement.NONE) setState();
    }

    @Override
    public void setData(Cursor c) {

    }

    private void setState() {
        if(uploadRowElement==null)return;
        int state=uploadRowElement.getState();
        switch (state) {
            case SequenceRowElement.UploadRowElement.NONE:
                return;
            case SequenceRowElement.UploadRowElement.WAIT:
                setWait();
                break;
            case SequenceRowElement.UploadRowElement.PROGRESS:
                setProgress(uploadRowElement.getProgressValue());
                break;
            case SequenceRowElement.UploadRowElement.PASS:
                setPass();
                break;
            case SequenceRowElement.UploadRowElement.FAIL:
                setFail(uploadRowElement.getFailReason());
                break;
            case SequenceRowElement.UploadRowElement.RESET:
                reset();
                break;
        }
    }

    public void reset() {
        uploadRowElement.setState(SequenceRowElement.UploadRowElement.RESET);
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        donutProgress.setProgress(0);
    }

    public void setFail(String text) {
        uploadRowElement.setState(SequenceRowElement.UploadRowElement.FAIL);
        uploadRowElement.setFailReason(text);
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_cancel);
        result.setColor(Color.RED);
        reading.setText(text != null ? text : "");
    }

    public void setPass() {
        uploadRowElement.setState(SequenceRowElement.UploadRowElement.PASS);
        result.setVisibility(View.VISIBLE);
        donutProgress.setVisibility(View.INVISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
        result.setColor(Color.GREEN);
    }

    public void setProgress(int progress) {
        uploadRowElement.setState(SequenceRowElement.UploadRowElement.PROGRESS);
        uploadRowElement.setProgressValue(progress);
        result.setVisibility(View.INVISIBLE);
        donutProgress.setVisibility(View.VISIBLE);
        donutProgress.setProgress(progress);
    }

    public void setWait() {
        uploadRowElement.setState(SequenceRowElement.UploadRowElement.WAIT);
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
