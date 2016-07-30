package com.pietrantuono.tests.implementations;

import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalyticaRxBleScanResult that = (AnalyticaRxBleScanResult) o;

        if (timeStamp != that.timeStamp) return false;
        if (getRssi() != that.getRssi()) return false;
        if (!Arrays.equals(getScanRecord(), that.getScanRecord())) return false;
        return getBleDevice() != null ? getBleDevice().equals(that.getBleDevice()) : that.getBleDevice() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + getRssi();
        result = 31 * result + Arrays.hashCode(getScanRecord());
        result = 31 * result + (getBleDevice() != null ? getBleDevice().hashCode() : 0);
        return result;
    }
}
