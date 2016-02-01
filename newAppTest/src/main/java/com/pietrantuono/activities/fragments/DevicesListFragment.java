package com.pietrantuono.activities.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.kennyc.view.MultiStateView;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.RetrofitRestServices;
import server.pojos.Device;
import server.pojos.DevicesList;
import server.pojos.Job;
import server.service.ServiceDBHelper;

public class DevicesListFragment extends Fragment {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private Context context;
    private MultiStateView state;
    private SwipeRefreshLayout swiper;
    private ActionMode mActionMode;
    private ActionModecallback callback;

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
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.devices_list_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        state = (MultiStateView) v.findViewById(R.id.state);
        swiper = (SwipeRefreshLayout) v.findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiper.setRefreshing(true);
                downloadDevicesList();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        state.setViewState(MultiStateView.VIEW_STATE_LOADING);
        populateList();

        return v;


    }

    private void populateList() {
        Boolean thisJobOnly = true;    //TODO - Make this a configurable option in the UI/App
        Job job = PeriCoachTestApplication.getCurrentJob();
        List<Device> temp = new Select().from(Device.class).execute();
        ArrayList<Device> devices = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            Log.d("DevListFrag", String.valueOf(temp.get(i).getJobId()) + " | " + String.valueOf(job.getId()));
            // Check if we only want devices from the current job, if so check job ids match.
            if ((thisJobOnly && (temp.get(i).getJobId() == job.getId()))) {
                // Only add devices which have actually executed the current test type
                if ((temp.get(i).getExec_Tests() & job.getTesttypeId()) == job.getTesttypeId()) {
                    devices.add(temp.get(i));
                }
            } else {
                // We want all devices from all jobs
                devices.add(temp.get(i));
            }
        }
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device lhs, Device rhs) {
                return (int) (Long.parseLong(lhs.getBarcode()) - Long.parseLong(rhs.getBarcode()));
            }
        });
        if(devices.size()<=0)state.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        else state.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        recyclerView.setAdapter(new DevicesListAdapter(context, devices, job));
    }

    private void downloadDevicesList() {
        if(swiper!=null)swiper.setRefreshing(true);
        //state.setViewState(MultiStateView.VIEW_STATE_LOADING);
        RetrofitRestServices.getRest(getActivity()).getLastDevices(PeriCoachTestApplication.getDeviceid(), PeriCoachTestApplication.getLastId(), new Callback<DevicesList>() {
            @Override
            public void success(DevicesList arg0, Response arg1) {
                if (swiper != null) swiper.setRefreshing(false);
                if (arg0 != null) ServiceDBHelper.addDevices(arg0);
                else Snackbar.make(state, "Failed devices download", Snackbar.LENGTH_LONG).show();
                populateList();
            }

            @Override
            public void failure(RetrofitError arg0) {
                if (swiper != null) swiper.setRefreshing(false);
                Snackbar.make(state, "Failed devices download", Snackbar.LENGTH_LONG).show();
                populateList();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if(isVisibleToUser){
            downloadDevicesList();
            callback = new ActionModecallback(getActivity(),null);
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(callback);
        }
        else {
            if(mActionMode!=null)mActionMode.finish();

        }
    }

}
