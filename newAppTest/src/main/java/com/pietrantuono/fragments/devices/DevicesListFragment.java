package com.pietrantuono.fragments.devices;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.query.Select;
import com.kennyc.view.MultiStateView;
import com.pietrantuono.fragments.ActionModecallback;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import server.pojos.Device;
import server.pojos.Job;

public class DevicesListFragment extends Fragment implements ActionModecallback.Callback {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private Context context;
    private MultiStateView state;
    private SwipeRefreshLayout swiper;
    private ActionMode mActionMode;
    private ActionModecallback callback;
    private DevicesListAdapter adapter;

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
                forceSync();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        state.setViewState(MultiStateView.VIEW_STATE_LOADING);
        populateList();

        return v;


    }

    private void populateList() {
        swiper.setRefreshing(false);
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
        if (devices.size() <= 0) state.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        else state.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        adapter = new DevicesListAdapter(context, devices, job);
        recyclerView.setAdapter(adapter);
    }

    private void forceSync() {
        swiper.setRefreshing(true);
        IntentFilter filter = new IntentFilter(getString(R.string.devices_sync_finished));
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(getString(R.string.devices_sync_finished))) {
                    populateList();
                    getActivity().unregisterReceiver(this);
                }

            }
        }, filter);
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(createAccount(), getResources().getString(R.string.devices_sync_provider_authority), settingsBundle);
    }


    private Account createAccount() {
        Account newAccount = new Account(
                getActivity().getResources().getString(R.string.devices_sync_account), getActivity().getResources().getString(R.string.devices_sync_account_type));
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {/*TODO*/} else {/*TODO*/}
        return newAccount;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            forceSync();
            callback = new ActionModecallback(getActivity(), DevicesListFragment.this);
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(callback);
        } else {
            if (mActionMode != null) mActionMode.finish();

        }
    }

    @Override
    public void sortByResult() {
        adapter.sortByResult();
    }

    @Override
    public void sortByBarcode() {
        adapter.sortByBarcode();
    }


}
