package com.pietrantuono.fragments.devices;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.activeandroid.query.Select;
import com.kennyc.view.MultiStateView;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.fragments.ActionModecallback;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import analytica.pericoach.android.Contract;
import server.pojos.Device;
import server.pojos.Job;

public class DevicesListFragment extends Fragment implements ActionModecallback.Callback, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER = 1;
    private final String TAG = getClass().getSimpleName();
    private RecyclerView recyclerView;
    private Context context;
    private MultiStateView state;
    private SwipeRefreshLayout swiper;
    private ActionMode mActionMode;
    private ActionModecallback callback;
    private MyRecyclerCursorAdapter mAdapter;
    private Boolean thisJobOnly = true;    //TODO - Make this a configurable option in the UI/App
    private boolean orderbyBarcode=true;

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
        getLoaderManager().initLoader(LOADER, null, this);
        mAdapter= new MyRecyclerCursorAdapter(getActivity(),null);
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
        orderbyBarcode=false;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    @Override
    public void sortByBarcode() {
        orderbyBarcode=true;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    @Override
    public void currentJobOnly(boolean onlycurrent) {
        thisJobOnly=onlycurrent;
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER:
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
        String sortorder;
        if(orderbyBarcode){
            sortorder= Contract.DevicesColumns.DEVICES_BARCODE + " ASC";
        }
        else {
            sortorder="(("+ Contract.DevicesColumns.DEVICES_STATUS+ " & "+job.getTesttypeId()+") = "+job.getTesttypeId()+")";
        }
        return sortorder;
    }

    private String getSelection() {
        if (!thisJobOnly) return null;
        Job job = PeriCoachTestApplication.getCurrentJob();
        String selection =Contract.DevicesColumns.DEVICES_JOB_ID + "= "+job.getId()+
                " AND " + "(" + Contract.DevicesColumns.DEVICES_EXEC_TESTS + " & "+job.getTesttypeId()+") = "+job.getTesttypeId();
        return selection;
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

}
