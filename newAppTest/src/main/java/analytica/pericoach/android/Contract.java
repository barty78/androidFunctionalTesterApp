package analytica.pericoach.android;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@SuppressWarnings("unused")
public class Contract {

    static final String SEPARATOR = ",";

    /**
     * Jobs Table
     */
    static final String JOBS_TABLE_NAME = "jobs";

    static final String JOBS_JOB_ID_COLUMN = "job_id";
    static final String JOBS_JOB_NUMBER_COLUMN = "jobNo";
    static final String JOBS_TESTID_COLUMN = "test_id";
    static final String JOBS_TESTTYPE_ID_COLUMN = "testtype_id";
    static final String JOBS_TOTALQUANTITY_COLUMN = "totalqty";
    static final String JOBS_TESTEDQUANTITY_COLUMN = "testedqty";
    static final String JOBS_PASSEDQUANTITY_COLUMN = "passedqty";
    static final String JOBS_CREATED_COLUMN = "created";
    static final String JOBS_LAST_REPORTED_RECORD_COLUMN = "LastReportedRecord";
    static final String JOBS_REPORT_NUMBER_COLUMN = "ReportNumber";
    static final String JOBS_ACTIVE_COLUMN = "active";
    static final String JOBS_STAGE_DEP = "stage_dep";
    static final String JOBS_LAST_UPDATED = "LastUpdated";
    ;


    /**
     * Devices Table
     */
    public static class DevicesColumns implements BaseColumns {

        public static final String DEVICES_TABLE_NAME = "devices_table";
        public static final String DEVICES_DEVICES_ID = "device_id";
        public static final String DEVICES_JOB_ID = "job_id";
        public static final String DEVICES_BARCODE = "barcode";
        public static final String DEVICES_SERIAL = "serial";
        public static final String DEVICES_MODEL = "model";
        public static final String DEVICES_FWVER = "fwver";
        public static final String DEVICES_ADDRESS = "address";
        public static final String DEVICES_EXEC_TESTS = "executed_tests";
        public static final String DEVICES_STATUS    = "status";

        public static final String TEXT_TYPE = " TEXT";
        public static final String COMMA_SEP = ",";
        public static final String INTEGER_TYPE = " INTEGER";

        static final String CREATE_DEVICES_TABLE = "CREATE TABLE " + DevicesColumns.DEVICES_TABLE_NAME +
                " (" +
                DevicesColumns._ID + " INTEGER PRIMARY KEY," +
                DevicesColumns.DEVICES_DEVICES_ID + INTEGER_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_JOB_ID + INTEGER_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_BARCODE + TEXT_TYPE + " unique ON CONFLICT REPLACE"+ COMMA_SEP +
                DevicesColumns.DEVICES_SERIAL + TEXT_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_MODEL + TEXT_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_FWVER + TEXT_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_ADDRESS + TEXT_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_EXEC_TESTS + INTEGER_TYPE + COMMA_SEP +
                DevicesColumns.DEVICES_STATUS + INTEGER_TYPE +
                " )";
    }
    public static final String DEFAULT_SORT_ORDER = DevicesColumns.DEVICES_DEVICES_ID + " DESC";


}
