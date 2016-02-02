package com.pietrantuono.activities.fragments.devices;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import server.pojos.Device;
import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class DevicesListAdapter extends RecyclerView.Adapter<DevicesHolder> {
    private Context context;
    private ArrayList<Device> devices;
    private Job job;

    public DevicesListAdapter(Context context, ArrayList<Device> devices, Job job) {
        this.context = context;
        this.devices = devices;
        this.job = job;
        setHasStableIds(true);
    }

    @Override
    public DevicesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.new_device_row, parent, false);
        return new DevicesHolder(v);
    }

    @Override
    public void onBindViewHolder(DevicesHolder holder, int position) {
        holder.setData(devices.get(position), context);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public long getItemId(int position) {
        return devices.hashCode();
    }

    public void sortByBarcode() {
        Comparator<Device> barcodeComparator = new Comparator<Device>() {
            @Override
            public int compare(Device lhs, Device rhs) {
                if (lhs.getBarcode() == null || rhs.getBarcode() == null) return 0;
                return (lhs.getBarcode().compareTo(rhs.getBarcode()));
            }
        };
        Collections.sort(devices, barcodeComparator);
        notifyDataSetChanged();
    }

    public void sortByResult() {
        Comparator<Device> resultComparator = new Comparator<Device>() {
            @Override
            public int compare(Device lhs, Device rhs) {
                if (isSuccess(lhs) && isSuccess(rhs)) return 0;
                if (!isSuccess(lhs) && !isSuccess(rhs)) return 0;
                if (isSuccess(lhs) && !isSuccess(rhs)) return 1;
                if (!isSuccess(lhs) && isSuccess(rhs)) return -1;
                return 0;
            }
        };
        Collections.sort(devices, resultComparator);
        notifyDataSetChanged();
    }

    public boolean isSuccess(Device device) {
        if ((device.getStatus() & job.getTesttypeId()) == job.getTesttypeId()) {
            Log.d("HOLDER", "TestType: " + String.valueOf(job.getTesttypeId()) + " | Status: " + String.valueOf(device.getStatus()));
            return true;
        }
        return false;
    }
}

