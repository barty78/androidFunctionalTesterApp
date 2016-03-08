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
import com.pietrantuono.sequencedb.SequenceContracts;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class UploadItemHolder extends SequenceItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;
    private final TextView reading;
    private final IconicsImageView result;
    private SequenceRowElement.UploadRowElement uploadRowElement;

    public UploadItemHolder(View v, Context context) {
        super(v, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        result = (IconicsImageView) itemView.findViewById(R.id.result);
        reading = (TextView) itemView.findViewById(R.id.reading);
    }

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
        if (uploadRowElement.isSuccess()) setPass();
        else setFail(uploadRowElement.getFailReason());
    }

    @Override
    public void setData(Cursor c) {
        String name = c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_NAME));
        String failReason = c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_OTHER_READING));
        long result = c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_RESULT));
        uploadRowElement = new SequenceRowElement.UploadRowElement(name != null ? name : "", true, result == 0 ? false : true, null, failReason != null ? failReason : "");
        setData(uploadRowElement, c.getPosition());

    }

    public void setFail(String text) {
        result.setVisibility(View.VISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_cancel);
        result.setColor(Color.RED);
        reading.setText(text != null ? text : "");
    }

    public void setPass() {
        result.setVisibility(View.VISIBLE);
        result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
        result.setColor(Color.GREEN);
    }

    @Override
    public int hashCode() {
        int result1 = testSeqNum != null ? testSeqNum.hashCode() : 0;
        result1 = 31 * result1 + (testName != null ? testName.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }
}
