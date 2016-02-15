package com.pietrantuono.tests;

/**
 * Created by Peter on 15/02/2016.
 */
public interface ErrorCodes {

    // Sensor Test Errors
    public static final int SENSORTEST_INSUFFICIENT_SAMPLES = 1;
    public static final int SENSORTEST_VOLTAGE_SETTING_FAILED = 2;
    public static final int SENSORTEST_ACTIVITY_ERROR = 3;
    public static final int SENSORTEST_NO_DRIVE_VOLTAGE_SET = 4;
    public static final int SENSORTEST_NO_ZERO_VOLTAGE_SET = 5;
    public static final int SENSORTEST_NO_LOAD_SET = 6;

    // Firmware Upload Errors
    public static final int FIRMWAREUPLOAD_INIT_FAILED = 7;
    public static final int FIRMWAREUPLOAD_GET_INFO_FAILED = 8;
    public static final int FIRMWAREUPLOAD_FILESIZE_ERROR = 9;
    public static final int FIRMWAREUPLOAD_WRITE_MEMORY_ERROR = 10;
    public static final int FIRMWAREUPLOAD_DEVICE_INFO_ERROR = 11;
    public static final int FIRMWAREUPLOAD_NO_ACK_ERROR = 12;
    public static final int FIRMWAREUPLOAD_CMD_SEND_ERROR = 13;
    public static final int FIRMWAREUPLOAD_ADDR_SEND_ERROR = 14;
    public static final int FIRMWAREUPLOAD_ADDR_ALIGN_ERROR = 15;

}
