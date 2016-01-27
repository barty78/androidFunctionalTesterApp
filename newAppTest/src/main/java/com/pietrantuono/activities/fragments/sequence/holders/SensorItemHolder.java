package com.pietrantuono.activities.fragments.sequence.holders;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.activities.fragments.sequence.SequenceRowElement;
import com.pietrantuono.activities.fragments.sequence.holders.SequenceItemHolder;
import com.pietrantuono.pericoach.newtestapp.R;

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
        stability2 = (TextView) itemView.findViewById(R.id.stability2);
    }

    @Override
    public void setData(SequenceRowElement.RowElement sensorRowElement) {
        if (!(sensorRowElement instanceof SequenceRowElement.SensorTestRowElement))
            throw new RuntimeException("Wrong adata " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.SensorTestRowElement rowElement = (SequenceRowElement.SensorTestRowElement) sensorRowElement;
        if(rowElement.getmSensorResult()==null)return;
        
        testName.setText(rowElement.getmSensorResult().getDescription() != null ? rowElement.getmSensorResult().getDescription() : context.getString(R.string.no_description));
        testSeqNum.setText("" + (rowElement.getSequence().getCurrentTestNumber() + 1));
         
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

        TextView stability1 = (TextView) itemView.findViewById(R.id.stability1);
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
}