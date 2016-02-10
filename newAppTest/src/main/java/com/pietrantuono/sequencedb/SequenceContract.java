package com.pietrantuono.sequencedb;

import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceContract implements BaseColumns {

    /**
     * Tests table
     */
    public static final String TABLE_TESTS = null;

    public static final String TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD = null;
    public static final String TABLE_TESTS_TEST_ID = null;
    public static final String TABLE_TESTS_VALUE = null;
    public static final String TABLE_TESTS_RESULT = null;
    public static final String TABLE_TESTS_IS_SENSOR_TEST = null;
    public static final String TABLE_TESTS_SEQUENCE_NUMBER = null;
    public static final String TABLE_TESTS_NAME = null;
    public static final String TABLE_TESTS_READING = null;

    /**
     * Records table
     */
    public static final String TABLE_RECORDS = null;

    public static final String RECORDS_BARCODE = null;
    public static final String RECORDS_DURATION = null;
    public static final String RECORDS_FIXTURE_NUMBER = null;
    public static final String RECORDS_FW_VERSION = null;
    public static final String RECORDS_JOB_NUMBER = null;
    public static final String RECORDS_MODEL = null;
    public static final String RECORDS_RESULT = null;
    public static final String RECORDS_SERIAL = null;
    public static final String RECORDS_STARTED_AT = null;
    public static final String RECORDS_BT_MAC = null;

    /**
     * Sensor results table
     */
    public static final String TABLE_SENSOR_RESULTS = null;

    public static final String SENSOR_RESULTS_TEST_ID = null;
    public static final String SENSOR_RESULTS_S0_AVG = null;
    public static final String SENSOR_RESULTS_S0_MAX = null;
    public static final String SENSOR_RESULTS_S0_MIN = null;
    public static final String SENSOR_RESULTS_S0_RESULT = null;
    public static final String SENSOR_RESULTS_S1_AVG = null;
    public static final String SENSOR_RESULTS_S1_MAX = null;
    public static final String SENSOR_RESULTS_S1_MIN = null;
    public static final String SENSOR_RESULTS_S1_RESULT = null;
    public static final String SENSOR_RESULTS_S2_AVG = null;
    public static final String SENSOR_RESULTS_S2_MAX = null;
    public static final String SENSOR_RESULTS_S2_MIN = null;
    public static final String SENSOR_RESULTS_S2_RESULT = null;

    public static final String TABLE_SENSOR_RESULTS_FOREIGN_KEY_ID_OF_TEST = null;
}
