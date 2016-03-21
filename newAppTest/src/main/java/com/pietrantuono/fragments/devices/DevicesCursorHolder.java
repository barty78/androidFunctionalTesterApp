package com.pietrantuono.fragments.devices;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.ionicons_typeface_library.Ionicons;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Device;
import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DevicesCursorHolder extends RecyclerView.ViewHolder {
    private TextView barcode;
    private TextView serial;
    private TextView address;
    private Device device;
    private final IconicsImageView barcode_image;
    private final IconicsImageView result;
    private final IconicsImageView bluetooth;


    public DevicesCursorHolder(View itemView) {
        super(itemView);
        barcode_image = (IconicsImageView) itemView.findViewById(R.id.barcode);
        barcode = (TextView) itemView.findViewById(R.id.barcode_tv);
        serial = (TextView) itemView.findViewById(R.id.serial_tv);
        address = (TextView) itemView.findViewById(R.id.address_tv);
        result = (IconicsImageView) itemView.findViewById(R.id.result);
        bluetooth = (IconicsImageView) itemView.findViewById(R.id.bluetooth);

    }

    public void setData(Cursor c) {
        this.device = DevicesContentProvider.reconstructDevice(c);
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
        barcode_image.setIcon(Ionicons.Icon.ion_ios_barcode_outline);
        if (device.getBt_addr() != null) {
            bluetooth.setIcon(Ionicons.Icon.ion_bluetooth);
            bluetooth.setVisibility(View.VISIBLE);
        } else {
            bluetooth.setVisibility(View.INVISIBLE);
        }
        Log.d("HOLDER", "TestType: " + String.valueOf(job.getTesttypeId()) + " | Exec_Tests: " + String.valueOf(device.getExec_Tests()));
        if ((device.getStatus() & job.getTesttypeId()) == job.getTesttypeId()) {
            Log.d("HOLDER", "TestType: " + String.valueOf(job.getTesttypeId()) + " | Status: " + String.valueOf(device.getStatus()));
            result.setIcon(GoogleMaterial.Icon.gmd_check_circle);
            result.setColor(Color.GREEN);
        } else {
            result.setIcon(GoogleMaterial.Icon.gmd_cancel);
            result.setColor(Color.RED);
        }
    }

}
