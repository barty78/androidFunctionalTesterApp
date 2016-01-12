package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;

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
    private ImageView passed;
    private Context context;
    private Device device;

    public Holder(View itemView) {
        super(itemView);
        barcode= (TextView) itemView.findViewById(R.id.barcode_tv);
        serial= (TextView) itemView.findViewById(R.id.serial_tv);
        address = (TextView) itemView.findViewById(R.id.address_tv);
    }

    public void setData(Device device, Context context){
        this.context=context;
        this.device=device;
        if(device==null)return;
        if(device.getBarcode()!=null)barcode.setText(device.getBarcode());
        if(device.getSerial()!=null)serial.setText(device.getSerial());
        if(device.getBt_addr()!=null)address.setText(device.getBt_addr());
    }

    private analytica.pericoach.android.Job getJob(Device device){
        long jobID = device.getJobId();
        DBManager manager= new DBManager(context);
        ArrayList<analytica.pericoach.android.Job> jobs = manager.getAllJobs();
//        for(analytica.pericoach.android.Job job:jobs){
//            job.g
//            if(job.getId()==jobID)return job;
//        }

        return null;
    }


}
