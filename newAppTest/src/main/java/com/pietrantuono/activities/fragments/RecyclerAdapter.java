package com.pietrantuono.activities.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

import server.pojos.Device;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<Holder> {
    private Context context;
    private ArrayList<Device> devices;

    public RecyclerAdapter(Context context, ArrayList<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(context).inflate(R.layout.devices_row,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setData(devices.get(position),context);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
