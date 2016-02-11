package com.pietrantuono.sequencedb;

import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class SequenceContracts {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    public static class Records implements BaseColumns {
        /**
         * Records table
         */
        public static final String TABLE_RECORDS = "records_table";

        public static final String RECORDS_BARCODE = "barcode";
        public static final String RECORDS_DURATION = "duration";
        public static final String RECORDS_FIXTURE_NUMBER = "fixture number";
        public static final String RECORDS_FW_VERSION = "fw_version";
        public static final String RECORDS_JOB_NUMBER = "job_number";
        public static final String RECORDS_MODEL = "model";
        public static final String RECORDS_RESULT = "result";
        public static final String RECORDS_SERIAL = "serial";
        public static final String RECORDS_STARTED_AT = "started_at";
        public static final String RECORDS_BT_MAC = "bt_mac";

        public static String CREATE_TABLES = "CREATE TABLE " + Records.TABLE_RECORDS + " (" +
                Records._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Records.RECORDS_BARCODE + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_DURATION + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_FIXTURE_NUMBER + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_FW_VERSION + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_JOB_NUMBER + INTEGER_TYPE + COMMA_SEP +
                Records.RECORDS_MODEL + INTEGER_TYPE + COMMA_SEP +
                Records.RECORDS_RESULT + INTEGER_TYPE + COMMA_SEP +
                Records.RECORDS_SERIAL + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_STARTED_AT + TEXT_TYPE + COMMA_SEP +
                Records.RECORDS_BT_MAC + TEXT_TYPE +
                " )";

    }

    public static class Tests implements BaseColumns {
        /**
         * Tests table
         */
        public static final String TABLE_TESTS = "table_tests";
        public static final String TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD = "tests_foreign_key_id_of_record";

        public static final String TABLE_TESTS_TEST_ID = "test_id";
        public static final String TABLE_TESTS_VALUE = "tests_value";
        public static final String TABLE_TESTS_RESULT = "result";
        public static final String TABLE_TESTS_IS_NORMAL_TEST = "is__test";
        public static final String TABLE_TESTS_IS_SENSOR_TEST = "is_sensor_test";
        public static final String TABLE_TESTS_IS_UPLOAD_TEST = "is_upload_test";
        public static final String TABLE_TESTS_NAME = "test_name";
        public static final String TABLE_TESTS_READING = "reading";
        public static final String TABLE_TESTS_OTHER_READING = "other_reading";
        public static final String TABLE_TESTS_TIME_INSERTED = "time_inserted";

        public static final String TABLE_TESTS_S0_AVG = "sensor_results_s0_avg";
        public static final String TABLE_TESTS_S0_MAX = "sensor_results_s0_max";
        public static final String TABLE_TESTS_S0_MIN = "sensor_results_s0_min";
        public static final String TABLE_TESTS_S0_RESULT = "sensor_results_s0_result";
        public static final String TABLE_TESTS_S1_AVG = "sensor_results_s1_avg";
        public static final String TABLE_TESTS_S1_MAX = "sensor_results_s1_max";
        public static final String TABLE_TESTS_S1_MIN = "sensor_results_s1_min";
        public static final String TABLE_TESTS_S1_RESULT = "sensor_results_s1_result";
        public static final String TABLE_TESTS_S2_AVG = "sensor_results_s2_avg ";
        public static final String TABLE_TESTS_S2_MAX = "sensor_results_s2_max";
        public static final String TABLE_TESTS_S2_MIN = "sensor_results_s2_min";
        public static final String TABLE_TESTS_S2_RESULT = "sensor_results_s2_result";

        public static String CREATE_TABLES = "CREATE TABLE " + Tests.TABLE_TESTS + " (" +
                Tests._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_TEST_ID + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_TIME_INSERTED+ INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_VALUE + REAL_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_RESULT + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_IS_NORMAL_TEST + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_IS_SENSOR_TEST + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_IS_UPLOAD_TEST + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_NAME + TEXT_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_READING + TEXT_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_OTHER_READING + TEXT_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S0_AVG + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S0_MAX + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S0_MIN + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S0_RESULT + INTEGER_TYPE + COMMA_SEP +

                Tests.TABLE_TESTS_S1_AVG + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S1_MAX + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S1_MIN + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S1_RESULT + INTEGER_TYPE + COMMA_SEP +

                Tests.TABLE_TESTS_S2_AVG + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S2_MAX + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S2_MIN + INTEGER_TYPE + COMMA_SEP +
                Tests.TABLE_TESTS_S2_RESULT + INTEGER_TYPE + COMMA_SEP +
                " FOREIGN KEY ("+Tests.TABLE_TESTS_FOREIGN_KEY_ID_OF_RECORD+") REFERENCES "+Records.TABLE_RECORDS+"("+Records._ID+")"+
                " )";
    }
}
