package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import analytica.pericoach.android.DBManager;
import server.pojos.Device;
import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Holder extends RecyclerView.ViewHolder {
    private TextView barcode;
    private TextView serial;
    private TextView address;
    private TextView passed_tv;
    private Context context;
    private Device device;

    public Holder(View itemView) {
        super(itemView);
        barcode = (TextView) itemView.findViewById(R.id.barcode_tv);
        serial = (TextView) itemView.findViewById(R.id.serial_tv);
        address = (TextView) itemView.findViewById(R.id.address_tv);
        passed_tv = (TextView) itemView.findViewById(R.id.passed_tv);
    }

    public void setData(Device device, Context context) {
        this.context = context;
        this.device = device;
        if (device == null) return;
        if (device.getBarcode() != null) barcode.setText(device.getBarcode());
        if (device.getSerial() != null) serial.setText(device.getSerial());
        if (device.getBt_addr() != null) address.setText(device.getBt_addr());
        pouplateExecutedAndPassed();
    }

    private void pouplateExecutedAndPassed() {
//        analytica.pericoach.android.Job job = DBManager.getJobByJobID((int) device.getJobId(), context);
        Job job = PeriCoachTestApplication.getCurrentJob();
        if (job == null) return;

        Log.d("HOLDER", "TestType: " + String.valueOf(job.getTesttypeId()) + " | Exec_Tests: " + String.valueOf(device.getExec_Tests()));
        if ((device.getStatus() & job.getTesttypeId()) == job.getTesttypeId()) {
            Log.d("HOLDER", "TestType: " + String.valueOf(job.getTesttypeId()) + " | Status: " + String.valueOf(device.getStatus()));
            passed_tv.setText(context.getString(R.string.passed_passed));
        }
    }


    private int getFirstBit(int integer) {
        return integer & 1;
    }

    private int getSecondBit(int integer) {
        return integer >> 1 & 1;
    }

}
