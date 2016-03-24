package com.pietrantuono.tests.implementations;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.pietrantuono.tests.superclass.Test;

import ioio.lib.api.IOIO;


/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class BLEWakeTest extends Test implements BluetoothAdapter.LeScanCallback {
    private BluetoothAdapter mBluetoothAdapter;

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
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(BLEWakeTest.this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "Device found");
        try {
            mBluetoothAdapter.stopLeScan(BLEWakeTest.this);
        } catch (Exception ignored) {
        }
    }
}
