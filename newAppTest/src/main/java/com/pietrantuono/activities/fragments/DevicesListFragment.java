package com.pietrantuono.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import analytica.pericoach.android.DBManager;
import server.pojos.Device;
import server.pojos.Job;

public class DevicesListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;

    public DevicesListFragment() {
    }
    public static DevicesListFragment newInstance() {
        DevicesListFragment fragment = new DevicesListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onAttach(Context context) {
       super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.devices_list_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateList();
            }
        });
        populateList();
        return v;
    }

    private void populateList() {

        Boolean thisJobOnly = true;    //TODO - Make this a configurable option in the UI/App
        Job job = PeriCoachTestApplication.getCurrentJob();
        List<Device> temp = new Select().from(Device.class).execute();
        ArrayList<Device> devices=new ArrayList<>();
        for(int i =0; i < temp.size();i++){
            Log.d("DevListFrag", String.valueOf(temp.get(i).getJobId()) + " | " + String.valueOf(job.getId()));
            // Check if we only want devices from the current job, if so check job ids match.
            if(!thisJobOnly || (thisJobOnly && (temp.get(i).getJobId() == job.getId()))) {
                // Only add devices which have actually executed the current test type
                if ((temp.get(i).getExec_Tests() & job.getTesttypeId()) == job.getTesttypeId()) {
                    devices.add(temp.get(i));
                }
            }
        }
//        devices.addAll(temp);
        recyclerView.setAdapter(new RecyclerAdapter(context, devices));
    }

}
