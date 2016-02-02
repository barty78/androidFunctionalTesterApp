package com.pietrantuono.devicessync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import hugo.weaving.DebugLog;

public class DevicesSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG =getClass().getSimpleName();
    // Global variables
    // Define a variable to contain a content resolver instance 
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    public DevicesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /* 
         * If your app uses a content resolver, get an instance of it 
         * from the incoming Context 
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public DevicesSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /* 
         * If your app uses a content resolver, get an instance of it 
         * from the incoming Context 
         */
        mContentResolver = context.getContentResolver();

    }
    @DebugLog
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "start onPerformSync");
        try { Thread.sleep(5*1000);
        } catch (InterruptedException e) {}
        Log.d(TAG, "end onPerformSync");
    }

}