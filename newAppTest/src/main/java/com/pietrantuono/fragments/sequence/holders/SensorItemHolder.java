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
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.fragments.sequence.SequenceRowElement;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sequencedb.SequenceContracts;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SensorItemHolder extends SequenceItemHolder {
    private final TextView avg1;
    private final TextView avg2;
    private final TextView testSeqNum;
    private final TextView testName;
    private final IconicsImageView result1_avg;
    private final IconicsImageView result_stability;
    private final TextView avg0;
    private final TextView stability0;
    private final TextView stability1;
    private final TextView stability2;

    public SensorItemHolder(View itemView, Context context) {
        super(itemView, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        result1_avg = (IconicsImageView) itemView.findViewById(R.id.result1_avg);
        result_stability = (IconicsImageView) itemView.findViewById(R.id.result_stability);
        avg0= (TextView) itemView.findViewById(R.id.avg0);
        avg1 = (TextView) itemView.findViewById(R.id.avg1);
        avg2 = (TextView) itemView.findViewById(R.id.avg2);
        stability0 = (TextView) itemView.findViewById(R.id.stability0);
        stability1 = (TextView) itemView.findViewById(R.id.stability1);
        stability2 = (TextView) itemView.findViewById(R.id.stability2);
    }

    @Override
    public void setData(SequenceRowElement.RowElement sensorRowElement, int position) {
        if (!(sensorRowElement instanceof SequenceRowElement.SensorTestRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.SensorTestRowElement rowElement = (SequenceRowElement.SensorTestRowElement) sensorRowElement;
        if(rowElement.getmSensorResult()==null)return;
        
        testName.setText(rowElement.getmSensorResult().getDescription() != null ? rowElement.getmSensorResult().getDescription() : context.getString(R.string.no_description));
        testSeqNum.setText("" +  (position+ 1));
         
        if (rowElement.getmSensorResult().getSensor0AvgPass())
            avg0.setTextColor(Color.GREEN);
        else
            avg0.setTextColor(Color.RED);
        avg0.setText(Short.toString(rowElement.getmSensorResult().getSensor0avg()));

        
        if (rowElement.getmSensorResult().getSensor1AvgPass())
            avg1.setTextColor(Color.GREEN);
        else
            avg1.setTextColor(Color.RED);
        avg1.setText(Short.toString(rowElement.getmSensorResult().getSensor1avg()));


        if (rowElement.getmSensorResult().getSensor2AvgPass())
            avg2.setTextColor(Color.GREEN);
        else
            avg2.setTextColor(Color.RED);
        avg2.setText(Short.toString(rowElement.getmSensorResult().getSensor2avg()));

        if (rowElement.getmSensorResult().getSensor0AvgPass() && rowElement.getmSensorResult().getSensor1AvgPass()
                && rowElement.getmSensorResult().getSensor2AvgPass()) {
            //PASS
            result1_avg.setIcon(GoogleMaterial.Icon.gmd_check_circle);
            result1_avg.setColor(Color.GREEN);
        } else {
            //FAIL
            result1_avg.setIcon(GoogleMaterial.Icon.gmd_cancel);
            result1_avg.setColor(Color.RED);
        }

        if (rowElement.getmSensorResult().getSensor0stabilitypass())
            stability0.setTextColor(Color.GREEN);
        else
            stability0.setTextColor(Color.RED);
        stability0.setText("" + (rowElement.getmSensorResult().getSensor0max() - rowElement.getmSensorResult().getSensor0min() > 0
                ? rowElement.getmSensorResult().getSensor0max() - rowElement.getmSensorResult().getSensor0min() : (short) 0));

        if (rowElement.getmSensorResult().getSensor1stabilitypass())
            stability1.setTextColor(Color.GREEN);
        else
            stability1.setTextColor(Color.RED);
        stability1.setText("" + (rowElement.getmSensorResult().getSensor1max() - rowElement.getmSensorResult().getSensor1min() > 0
                ? rowElement.getmSensorResult().getSensor1max() - rowElement.getmSensorResult().getSensor1min() : (short) 0));

        if (rowElement.getmSensorResult().getSensor2stabilitypass())
            stability2.setTextColor(Color.GREEN);
        else
            stability2.setTextColor(Color.RED);
        stability2.setText("" + (rowElement.getmSensorResult().getSensor2max() - rowElement.getmSensorResult().getSensor2min() > 0
                ? rowElement.getmSensorResult().getSensor2max() - rowElement.getmSensorResult().getSensor2min() : (short) 0));

        if (rowElement.getmSensorResult().getSensor0stabilitypass() && rowElement.getmSensorResult().getSensor1stabilitypass()
                && rowElement.getmSensorResult().getSensor2stabilitypass()) {
            //PASS
            result_stability.setIcon(GoogleMaterial.Icon.gmd_check_circle);
            result_stability.setColor(Color.GREEN);
        } else {
            //FAIL
            result_stability.setIcon(GoogleMaterial.Icon.gmd_cancel);
            result_stability.setColor(Color.RED);
        }
        itemView.setOnClickListener((new SensorItemClickListener((AppCompatActivity) context,rowElement.getTestToBeParsed())));
    }

    @Override
    public void setData(Cursor c) {
        SequenceRowElement.SensorTestRowElement rowElement = new SequenceRowElement.SensorTestRowElement(null);
        NewMSensorResult result = new NewMSensorResult(null);
        result.setSensor0avg((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S0_AVG)));
        result.setSensor0min((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S0_MIN)));
        result.setSensor0max((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S0_MAX)));
        result.setSensor0AvgPass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S0_AVG_PASS)) == 1 ? true : false);
        result.setSensor0stabilitypass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S0_STABILITY_PASS)) == 1 ? true : false);

        result.setSensor1avg((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S1_AVG)));
        result.setSensor1min((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S1_MIN)));
        result.setSensor1max((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S1_MAX)));
        result.setSensor1AvgPass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S1_AVG_PASS)) == 1 ? true : false);
        result.setSensor1stabilitypass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S1_STABILITY_PASS)) == 1 ? true : false);

        result.setSensor2avg((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S2_AVG)));
        result.setSensor2min((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S2_MIN)));
        result.setSensor2max((short) c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S2_MAX)));
        result.setSensor2AvgPass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S2_AVG_PASS)) == 1 ? true : false);
        result.setSensor2stabilitypass(c.getLong(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_S2_STABILITY_PASS)) == 1 ? true : false);
        result.setDescription(c.getString(c.getColumnIndexOrThrow(SequenceContracts.Tests.TABLE_TESTS_NAME)));

        rowElement.setmSensorResult(result);
        setData(rowElement,c.getPosition());
    }

    @Override
    public int hashCode() {
        int result = avg1 != null ? avg1.hashCode() : 0;
        result = 31 * result + (avg2 != null ? avg2.hashCode() : 0);
        result = 31 * result + (testSeqNum != null ? testSeqNum.hashCode() : 0);
        result = 31 * result + (testName != null ? testName.hashCode() : 0);
        result = 31 * result + (result1_avg != null ? result1_avg.hashCode() : 0);
        result = 31 * result + (result_stability != null ? result_stability.hashCode() : 0);
        result = 31 * result + (avg0 != null ? avg0.hashCode() : 0);
        result = 31 * result + (stability0 != null ? stability0.hashCode() : 0);
        result = 31 * result + (stability2 != null ? stability2.hashCode() : 0);
        return result;
    }
}
