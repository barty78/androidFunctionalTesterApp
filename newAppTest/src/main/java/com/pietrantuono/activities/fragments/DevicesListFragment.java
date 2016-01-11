package com.pietrantuono.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.List;

import server.pojos.Device;

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
        List<Device> temp = new Select().from(Device.class).execute();
        ArrayList<Device> devices=new ArrayList<>();
        devices.addAll(temp);
        recyclerView.setAdapter(new RecyclerAdapter(context, devices));
    }

}
