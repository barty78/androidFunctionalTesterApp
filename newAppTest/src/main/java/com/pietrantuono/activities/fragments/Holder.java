package com.pietrantuono.activities.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Device;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class Holder extends RecyclerView.ViewHolder {
    private TextView barcode;
    private TextView serial;
    private TextView address;
    private ImageView passed;


    public Holder(View itemView) {
        super(itemView);
        barcode= (TextView) itemView.findViewById(R.id.barcode_tv);
        serial= (TextView) itemView.findViewById(R.id.serial_tv);
        address = (TextView) itemView.findViewById(R.id.address_tv);
    }

    public void setData(Device device){
        if(device==null)return;
        if(device.getBarcode()!=null)barcode.setText(device.getBarcode());
        if(device.getSerial()!=null)serial.setText(device.getSerial());
        if(device.getBt_addr()!=null)address.setText(device.getBt_addr());
    }
}
