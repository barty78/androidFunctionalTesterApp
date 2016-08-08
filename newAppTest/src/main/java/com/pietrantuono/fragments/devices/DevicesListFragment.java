package com.pietrantuono.fragments.devices;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kennyc.view.MultiStateView;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import analytica.pericoach.android.Contract;
import server.pojos.Job;

public class DevicesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER = 1;
    private Context context;
    private MultiStateView state;
    private SwipeRefreshLayout swiper;
    private MyRecyclerCursorAdapter mAdapter;
    private Boolean thisJobOnly = true;    //TODO - Make this a configurable option in the UI/App
    private boolean orderbyBarcode = true;
    private CallBack callBack;

    public DevicesListFragment() {
    }

    public static DevicesListFragment newInstance() {
        return new DevicesListFragment();
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
        this.callBack = (CallBack) context;
        callBack.setDevicesListFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        callBack.setDevicesListFragment(null);
        this.callBack = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.devices_list_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);
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
        getLoaderManager().initLoader(LOADER, null, this);
        mAdapter = new MyRecyclerCursorAdapter(getActivity(), null);
        recyclerView.setAdapter(mAdapter);
        return v;
    }


    private void forceSync() {
        swiper.setRefreshing(true);
        IntentFilter filter = new IntentFilter(getString(R.string.devices_sync_finished));
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase(getString(R.string.devices_sync_finished))) {
                    swiper.setRefreshing(false);
                    getActivity().unregisterReceiver(this);
                }
            }
        }, filter);
        PeriCoachTestApplication.getApplication().forceSyncDevices();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            forceSync();
            if (callBack != null) callBack.setDevicesFragmentActionBar(true);
            /*callback = new ActionModecallback(getActivity(), DevicesListFragment.this);
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(callback);
            Job job = PeriCoachTestApplication.getCurrentJob();
            if (PeriCoachTestApplication.getIsRetestAllowed()) {
                mActionMode.setTitle(getActivity().getString(R.string.job_number)+job.getJobno() + " (Retests)");
            } else {
                mActionMode.setTitle(getActivity().getString(R.string.job_number)+job.getJobno() + " (No Retests)");
            }*/
        } else {
            if (callBack != null) callBack.setDevicesFragmentActionBar(false);

            //if (mActionMode != null) mActionMode.finish();
        }
    }

    public void sortByResult() {
        orderbyBarcode = false;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    public void sortByBarcode() {
        orderbyBarcode = true;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    public void currentJobOnly(boolean onlycurrent) {
        thisJobOnly = onlycurrent;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER:
                if(getSelection()==null)return null;
                return new CursorLoader(
                        getActivity(),
                        DevicesContentProvider.CONTENT_URI,
                        null,
                        getSelection(),
                        null,
                        getSortOrder()
                );
            default:
                return null;
        }
    }

    private String getSortOrder() {
        Job job = PeriCoachTestApplication.getCurrentJob();
        Job primaryJob = PeriCoachTestApplication.getPrimaryJob();
        String sortorder;
        if (orderbyBarcode) {
            sortorder = Contract.DevicesColumns.DEVICES_BARCODE + " ASC";
        } else {
            sortorder = "((" + Contract.DevicesColumns.DEVICES_STATUS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId() + ")";
        }
        return sortorder;
    }

    private String getSelection() {
        Job job = PeriCoachTestApplication.getCurrentJob();
        Job primaryJob = PeriCoachTestApplication.getPrimaryJob();
        if (!thisJobOnly || primaryJob == null) return null;
        return Contract.DevicesColumns.DEVICES_JOB_ID + "= " + primaryJob.getId() +
                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & " + job.getTesttypeId() + ") = " + job.getTesttypeId();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() <= 0) state.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        else state.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    public Boolean isThisJobOnly() {
        return thisJobOnly;
    }

    public interface CallBack {

        void setDevicesListFragment(DevicesListFragment devicesListFragment);

        void setDevicesFragmentActionBar(boolean isDevicesListActionbar);
    }

}
