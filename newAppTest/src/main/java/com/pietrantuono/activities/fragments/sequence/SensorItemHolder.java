package com.pietrantuono.activities.fragments.sequence;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.activities.fragments.SensorItemClickListener;
import com.pietrantuono.pericoach.newtestapp.R;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SensorItemHolder extends SequenceItemHolder {
    private final TextView avg1;
    private final TextView avg2;
    private final TextView passfail;
    private TextView testSeqNum;
    private TextView testName;
    private TextView reading;
    private IconicsImageView result1;
    private IconicsImageView result2;
    private TextView avg0;

    public SensorItemHolder(View itemView, Context context) {
        super(itemView, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        reading = (TextView) itemView.findViewById(R.id.reading);
        result1 = (IconicsImageView) itemView.findViewById(R.id.result1);
        result2 = (IconicsImageView) itemView.findViewById(R.id.result2);
        avg0= (TextView) itemView.findViewById(R.id.avg0);
        avg1 = (TextView) itemView.findViewById(R.id.avg1);
        avg2 = (TextView) itemView.findViewById(R.id.avg2);
        passfail = (TextView) itemView.findViewById(R.id.pass_or_fail_avg_text);
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

        ProgressBar progress = (ProgressBar) itemView.findViewById(R.id.pass_or_fail_avg_indicator);
        if (rowElement.getmSensorResult().getSensor0AvgPass() && rowElement.getmSensorResult().getSensor1AvgPass()
                && rowElement.getmSensorResult().getSensor2AvgPass()) {
            passfail.setText("PASS");
            Resources res = activity.getResources();
            Drawable background = null;
            background = res.getDrawable(R.drawable.greenprogress);
            progress.setProgressDrawable(background);

        } else {
            passfail.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            progress.setProgressDrawable(background);
        }

        TextView stability0 = (TextView) itemView.findViewById(R.id.stability0);
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

        TextView stability2 = (TextView) itemView.findViewById(R.id.stability2);
        if (rowElement.getmSensorResult().getSensor2stabilitypass())
            stability2.setTextColor(Color.GREEN);
        else
            stability2.setTextColor(Color.RED);
        stability2.setText("" + (rowElement.getmSensorResult().getSensor2max() - rowElement.getmSensorResult().getSensor2min() > 0
                ? rowElement.getmSensorResult().getSensor2max() - rowElement.getmSensorResult().getSensor2min() : (short) 0));

        TextView pass_or_fail_stability_text = (TextView) itemView.findViewById(R.id.pass_or_fail_stability_text);
        ProgressBar pass_or_fail_stability_indicator = (ProgressBar) v
                .findViewById(R.id.pass_or_fail_stability_indicator);
        if (rowElement.getmSensorResult().getSensor0stabilitypass() && rowElement.getmSensorResult().getSensor1stabilitypass()
                && rowElement.getmSensorResult().getSensor2stabilitypass()) {
            pass_or_fail_stability_text.setText("PASS");
            Resources res = activity.getResources();
            Drawable background = null;
            background = res.getDrawable(R.drawable.greenprogress);
            pass_or_fail_stability_indicator.setProgressDrawable(background);

        } else {
            pass_or_fail_stability_text.setText("FAIL");
            Resources res = activity.getResources();
            Drawable background = res.getDrawable(R.drawable.redprogress);
            pass_or_fail_stability_indicator.setProgressDrawable(background);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ll.addView(v);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });
    }
}
