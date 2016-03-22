package com.pietrantuono.activities;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;

import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
class JobAdapter extends RecyclerView.Adapter<JobHolder> {
    private ArrayList<Job> list;
    private Context context;


    public JobAdapter(ArrayList<Job> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.job_row, parent,false);
        return new JobHolder(v,context);
    }

    @Override
    public void onBindViewHolder(JobHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
