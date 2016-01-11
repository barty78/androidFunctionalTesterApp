package com.pietrantuono.activities.fragments;

/**
 * Created by mauriziopietrantuono on 08/01/16.
 */
public interface SerialConsoleFragmentCallback {
    void updateUI(String text);
    void clearSerialConsole();
    void setCallback(SerialConsoleFragmentCallback callback);
    void removeCallback();
}
