package analytica.pericoach.android;

import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.TEST;
import hydrix.pfmat.generic.TestLimits;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.RetrofitRestServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBManager {
    Context context;
    private static final String TAG = "DBManager";
    private SQLiteDatabase db;

    private static final String DB_NAME = "PeriCoachTest";
    private static final int DB_VERSION = 3;

    public DBManager(Context context) {
        this.context = context;

    }
    // *** JOB TABLE DATABASE QUERIES ***
    public void insertJob(Job entity) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDate = sdf1.format(new Date());
        String currentDateandTime = sdf2.format(new Date());

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

		/*
         * String strSQL =
		 * "INSERT INTO jobs(jobNo,totalqty,testedqty,passedqty,created,LastUpdated,active) "
		 * + "VALUES(?,?,0,0,?,?,0);";
		 * 
		 * Object[] values = new Object[] { entity.getJobNo(),
		 * entity.getTotalQty(), currentDate, currentDateandTime };
		 */

        String strSQL = "INSERT INTO jobs("
                //"jobNo,test_id,totalqty"
                + Contract.JOBS_JOB_NUMBER_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_TESTID_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_TOTALQUANTITY_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_JOB_ID_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_TESTTYPE_ID_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_ACTIVE_COLUMN
                + Contract.SEPARATOR + Contract.JOBS_STAGE_DEP
                +
        ") "
                + "VALUES(?,?,?,?,?);";

        Object[] values = new Object[]{
                entity.getJobNo(),
                entity.getTestID(),
                entity.getTotalQty(),
                entity.getJob_id(),
                entity.getTesttype_id(),
                entity.isActive()?0:1,
                entity.getStage_dep()
        };

        Log.d("SQL: ", strSQL);
        try {
            db.execSQL(strSQL, values);
        } catch (Exception e) {
        }
        db.close();
    }

    public void closeJob(String jobNo) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL = "UPDATE jobs " + "SET " + "active=1 " + "WHERE jobNo=?";

        Object[] values = new Object[]{jobNo};

        try {
            db.execSQL(strSQL, values);
        } catch (Exception e) {

        }

        db.close();
    }


    public ArrayList<Job> getAllActiveJobsForTest(Integer testType) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        // create an ArrayList that will hold all of the data collected from
        // the database.
        ArrayList<Job> entityList = new ArrayList<Job>();

        String[] values = new String[]{Integer.toString(testType)};

        try {
            // ask the database object to create the cursor.
            Cursor c = db.rawQuery("SELECT * FROM jobs "
                    + "WHERE active=0 AND test_id=?;", values);

            while (c.moveToNext()) {
                Job entity = new Job();

                entity.setJobNo(c.getString(0));
                entity.setTestID(c.getInt(1));
                entity.setTotalQty(c.getInt(2));
                entity.setTestedQty(c.getInt(3));
                entity.setPassedQty(c.getInt(4));
                entity.setDate(c.getString(5));
                entity.setLastUpdated(c.getString(6));
                entity.setLastReportedRecord(c.getInt(7));
                entity.setLastReportNumber(c.getInt(8));
                entity.setActive(c.getInt(9));
                entity.setJob_id(c.getInt(c.getColumnIndexOrThrow(Contract.JOBS_JOB_ID_COLUMN)));
                entity.setTesttype_id(c.getInt(c.getColumnIndexOrThrow(Contract.JOBS_TESTTYPE_ID_COLUMN)));
                entity.setStage_dep(c.getColumnIndexOrThrow(Contract.JOBS_STAGE_DEP));

                entityList.add(entity);
            }

            db.close();
            return entityList;

        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        db.close();
        return null;
    }

    public Integer deviceExists(String devid) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL;

        strSQL = "SELECT rowid, COUNT(*)" + " FROM deviceRecords"
                + " WHERE devid=?";

        String[] values = new String[]{devid};

        try {
            Cursor c = db.rawQuery(strSQL, values);

            while (c.moveToNext()) {
                // Log.d("COUNT:", Integer.toString(c.getInt(0)));
                return c.getInt(0);

            }

        } catch (Exception e) {

        }
        db.close();
        return 0;
    }

    public Integer deviceRecordIndex(String devid) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        Integer returnval = 0;

        // Find rowid for inserted devid so that we can return index
        String strSQL = "SELECT rowid FROM deviceRecords WHERE devid=?;";

        String[] devID = {devid};

        try {
            // ask the database object to create the cursor.
            Log.d("SQL: ", strSQL);

            Cursor c = db.rawQuery(strSQL, devID);

            while (c.moveToNext()) {
                returnval = c.getInt(0);
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        db.close();
        return returnval;
    }

    public Integer insertDeviceRecord(String devid, String serial,
                                      String version) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL = "INSERT INTO deviceRecords(devid, serial, version) "
                + "VALUES(?,?,?);";

        Integer returnval = 0;

        try {
            // Perform the insert of device record

            Object[] values = new Object[]{devid, serial, version};

            Log.d("SQL: ", strSQL);

            db.execSQL(strSQL, values);

        } catch (Exception e) {
            // Insert failed
            db.close();
            return -1;

        }

        // Find rowid for inserted devid so that we can return index
        strSQL = "SELECT rowid FROM deviceRecords WHERE devid=?;";
        String[] devID = {devid};

        try {
            // ask the database object to create the cursor.
            Log.d("SQL: ", strSQL);

            Cursor c = db.rawQuery(strSQL, devID);

            while (c.moveToNext()) {
                returnval = c.getInt(0);
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        db.close();
        return returnval;

    }


    private static class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
        private static CustomSQLiteOpenHelper customSQLiteOpenHelper;
        private Context context;

        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        public static CustomSQLiteOpenHelper getCustomSQLiteOpenHelper(Context context){
            if(CustomSQLiteOpenHelper.customSQLiteOpenHelper==null){
                CustomSQLiteOpenHelper.customSQLiteOpenHelper=new CustomSQLiteOpenHelper(context);
            }
            return customSQLiteOpenHelper;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String s;
            try {
                Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
                InputStream in = context.getResources().openRawResource(
                        R.raw.sql);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(in, null);
                NodeList statements = doc.getElementsByTagName("statement");
                for (int i = 0; i < statements.getLength(); i++) {
                    s = statements.item(i).getChildNodes().item(0)
                            .getNodeValue();
                    // Log.d("SQL:", s);
                    db.execSQL(s);
                }
            } catch (Throwable t) {
                Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                Log.d(TAG, "Upgrading database");
                String ADD_TESTTYPE_ID = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_TESTTYPE_ID_COLUMN + " NUMERIC";
                db.execSQL(ADD_TESTTYPE_ID);
                String ADD_JOB_ID = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_JOB_ID_COLUMN + " NUMERIC";
                db.execSQL(ADD_JOB_ID);
                getAndUpdateJobs(context);
            }
            if(oldVersion<3){
                Log.d(TAG, "Upgrading database");
                String ADD_TESTTYPE_ID = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_STAGE_DEP + " NUMERIC";
                db.execSQL(ADD_TESTTYPE_ID);
            }
        }

        private void getAndUpdateJobs(final Context context) {
            RetrofitRestServices.getRest(context).getJobListActiveJobs(PeriCoachTestApplication.getDeviceid(), new Callback<List<server.pojos.Job>>() {
                @Override
                public void success(List<server.pojos.Job> arg0, Response arg1) {
                    CustomSQLiteOpenHelper helper = getCustomSQLiteOpenHelper(context);
                    SQLiteDatabase database = helper.getWritableDatabase();
                    if (arg0 == null || arg0.size() <= 0) {/*Do nothing*/} else {
                        try {
                            for (server.pojos.Job job : arg0) {

                                ContentValues values = new ContentValues();
                                values.put(Contract.JOBS_JOB_ID_COLUMN, job.getId());
                                values.put(Contract.JOBS_TESTTYPE_ID_COLUMN, job.getTesttypeId());
                                String selection = Contract.JOBS_JOB_NUMBER_COLUMN + " = ?";
                                String[] selectionArgs = new String[]{job.getJobno()};
                                database.update(Contract.JOBS_TABLE_NAME, values, selection, selectionArgs);
                            }
                        } finally {
                            database.close();
                        }
                    }
                }

                @Override
                public void failure(RetrofitError arg0) {/*Do nothing*/}
            });
        }

    }

    public void insertTestRecord(String mJobNo, int mTestType, String scancode,
                                 ClosedTestResult result) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL = "";
        String[] jobNum = {mJobNo};
        Object[] values = new Object[]{};

        strSQL = "SELECT rowid FROM jobs WHERE jobNo=?;";

        try {
            // ask the database object to create the cursor.
            Log.d("SQL: ", strSQL);

            Cursor c = db.rawQuery(strSQL, jobNum);

            while (c.moveToNext()) {
                result.setJobId(c.getInt(0));
                Log.d("JOBID-SQL:", Integer.toString(c.getInt(0)));
            }
        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        Log.d("DB INSERT:", Integer.toString(mTestType));
        switch (mTestType) {
            case TEST.NO_TEST:
                db.close();
                break;
            case TEST.CLOSED_TEST: {
                strSQL = "INSERT INTO testRecords"
                        + " (job_id, dev_id, scancode,"
                        + " zero0min, zero0max, zero0avg,"
                        + " zero1min, zero1max, zero1avg,"
                        + " zero2min, zero2max, zero2avg,"
                        + " weight0min, weight0max, weight0avg,"
                        + " weight1min, weight1max, weight1avg,"
                        + " weight2min, weight2max, weight2avg,"
                        + " test_id, result, operator)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                values = new Object[]{
                        result.getJobId(),
                        result.getDevId(),
                        scancode,
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(2),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(2),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(2), result.getTestId(),
                        result.getResult().toString(), result.getOperator()};

                Log.d("SQL-INSERT: ", strSQL);
                String tmp = "";

                for (int i = 0; i < values.length; i++) {
                    tmp = tmp + values[i] + ",";
                }
                Log.d("SQL-INSERT", tmp);
                try {
                    db.execSQL(strSQL, values);

                } catch (Exception e) {
                    Log.d("SQL: ", "INSERT FAILED!");
                }
                db.close();
                break;

            }
            case TEST.NEW_CLOSED_TEST:
            case TEST.EXPERIMENTAL_CLOSED_TEST: {
                strSQL = "INSERT INTO testRecords"
                        + " (job_id, dev_id, scancode,"
                        + " zero0min, zero0max, zero0avg,"
                        + " zero1min, zero1max, zero1avg,"
                        + " zero2min, zero2max, zero2avg,"
                        + " weight0min, weight0max, weight0avg,"
                        + " weight1min, weight1max, weight1avg,"
                        + " weight2min, weight2max, weight2avg,"
                        + " test_id, result, operator)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                values = new Object[]{
                        result.getJobId(),
                        result.getDevId(),
                        scancode,
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(0),
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(1),
                        result.getSamples().getMinSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getMaxSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getAvgSamples().get(0)
                                .getLiteralSensor(2),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(0),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(1),
                        result.getSamples().getMinSamples().get(1)
                                .getLiteralSensor(2),
                        result.getSamples().getMaxSamples().get(1)
                                .getLiteralSensor(2),
                        result.getSamples().getAvgSamples().get(1)
                                .getLiteralSensor(2), result.getTestId(),
                        result.getResult().toString(), result.getOperator()};

                Log.d("SQL-INSERT: ", strSQL);
                String tmp = "";

                for (int i = 0; i < values.length; i++) {
                    tmp = tmp + values[i] + ",";
                }
                Log.d("SQL-INSERT", tmp);
                try {
                    db.execSQL(strSQL, values);

                } catch (Exception e) {
                    Log.d("SQL: ", "INSERT FAILED!");
                }
                db.close();
                break;

            }
            case TEST.OPEN_TEST: {
                strSQL = "INSERT INTO testRecords"
                        + " (job_id, dev_id, scancode,"
                        + " zero0min, zero0max, zero0avg,"
                        + " zero1min, zero1max, zero1avg,"
                        + " zero2min, zero2max, zero2avg,"
                        + " weight0min, weight0max, weight0avg,"
                        + " weight1min, weight1max, weight1avg,"
                        + " weight2min, weight2max, weight2avg,"
                        + " test_id, result, operator)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                if (result.getSamples().getMinSamples().size() < 3) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                if (result.getSamples().getMinSamples().size() > 2) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                Log.d("SQL-INSERT: ", strSQL);
                String tmp = "";

                for (int i = 0; i < values.length; i++) {
                    tmp = tmp + values[i] + ",";
                }
                Log.d("SQL-INSERT", tmp);
                try {
                    db.execSQL(strSQL, values);

                } catch (Exception e) {
                    Log.d("SQL: ", "INSERT FAILED!");
                }

                db.close();
                break;
            }

            case TEST.DYNAMIC_TEST: {
                strSQL = "INSERT INTO testRecords"
                        + " (job_id, dev_id, scancode,"
                        + " zero0min, zero0max, zero0avg,"
                        + " zero1min, zero1max, zero1avg,"
                        + " zero2min, zero2max, zero2avg,"
                        + " weight0min, weight0max, weight0avg,"
                        + " weight1min, weight1max, weight1avg,"
                        + " weight2min, weight2max, weight2avg,"
                        + " test_id, result, operator)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                if (result.getSamples().getMinSamples().size() < 3) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                if (result.getSamples().getMinSamples().size() > 2) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                Log.d("SQL-INSERT: ", strSQL);
                String temp = "";

                for (int i = 0; i < values.length; i++) {
                    temp = temp + values[i] + ",";
                }
                Log.d("SQL-INSERT", temp);
                try {
                    db.execSQL(strSQL, values);

                } catch (Exception e) {
                    Log.d("SQL: ", "INSERT FAILED!");
                }

                db.close();
                break;
            }

            case TEST.NEW_OPEN_TEST: {
                strSQL = "INSERT INTO testRecords"
                        + " (job_id, dev_id, scancode,"
                        + " zero0min, zero0max, zero0avg,"
                        + " zero1min, zero1max, zero1avg,"
                        + " zero2min, zero2max, zero2avg,"
                        + " weight0min, weight0max, weight0avg,"
                        + " weight1min, weight1max, weight1avg,"
                        + " weight2min, weight2max, weight2avg,"
                        + " test_id, result, operator)"
                        + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

                if (result.getSamples().getMinSamples().size() < 3) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(1)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(1)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                if (result.getSamples().getMinSamples().size() > 2) {
                    values = new Object[]{
                            result.getJobId(),
                            result.getDevId(),
                            scancode,
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(0)
                                    .getLiteralSensor(2),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(0),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(1),
                            result.getSamples().getMinSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getMaxSamples().get(2)
                                    .getLiteralSensor(2),
                            result.getSamples().getAvgSamples().get(2)
                                    .getLiteralSensor(2), result.getTestId(),
                            result.getResult().toString(), result.getOperator()};
                }
                Log.d("SQL-INSERT: ", strSQL);
                String tmp = "";

                for (int i = 0; i < values.length; i++) {
                    tmp = tmp + values[i] + ",";
                }
                Log.d("SQL-INSERT", tmp);
                try {
                    db.execSQL(strSQL, values);

                } catch (Exception e) {
                    Log.d("SQL: ", "INSERT FAILED!");
                }

                db.close();
                break;
            }

        }

    }

    // Gets job results after mLastReportNumber
    public ArrayList<String> getAllDevices() {
        //Random rand = new Random();
        //String stri=Integer.toString((rand.nextInt((1000 - 0) + 1) + 0));
        //return new ArrayList<String>(Arrays.asList(stri));

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL;

        strSQL = "SELECT device FROM devices";


        //String[] values = new String[] { device };

        // create an ArrayList that will hold all of the data collected from
        // the database.
        ArrayList<String> entityList = null;
        Cursor cursor = db.rawQuery("SELECT * from devices", null);
        try {
            // ask the database object to create the cursor.
            Cursor c = db.rawQuery(strSQL, null);
            // Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
            if (c != null && c.getCount() > 0) {
                entityList = new ArrayList<String>();
                while (c.moveToNext()) {
                    entityList.add(c.getString(0));

                }
            }
            db.close();
            return entityList;

        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        db.close();
        return null;

    }

    public Boolean insertDeviceID(String devid) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL = "INSERT INTO devices(device) "
                + "VALUES(?);";


        try {
            // Perform the insert of device record

            Object[] values = new Object[]{devid};

            Log.d("SQL: ", strSQL);

            db.execSQL(strSQL, values);

        } catch (Exception e) {
            // Insert failed
            db.close();
            return false;

        }
        db.close();
        return true;


    }

    public Boolean insertScancode(String scancode) {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL = "INSERT INTO scancodes(scancode) "
                + "VALUES(?);";


        try {
            // Perform the insert of device record

            Object[] values = new Object[]{scancode};

            Log.d("SQL: ", strSQL);

            db.execSQL(strSQL, values);

        } catch (Exception e) {
            // Insert failed
            db.close();
            return false;

        }
        db.close();
        return true;
    }

    public ArrayList<String> getAllScancodes() {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        String strSQL;

        strSQL = "SELECT scancode FROM scancodes";


        //String[] values = new String[] { device };

        // create an ArrayList that will hold all of the data collected from
        // the database.
        ArrayList<String> entityList = null;
        Cursor cursor = db.rawQuery("SELECT * from scancodes", null);
        try {
            // ask the database object to create the cursor.
            Cursor c = db.rawQuery(strSQL, null);
            // Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
            if (c != null && c.getCount() > 0) {
                entityList = new ArrayList<String>();
                while (c.moveToNext()) {
                    entityList.add(c.getString(0));

                }
            }
            db.close();
            return entityList;

        } catch (SQLException e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        db.close();
        return null;

    }

}
