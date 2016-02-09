package com.pietrantuono.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.pericoach.newtestapp.R;

import analytica.pericoach.android.Contract;
import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class JobHolder extends RecyclerView.ViewHolder{
    private final Callback callback;
    private final IconicsImageView image;
    private final TextView description;
    private final TextView jobnumber;
    private final TextView quantity;
    private final TextView num_of_devices;
    private final TextView devices_passed;
    private final TextView devices_failed;
    private final DonutProgress progress_stats;

    public JobHolder(View itemView, Context context) {
        super(itemView);
        image= (IconicsImageView) itemView.findViewById(R.id.image);
        description= (TextView) itemView.findViewById(R.id.description);
        jobnumber= (TextView) itemView.findViewById(R.id.jobnumber);
        quantity= (TextView) itemView.findViewById(R.id.quantity);
        num_of_devices=(TextView)itemView.findViewById(R.id.num_of_devices);
        devices_passed=(TextView)itemView.findViewById(R.id.devices_passed);
        devices_failed=(TextView)itemView.findViewById(R.id.devices_failed);
        progress_stats=(DonutProgress)itemView.findViewById(R.id.progress_stats);
        callback=(Callback)context;

    }

    public void setData(final Job job){
        description.setText(job.getDescription() != null ? job.getDescription() : "");

        if(job.getIslogging()==1)image.setIcon(GoogleMaterial.Icon.gmd_save);
        else image.setVisibility(View.INVISIBLE);
         String jobno=job.getJobno();
        if(jobno!=null){
            jobnumber.setText(jobno);
        }
        else {jobnumber.setText("EMPTY");}
        String q=""+job.getQuantity();
        if(q!=null){
            quantity.setText(q);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.setJob(job);
                Log.d("Job#:", String.valueOf(job.getJobno()));
                Log.d("Test ID:", String.valueOf(job.getTestId()));
                Log.d("Firmware ID:", String.valueOf(job.getFirmwareId()));
                Log.d("Logging", String.valueOf(job.getIslogging() != 0));
                Log.d("Job ID: ", String.valueOf(job.getId()));
                PeriCoachTestApplication.setCurrentJob(job);
                PeriCoachTestApplication.setIsRetestAllowed(job.getIsretestallowed() != 0);
                callback.getFirmwareListFromServer(job.getFirmwareId());
            }

        });
        updateStats(job);
    }

    public void updateStats(Job job) {
        ContentResolver resolver = itemView.getContext().getContentResolver();

        String selection = Contract.DevicesColumns.DEVICES_JOB_ID + "= "+job.getId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & "+job.getTesttypeId()+") = "+job.getTesttypeId();
        Cursor c=resolver.query(DevicesContentProvider.CONTENT_URI,null,selection,null,null);
        int numberOfDevices=c.getCount();
        c.close();

        selection =Contract.DevicesColumns.DEVICES_JOB_ID + "= "+job.getId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & "+job.getTesttypeId()+") = "+job.getTesttypeId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_STATUS + " & "+job.getTesttypeId()+") = "+job.getTesttypeId();
        c=resolver.query(DevicesContentProvider.CONTENT_URI,null,selection,null,null);
        int numberOfDevicesPassed=c.getCount();
        c.close();

        selection =Contract.DevicesColumns.DEVICES_JOB_ID + "= "+job.getId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & "+job.getTesttypeId()+") = "+job.getTesttypeId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_STATUS + " & "+job.getTesttypeId()+") = 0";
        c=resolver.query(DevicesContentProvider.CONTENT_URI,null,selection,null,null);

        int numberOfDevicesFailed=c.getCount();
        c.close();

        num_of_devices.setText("" + numberOfDevices);
        devices_passed.setText(""+numberOfDevicesPassed);
        devices_failed.setText("" + numberOfDevicesFailed);
        float numberOfDevicesFloat=numberOfDevices;
        progress_stats.setProgress((int)((numberOfDevicesPassed / numberOfDevicesFloat)*100));
    }


    interface Callback {

        void setJob(Job job);

        void getFirmwareListFromServer(long firmwareId);
    }

}
