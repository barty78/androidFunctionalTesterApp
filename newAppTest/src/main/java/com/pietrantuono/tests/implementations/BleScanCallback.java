package com.pietrantuono.tests.implementations;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.util.Log;

import com.pietrantuono.timelogger.TimeLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleScanCallback extends ScanCallback {

    private static final String TAG = "BleScanCallback";
    private Set<ScanResult> mResults = new LinkedHashSet<ScanResult>();
    private List<ScanResult> mBatchScanResults = new ArrayList<ScanResult>();

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        boolean add = true;
        if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
            mResults.add(result);
            TimeLogger.log("Device Added - " + result.getDevice().getAddress());
            Log.d(TAG, result.toString());
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        Log.d(TAG, "Batch " + results.toString());
        mBatchScanResults = results;
    }

    // Clear regular and batch scan results.
    synchronized public void clear() {
        mResults.clear();
        mBatchScanResults.clear();
    }

    // Return regular BLE scan results accumulated so far.
    synchronized Collection<ScanResult> getScanResults() {
        return Collections.unmodifiableCollection(mResults);
    }

    // Return batch scan results.
    synchronized List<ScanResult> getBatchScanResults() {
        return Collections.unmodifiableList(mBatchScanResults);
    }
}
