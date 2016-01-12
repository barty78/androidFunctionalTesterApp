package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

import analytica.pericoach.android.DBManager;
import server.pojos.Device;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Holder extends RecyclerView.ViewHolder {
    private TextView barcode;
    private TextView serial;
    private TextView address;
    private TextView testtype_tv;
    private TextView executed_open;
    private TextView executed_closed;
    private TextView passed_open;
    private TextView passed_closed;
    private Context context;
    private Device device;

    public Holder(View itemView) {
        super(itemView);
        barcode= (TextView) itemView.findViewById(R.id.barcode_tv);
        serial= (TextView) itemView.findViewById(R.id.serial_tv);
        address = (TextView) itemView.findViewById(R.id.address_tv);
        executed_closed= (TextView) itemView.findViewById(R.id.closed_executed);
        executed_open= (TextView) itemView.findViewById(R.id.open_executed);
        passed_closed= (TextView) itemView.findViewById(R.id.closed_passed);
        passed_open= (TextView) itemView.findViewById(R.id.open_passed);
        testtype_tv= (TextView) itemView.findViewById(R.id.testtype_tv);
    }

    public void setData(Device device, Context context){
        this.context=context;
        this.device=device;
        if(device==null)return;
        if(device.getBarcode()!=null)barcode.setText(device.getBarcode());
        if(device.getSerial()!=null)serial.setText(device.getSerial());
        if(device.getBt_addr()!=null)address.setText(device.getBt_addr());
        pouplateExecutedAndPassed();
    }

    private void pouplateExecutedAndPassed() {
        analytica.pericoach.android.Job job=DBManager.getJobByJobID((int) device.getJobId(), context);
        if(job==null)return;
        if(getFirstBit(job.getTesttype_id())==1){/*OPEN*/testtype_tv.setText(context.getResources().getString(R.string.open_open));}
        else if (getSecondBit(job.getTesttype_id())==1){/*CLOSED*/testtype_tv.setText(context.getResources().getString(R.string.closed));}
        else {/*NA*/}
        int typeID = job.getTesttype_id();

    }



    private int getFirstBit(int integer){
        return integer & 1;
    }
    private int getSecondBit(int integer){
        return integer>>1 & 1;
    }

}
