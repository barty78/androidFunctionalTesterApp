package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.IOIO;


/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class BLEWakeTest extends Test implements BluetoothAdapter.LeScanCallback {
    private BluetoothAdapter mBluetoothAdapter;
    private int TIMEOUT = 30 * 1000;
    private ProgressDialog progressDialog;

    /**
     * @param activity
     * @param ioio
     * @param description
     * @param isSensorTest
     * @param isBlockingTest
     * @param limitParam1
     * @param limitParam2
     * @param limitParam3
     */
    protected BLEWakeTest(Activity activity, IOIO ioio, String description, Boolean isSensorTest, Boolean isBlockingTest, float limitParam1, float limitParam2, float limitParam3) {
        super(activity, ioio, description, isSensorTest, isBlockingTest, limitParam1, limitParam2, limitParam3);
    }

    @Override
    public void execute() {
        Executed();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("BLE scan ongoing");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(BLEWakeTest.this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                onTimeout();
            }
        }, TIMEOUT);
    }

    private void onTimeout() {
        if (isSuccess()) return;
        else {
            stopScan();
            activityListener.addFailOrPass(true, false, "", "");
        }

    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "Device found");
        setSuccess(true);
        activityListener.addFailOrPass(true, true, "", "");
        stopScan();

    }

    public void stopScan() {
        try {
            mBluetoothAdapter.stopLeScan(BLEWakeTest.this);
        } catch (Exception ignored) {
        }
        try {
            if(progressDialog!=null && progressDialog.isShowing())progressDialog.dismiss();
        } catch (Exception ignored) {
        }
    }
}
