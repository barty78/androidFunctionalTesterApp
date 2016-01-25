package com.pietrantuono.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class JobHolder extends RecyclerView.ViewHolder{
    private final Context context;
    private final Callback callback;
    private final IconicsImageView image;
    private final TextView description;
    private final TextView jobnumber;
    private final TextView quantity;

    public JobHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        image= (IconicsImageView) itemView.findViewById(R.id.image);
        description= (TextView) itemView.findViewById(R.id.description);
        jobnumber= (TextView) itemView.findViewById(R.id.jobnumber);
        quantity= (TextView) itemView.findViewById(R.id.quantity);
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

    }

    interface Callback {

        void setJob(Job job);

        void getFirmwareListFromServer(long firmwareId);
    }

}
