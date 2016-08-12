package com.pietrantuono.fragments.sidePanel.holders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.fragments.sequence.SequenceRowElement;
import com.pietrantuono.fragments.sequence.holders.SequenceItemHolder;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class ValueItemHolder extends SidePanelItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;

    public ValueItemHolder(View itemView, Context context) {
        super(itemView, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
    }

    private void setData(SequenceRowElement.RowElement rowElement, int position) {
        if (!(rowElement instanceof SequenceRowElement.TestRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.TestRowElement testRowElement = (SequenceRowElement.TestRowElement) rowElement;

        testSeqNum.setText("" +  (position+ 1));
        testName.setText(testRowElement.getDescription() != null ? testRowElement.getDescription() : context.getString(R.string.no_description));

    }

    @Override
    public void setData(Cursor c) {
        boolean isNormalTest=c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_NORMAL_TEST))==0?false:true;
        boolean result =c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_RESULT))==0?false:true;
        String reading=c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_READING))!=null?c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_READING)):"";
        String otherreading=c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_OTHER_READING))!=null?c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_OTHER_READING)):"";
        String name=c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_NAME))!=null?c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_NAME)):"";
        boolean issensorttest=c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_IS_SENSOR_TEST))==0?false:true;

        SequenceRowElement.TestRowElement testRowElement = new SequenceRowElement.TestRowElement(true,result,reading,otherreading,name,issensorttest,null,null);
        setData(testRowElement,c.getPosition());
    }

    @Override
    public int hashCode() {
        int result1 = testSeqNum != null ? testSeqNum.hashCode() : 0;
        result1 = 31 * result1 + (testName != null ? testName.hashCode() : 0);
        return result1;
    }
}
