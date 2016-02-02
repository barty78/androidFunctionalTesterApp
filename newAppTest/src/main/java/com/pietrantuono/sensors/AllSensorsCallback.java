package com.pietrantuono.sensors;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public interface AllSensorsCallback {

    void onAllVoltageResponseReceived();

    void onError();
}
