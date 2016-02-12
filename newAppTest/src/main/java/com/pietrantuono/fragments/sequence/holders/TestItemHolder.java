package com.pietrantuono.fragments.sequence.holders;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.fragments.sequence.SequenceRowElement;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class TestItemHolder extends SequenceItemHolder {
    private final TextView testSeqNum;
    private final TextView testName;
    private final TextView reading;
    private final IconicsImageView result;

    public TestItemHolder(View itemView, Context context) {
        super(itemView, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        reading = (TextView) itemView.findViewById(R.id.reading);
        result = (IconicsImageView) itemView.findViewById(R.id.result);
    }

    @Override
    public void setData(SequenceRowElement.RowElement rowElement, int position) {
        if (!(rowElement instanceof SequenceRowElement.TestRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.TestRowElement testRowElement = (SequenceRowElement.TestRowElement) rowElement;

        testSeqNum.setText("" +  (position+ 1));
        testName.setText(testRowElement.getDescription() != null ? testRowElement.getDescription() : context.getString(R.string.no_description));
        reading.setText(testRowElement.getReading() != null ? testRowElement.getReading() : "");
        if (testRowElement.isSuccess()) {
            result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
            if (testRowElement.istest()) {
                result.setColor(Color.GREEN);
            } else {
                result.setColor(context.getResources().getColor(R.color.primary));
            }
        } else {
            result.setIcon(GoogleMaterial.Icon.gmd_cancel);
            if (testRowElement.istest()) {
                result.setColor(Color.RED);
            } else {
                result.setColor(context.getResources().getColor(R.color.primary));
            }
        }
        //if(!testRowElement.isSensorTest())itemView.setOnClickListener((new ListItemClickListener((AppCompatActivity) context,testRowElement.getTestToBeParsed())));
        //else{itemView.setOnClickListener((new SensorItemClickListener((AppCompatActivity) context,testRowElement.getTestToBeParsed())));}
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
        result1 = 31 * result1 + (reading != null ? reading.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        return result1;
    }
}
