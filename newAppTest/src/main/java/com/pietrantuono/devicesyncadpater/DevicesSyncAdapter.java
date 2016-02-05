package com.pietrantuono.devicesyncadpater;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.devicesprovider.DevicesContentProvider;
import com.pietrantuono.pericoach.newtestapp.R;

import java.util.List;

import analytica.pericoach.android.Contract;
import hugo.weaving.DebugLog;
import server.RetrofitRestServices;
import server.pojos.Device;
import server.pojos.DevicesList;

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
        Intent intent = new Intent(context.getResources().getString(R.string.devices_sync_started));
        context.sendOrderedBroadcast(intent, context.getString(R.string.PERMISSION));
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
        }
        DevicesList result = RetrofitRestServices.getRest(context).getLastDevicesSync(PeriCoachTestApplication.getDeviceid(), "0");
        insertOrUpdate(result);
        Log.d(TAG, "end onPerformSync");
        intent = new Intent(context.getResources().getString(R.string.devices_sync_finished));
        context.sendOrderedBroadcast(intent, context.getString(R.string.PERMISSION));
    }

    private void insertOrUpdate(DevicesList result) {
        if (result == null || (result.getNew() == null && result.getUpdated() == null)) return;
        if (result.getNew() != null && result.getNew().size() > 0) insert(result.getNew());
        if (result.getUpdated() != null && result.getUpdated().size() > 0)
            insertOrUpdate(result.getUpdated());
    }

    private void insert(List<Device> devices) {
        for (int i = 0; i < devices.size(); i++) {
            long id = deviceAlreadyExists(devices.get(i));
            if (id >= 0) updateDevice(id, devices.get(i));
            else insertDevice(devices.get(i));
        }
    }

    private void insertOrUpdate(List<Device> devices) {
        for (int i = 0; i < devices.size(); i++) {
            long id = deviceAlreadyExists(devices.get(i));
            if (id >= 0) updateDevice(id, devices.get(i));
            else insertDevice(devices.get(i));
        }
    }

    private void insertDevice(Device device) {
        if (device == null) return;
        if (device.getBarcode() == null || TextUtils.isEmpty(device.getBarcode()))
            Log.e(TAG, "ATTENTION, BARCODE OF DOWNLAODED DEVICE IS NULL!");
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(Contract.DevicesColumns.DEVICES_DEVICES_ID, device.getDeviceId());
        contentvalues.put(Contract.DevicesColumns.DEVICES_JOB_ID, device.getJobId());
        contentvalues.put(Contract.DevicesColumns.DEVICES_BARCODE, device.getBarcode() != null ? device.getBarcode() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_SERIAL, device.getSerial() != null ? device.getSerial() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_MODEL, device.getModel() != null ? device.getModel() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_FWVER, device.getFwver() != null ? device.getFwver() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_ADDRESS, device.getBt_addr() != null ? device.getBt_addr() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_EXEC_TESTS, device.getExec_Tests());
        contentvalues.put(Contract.DevicesColumns.DEVICES_STATUS, device.getSerial());
        mContentResolver.insert(DevicesContentProvider.CONTENT_URI, contentvalues);
    }

    private void updateDevice(long id, Device device) {
        if (device == null) return;
        if (device.getBarcode() == null || TextUtils.isEmpty(device.getBarcode()))
            Log.e(TAG, "ATTENTION, BARCODE OF DOWNLAODED DEVICE IS NULL!");
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(Contract.DevicesColumns.DEVICES_DEVICES_ID, device.getDeviceId());
        contentvalues.put(Contract.DevicesColumns.DEVICES_JOB_ID, device.getJobId());
        contentvalues.put(Contract.DevicesColumns.DEVICES_BARCODE, device.getBarcode() != null ? device.getBarcode() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_SERIAL, device.getSerial() != null ? device.getSerial() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_MODEL, device.getModel() != null ? device.getModel() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_FWVER, device.getFwver() != null ? device.getFwver() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_ADDRESS, device.getBt_addr() != null ? device.getBt_addr() : "");
        contentvalues.put(Contract.DevicesColumns.DEVICES_EXEC_TESTS, device.getExec_Tests());
        contentvalues.put(Contract.DevicesColumns.DEVICES_STATUS, device.getSerial());
        String selection = Contract.DevicesColumns._ID + "=?";
        String[] selectionargs = {"" + device.getId()};
        mContentResolver.update(ContentUris.withAppendedId(DevicesContentProvider.CONTENT_URI, id), contentvalues, selection, selectionargs);
    }

    private long deviceAlreadyExists(Device device) {
        long result = -1;
        if (device.getBarcode() == null || TextUtils.isEmpty(device.getBarcode())) return result;
        String selection = Contract.DevicesColumns.DEVICES_BARCODE + "=?";
        String[] selectionArgs = {device.getBarcode()};
        Cursor cursor = mContentResolver.query(DevicesContentProvider.CONTENT_URI, null, selection, selectionArgs, null);
        if (cursor.getCount() <= 0) return result;
        cursor.moveToFirst();
        try {
            result = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.DevicesColumns._ID));
        } catch (Exception e) {
            return result;
        }
        cursor.close();
        return result;
    }

}