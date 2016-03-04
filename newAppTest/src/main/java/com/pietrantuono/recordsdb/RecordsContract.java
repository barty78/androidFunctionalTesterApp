package com.pietrantuono.recordsdb;

import android.provider.BaseColumns;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class RecordsContract {

    public static class TestRecords implements BaseColumns {

        public static final String TABLE = "TestRecord";
        public static final String ID = "Id";
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
    }

    public class SingleTest implements BaseColumns{
        public static final String TABLE = "SingleTest";
        public static final String Test="Test";
    }
}
