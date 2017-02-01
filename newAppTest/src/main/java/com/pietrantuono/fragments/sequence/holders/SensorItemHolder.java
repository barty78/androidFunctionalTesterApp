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

import java.lang.reflect.Array;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SensorItemHolder extends SequenceItemHolder {
    private final TextView[] avgs;
    private final TextView testSeqNum;
    private final TextView testName;
    private final IconicsImageView result_avg;
    private final IconicsImageView result_stability;
    private final TextView[] vars;
    private final TextView[] headers;
    private int sensorToTest = -1;

    public SensorItemHolder(View itemView, Context context) {
        super(itemView, context);
        testSeqNum = (TextView) itemView.findViewById(R.id.testSeqNum);
        testName = (TextView) itemView.findViewById(R.id.testName);
        result_avg = (IconicsImageView) itemView.findViewById(R.id.result1_avg);
        result_stability = (IconicsImageView) itemView.findViewById(R.id.result_stability);
        headers = new TextView[]{(TextView) itemView.findViewById(R.id.headerSensor0),
                                (TextView) itemView.findViewById(R.id.headerSensor1),
                                (TextView) itemView.findViewById(R.id.headerSensor2)};
        avgs = new TextView[]{(TextView) itemView.findViewById(R.id.avg0),
                            (TextView) itemView.findViewById(R.id.avg1),
                            (TextView) itemView.findViewById(R.id.avg2)};
        vars = new TextView[]{(TextView) itemView.findViewById(R.id.stability0),
                (TextView) itemView.findViewById(R.id.stability1),
                (TextView) itemView.findViewById(R.id.stability2)};
    }

    private void setData(SequenceRowElement.RowElement sensorRowElement, int position) {
        if (!(sensorRowElement instanceof SequenceRowElement.SensorTestRowElement))
            throw new RuntimeException("Wrong data " + Log.getStackTraceString(new Exception()));
        SequenceRowElement.SensorTestRowElement rowElement = (SequenceRowElement.SensorTestRowElement) sensorRowElement;
        if(rowElement.getmSensorResult()==null)return;

        final String[] parts = rowElement.getmSensorResult().getDescription().split(",");

        if (parts[0].length() == 19) {
            sensorToTest = Character.digit(parts[0].charAt(7), 10);
        }

        testName.setText(rowElement.getmSensorResult().getDescription() != null ? rowElement.getmSensorResult().getDescription() : context.getString(R.string.no_description));
        testSeqNum.setText("" +  (position+ 1));

        if (sensorToTest != -1) {
            for (int i = 0; i < Array.getLength(avgs); i++){
                avgs[i].setVisibility(View.INVISIBLE);
                vars[i].setVisibility(View.INVISIBLE);
                headers[i].setVisibility(View.INVISIBLE);
            }
            avgs[sensorToTest].setVisibility(View.VISIBLE);
            vars[sensorToTest].setVisibility(View.VISIBLE);
            headers[sensorToTest].setVisibility(View.VISIBLE);

            if (rowElement.getmSensorResult().getSensorAvgPass(sensorToTest))
                avgs[sensorToTest].setTextColor(Color.GREEN);
            else
                avgs[sensorToTest].setTextColor(Color.RED);
            avgs[sensorToTest].setText(Short.toString(rowElement.getmSensorResult().getSensorAvg(sensorToTest)));

            if (rowElement.getmSensorResult().getSensorStabilityPass(sensorToTest))
                vars[sensorToTest].setTextColor(Color.GREEN);
            else
                vars[sensorToTest].setTextColor(Color.RED);
            vars[sensorToTest].setText("" + (rowElement.getmSensorResult().getSensorVar(sensorToTest) > 0
                    ? rowElement.getmSensorResult().getSensorVar(sensorToTest) : (short) 0));

            if (rowElement.getmSensorResult().getSensorAvgPass(sensorToTest)) {
                //PASS
                result_avg.setIcon(GoogleMaterial.Icon.gmd_check_circle);
                result_avg.setColor(Color.GREEN);
            } else {
                //FAIL
                result_avg.setIcon(GoogleMaterial.Icon.gmd_cancel);
                result_avg.setColor(Color.RED);
            }

            if (rowElement.getmSensorResult().getSensorStabilityPass(sensorToTest)) {
                //PASS
                result_stability.setIcon(GoogleMaterial.Icon.gmd_check_circle);
                result_stability.setColor(Color.GREEN);
            } else {
                //FAIL
                result_stability.setIcon(GoogleMaterial.Icon.gmd_cancel);
                result_stability.setColor(Color.RED);
            }

        } else {

            for (int i = 0; i < Array.getLength(avgs); i++) {
                if (rowElement.getmSensorResult().getSensorAvgPass(i))
                    avgs[i].setTextColor(Color.GREEN);
                else
                    avgs[i].setTextColor(Color.RED);
                avgs[i].setText(Short.toString(rowElement.getmSensorResult().getSensorAvg(i)));

                if (rowElement.getmSensorResult().getSensorStabilityPass(i))
                    vars[i].setTextColor(Color.GREEN);
                else
                    vars[i].setTextColor(Color.RED);
                vars[i].setText(Short.toString(rowElement.getmSensorResult().getSensorVar(i)));
            }

            if (rowElement.getmSensorResult().getSensor0AvgPass() && rowElement.getmSensorResult().getSensor1AvgPass()
                    && rowElement.getmSensorResult().getSensor2AvgPass()) {
                //PASS
                result_avg.setIcon(GoogleMaterial.Icon.gmd_check_circle);
                result_avg.setColor(Color.GREEN);
            } else {
                //FAIL
                result_avg.setIcon(GoogleMaterial.Icon.gmd_cancel);
                result_avg.setColor(Color.RED);
            }

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
        int result = avgs[0] != null ? avgs[0].hashCode() : 0;
        result = 31 * result + (avgs[1] != null ? avgs[1].hashCode() : 0);
        result = 31 * result + (avgs[2] != null ? avgs[2].hashCode() : 0);
        result = 31 * result + (testSeqNum != null ? testSeqNum.hashCode() : 0);
        result = 31 * result + (testName != null ? testName.hashCode() : 0);
        result = 31 * result + (result_avg != null ? result_avg.hashCode() : 0);
        result = 31 * result + (result_stability != null ? result_stability.hashCode() : 0);
        result = 31 * result + (vars[0] != null ? vars[0].hashCode() : 0);
        result = 31 * result + (vars[1] != null ? vars[1].hashCode() : 0);
        result = 31 * result + (vars[2] != null ? vars[2].hashCode() : 0);
        return result;
    }
}