package com.pietrantuono.devicesyncadpater;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.pietrantuono.pericoach.newtestapp.R;

import hugo.weaving.DebugLog;

public class DevicesSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String TAG = getClass().getSimpleName();
    private final Context context;
    ContentResolver mContentResolver;

    public DevicesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        this.context = context;
    }


    public DevicesSyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;
        mContentResolver = context.getContentResolver();

    }

    @DebugLog
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "start onPerformSync");
        Intent intent= new Intent(context.getResources().getString(R.string.devices_sync_started));
        context.sendOrderedBroadcast(intent,context.getString(R.string.PERMISSION));
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
        }
        Log.d(TAG, "end onPerformSync");
        intent= new Intent(context.getResources().getString(R.string.devices_sync_finished));
        context.sendOrderedBroadcast(intent, context.getString(R.string.PERMISSION));
    }

}