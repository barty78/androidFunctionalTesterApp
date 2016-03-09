package com.pietrantuono.recordsdb;

import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecordsContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ", ";

    public static class TestRecords implements BaseColumns {
        public static final String TABLE = "TestRecord";
        public static final String ID = "Id";

        public static final String BARCODE = "Barcode";
        public static final String DURATION = "Duration";
        public static final String FIXTURE_N = "FixtureNo";
        public static final String FMWVER = "FWVer";
        public static final String JOB_NO = "JobNo";
        public static final String MODEL = "Model";
        public static final String RESULT = "Result";
        public static final String SERIAL = "Serial";
        public static final String STARTED = "StartedAt";
        public static final String BT_ADDR = "BT_Addr";
        public static final String UPLOADED = "uploaded";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BT_ADDR + TEXT_TYPE + COMMA_SEP
                + BARCODE + INTEGER_TYPE + COMMA_SEP
                + DURATION + TEXT_TYPE + COMMA_SEP
                + FMWVER + TEXT_TYPE + COMMA_SEP
                + FIXTURE_N + TEXT_TYPE + COMMA_SEP
                + JOB_NO + INTEGER_TYPE + COMMA_SEP
                + MODEL + INTEGER_TYPE + COMMA_SEP
                //Redings column is not used
                + RESULT + INTEGER_TYPE + COMMA_SEP
                + SERIAL + TEXT_TYPE + COMMA_SEP
                + STARTED + TEXT_TYPE + COMMA_SEP
                + UPLOADED + INTEGER_TYPE
                + ")";
    }

    public class Readings implements BaseColumns {
        public static final String ID = "Id";
        public static final String TABLE = "Readings";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT "
                + ")";
    }

    public class Sensors implements BaseColumns {
        public static final String TABLE = "Sensors";
        public static final String ID = "Id";
        public static final String COL_READINGS = "Readings";
        public static final String S0 = "S0";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_READINGS + INTEGER_TYPE + COMMA_SEP
                + S0 + INTEGER_TYPE
                + ")";
    }

    public class Sensors0 implements BaseColumns {
        public static final String TABLE = "S0";
        public static final String ID = "Id";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ")";
    }

    public class SingleS0 implements BaseColumns {
        public static final String ID = "Id";
        public static final String TABLE = "SingleS0";
        public static final String AVG = "Avg";
        public static final String IDTEST = "IDTest";
        public static final String MIN = "Min";
        public static final String MAX = "Max";
        public static final String ERROR_CODE = "ErrorCode";
        public static final String RESULT = "Result";
        public static final String S0 = "S0";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AVG + INTEGER_TYPE + COMMA_SEP
                + IDTEST + INTEGER_TYPE + COMMA_SEP
                + MIN + INTEGER_TYPE + COMMA_SEP
                + MAX + INTEGER_TYPE + COMMA_SEP
                + ERROR_CODE + INTEGER_TYPE + COMMA_SEP
                + RESULT + INTEGER_TYPE + COMMA_SEP
                + S0 + INTEGER_TYPE + COMMA_SEP
                + ")";
    }

    public class SingleS1 implements BaseColumns {
        public static final String ID = "Id";
        public static final String TABLE = "SingleS1";
        public static final String AVG = "Avg";
        public static final String IDTEST = "IDTest";
        public static final String MIN = "Min";
        public static final String MAX = "Max";
        public static final String ERROR_CODE = "ErrorCode";
        public static final String RESULT = "Result";
        public static final String S1 = "S1";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AVG + INTEGER_TYPE + COMMA_SEP
                + IDTEST + INTEGER_TYPE + COMMA_SEP
                + MIN + INTEGER_TYPE + COMMA_SEP
                + MAX + INTEGER_TYPE + COMMA_SEP
                + ERROR_CODE + INTEGER_TYPE + COMMA_SEP
                + RESULT + INTEGER_TYPE + COMMA_SEP
                + S1 + INTEGER_TYPE + COMMA_SEP
                + ")";
    }

    public class SingleS2 {
        public static final String ID = "Id";
        public static final String TABLE = "SingleS2";
        public static final String AVG = "Avg";
        public static final String IDTEST = "IDTest";
        public static final String MIN = "Min";
        public static final String MAX = "Max";
        public static final String ERROR_CODE = "ErrorCode";
        public static final String RESULT = "Result";
        public static final String S2 = "S2";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AVG + INTEGER_TYPE + COMMA_SEP
                + IDTEST + INTEGER_TYPE + COMMA_SEP
                + MIN + INTEGER_TYPE + COMMA_SEP
                + MAX + INTEGER_TYPE + COMMA_SEP
                + ERROR_CODE + INTEGER_TYPE + COMMA_SEP
                + RESULT + INTEGER_TYPE + COMMA_SEP
                + S2 + INTEGER_TYPE + COMMA_SEP
                + ")";
    }

    public class SingleTest implements BaseColumns {
        public static final String TABLE = "SingleTest";
        public static final String ID = "Id";
        public static final String RESULT = "Result";
        public static final String ERRORCODE = "ErrorCode";
        public static final String IDTEST = "IDTest";
        public static final String VALUE = "Value";
        public static final String TEST = "TEST";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ("
                + ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RESULT + INTEGER_TYPE + COMMA_SEP
                + ERRORCODE + INTEGER_TYPE + COMMA_SEP
                + IDTEST + INTEGER_TYPE + COMMA_SEP
                + VALUE + REAL_TYPE + COMMA_SEP
                + TEST + INTEGER_TYPE + COMMA_SEP
                + ")";
    }
}
