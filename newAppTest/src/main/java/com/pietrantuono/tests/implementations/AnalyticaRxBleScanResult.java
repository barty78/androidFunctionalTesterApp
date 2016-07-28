package com.pietrantuono.tests.implementations;

import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class AnalyticaRxBleScanResult  {
    private RxBleScanResult result;
    private long timeStamp;

    public AnalyticaRxBleScanResult(RxBleScanResult result, long timeStamp) {
        this.result = result;
        this.timeStamp = timeStamp;
    }

    public RxBleDevice getBleDevice() {
        return result.getBleDevice();
    }

    public int getRssi() {
        return result.getRssi();
    }

    public byte[] getScanRecord() {
    return result.getScanRecord();
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
