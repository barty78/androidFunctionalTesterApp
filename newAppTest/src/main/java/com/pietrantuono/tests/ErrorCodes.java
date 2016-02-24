package com.pietrantuono.tests;

/**
 * Created by Peter on 15/02/2016.
 */
public interface ErrorCodes {

    public static final int NO_ERROR = -1;

    public static final int OUTSIDE_LIMITS = 1;

    // Sensor Test Errors
    public static final int SENSORTEST_INSUFFICIENT_SAMPLES = 2;
    public static final int SENSORTEST_VOLTAGE_SETTING_FAILED = 3;
    public static final int SENSORTEST_ACTIVITY_ERROR = 4;
    public static final int SENSORTEST_NO_DRIVE_VOLTAGE_SET = 5;
    public static final int SENSORTEST_NO_ZERO_VOLTAGE_SET = 6;
    public static final int SENSORTEST_NO_LOAD_SET = 7;

    // Firmware Upload Errors
    public static final int FIRMWAREUPLOAD_INIT_FAILED = 8;
    public static final int FIRMWAREUPLOAD_GET_INFO_FAILED = 9;
    public static final int FIRMWAREUPLOAD_FILESIZE_ERROR = 10;
    public static final int FIRMWAREUPLOAD_WRITE_MEMORY_ERROR = 11;
    public static final int FIRMWAREUPLOAD_DEVICE_INFO_ERROR = 12;
    public static final int FIRMWAREUPLOAD_NO_ACK_ERROR = 13;
    public static final int FIRMWAREUPLOAD_CMD_SEND_ERROR = 14;
    public static final int FIRMWAREUPLOAD_ADDR_SEND_ERROR = 15;
    public static final int FIRMWAREUPLOAD_ADDR_ALIGN_ERROR = 16;
    public static final int FIRMWAREUPLOAD_READ_PROTECT_ERROR = 17;
    public static final int FIRMWAREUPLOAD_ERASE_ERROR = 18;
    public static final int FIRMWAREUPLOAD_OPTIONBYTES_WRITE_ERROR = 19;
    public static final int FIRMWAREUPLOAD_CMD_GET_SEND_ERROR = 20;
    public static final int FIRMWAREUPLOAD_CMD_GVR_SEND_ERROR = 21;
    public static final int FIRMWAREUPLOAD_CMD_GID_SEND_ERROR = 22;
    public static final int FIRMWAREUPLOAD_CMD_RM_SEND_ERROR = 23;
    public static final int FIRMWAREUPLOAD_CMD_GO_SEND_ERROR = 24;
    public static final int FIRMWAREUPLOAD_CMD_WM_SEND_ERROR = 25;
    public static final int FIRMWAREUPLOAD_CMD_ER_SEND_ERROR = 26;
    public static final int FIRMWAREUPLOAD_CMD_WP_SEND_ERROR = 27;
    public static final int FIRMWAREUPLOAD_CMD_UW_SEND_ERROR = 28;
    public static final int FIRMWAREUPLOAD_CMD_RP_SEND_ERROR = 29;
    public static final int FIRMWAREUPLOAD_CMD_UR_SEND_ERROR = 30;

    public static final int SENSOR_STEP_VOLTAGE_SET_ERROR = 31;

    public static final int GENERIC_FAILURE = 100;

    public static final int FIRMWAREUPLOAD_GENERIC_FAILURE =999;

}
