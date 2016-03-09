package com.pietrantuono.recordsdb;

import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecordsContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    public static class TestRecords implements BaseColumns {
        public static final String TABLE = "TestRecord";
        public static final String ID = "Id";

        public static final String BARCODE="Barcode";
        public static final String DURATION="Duration";
        public static final String FIXTURE_N="FixtureNo";
        public static final String FMWVER="FWVer";
        public static final String JOB_NO="JobNo";
        public static final String MODEL="Model";
        public static final String RESULT="Result";
        public static final String SERIAL="Serial";
        public static final String STARTED="StartedAt";
        public static final String BT_ADDR="BT_Addr";
    }

    public class Readings implements BaseColumns {
        public static final String TABLE = "Readings";
    }

    public class Sensors implements BaseColumns {
        public static final String TABLE = "Sensors";
        public static final String COL_READINGS = "Readings";
        public static final String ID = "Id";
    }

    public class Sensors0 implements BaseColumns


    {
        public static final String TABLE = "S0";
        public static final String ID = "Id";
    }

    public class
            SingleS0 implements BaseColumns {
        public static final String TABLE = "SingleS0";
    }

    public class Sensors1 implements BaseColumns {
        public static final String ID = "Id";
    }

    public class SingleS1 implements BaseColumns {
        public static final String TABLE = "SingleS1";
    }

    public class Sensors2 implements BaseColumns {
        public static final String ID="Id";
    }

    public class SingleS2 {
        public static final String TABLE ="SingleS2" ;
        public static final String ID="Id";
    }

    public class SingleTest implements BaseColumns{
        public static final String TABLE = "SingleTest";
        public static final String Test="Test";
        public static final String ID="Id";
    }
}
