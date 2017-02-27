package analytica.pericoach.android;

import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {
    private final Context context;
    private SQLiteDatabase db;
    private final String TAG = getClass().getSimpleName();

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
                + Contract.SEPARATOR + Contract.JOBS_SET_SENSOR_TEST_FLAG
                + Contract.SEPARATOR + Contract.JOBS_DISCONNECT_POWER_STATE
                +
        ") "
                + "VALUES(?,?,?,?,?,?,?,?,?);";

        Object[] values = new Object[]{
                entity.getJobNo(),
                entity.getTestID(),
                entity.getTotalQty(),
                entity.getJob_id(),
                entity.getTesttype_id(),
                entity.isActive()?0:1,
                entity.getStage_dep(),
                entity.getSetSensorTestFlag(),
                entity.getDisconnectPowerState()
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

    public Job getJob(String jobNo) {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM jobs "
        + "WHERE jobNo = ", new String[]{jobNo});
        Job entity = new Job();

        while (c.moveToNext()) {
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
            entity.setSetSensorTestFlag(c.getColumnIndexOrThrow(Contract.JOBS_SET_SENSOR_TEST_FLAG));
            entity.setDisconnectPowerState(c.getColumnIndexOrThrow(Contract.JOBS_DISCONNECT_POWER_STATE));
        }
        db.close();
        return entity;
    }

    public void updateJob(server.pojos.Job job) {
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();
        Job oldJob = getJob(job.getJobno());

        ContentValues values = new ContentValues();
        values.put(Contract.JOBS_JOB_ID_COLUMN, job.getId());
        values.put(Contract.JOBS_TESTTYPE_ID_COLUMN, job.getTesttypeId());
        values.put(Contract.JOBS_TOTALQUANTITY_COLUMN, job.getQuantity());
        values.put(Contract.JOBS_SET_SENSOR_TEST_FLAG, job.getSetSensorTestFlag());
        values.put(Contract.JOBS_DISCONNECT_POWER_STATE, job.getDisconnectPowerState());
        String selection = Contract.JOBS_JOB_NUMBER_COLUMN + " = ?";
        String[] selectionArgs = new String[]{job.getJobno()};
        db.update(Contract.JOBS_TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }


    public ArrayList<Job> getAllActiveJobsForTest() {

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();

        // create an ArrayList that will hold all of the data collected from
        // the database.
        ArrayList<Job> entityList = new ArrayList<Job>();

        String[] values = new String[]{Integer.toString(hydrix.pfmat.generic.TEST.OPEN_TEST)};

        try {
            // ask the database object to create the cursor.
            Cursor c = db.rawQuery("SELECT * FROM jobs "
                    + "WHERE active=0 AND test_id=?;", values);

            while (c.moveToNext()) {
                Job entity = new Job();

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
                entity.setSetSensorTestFlag(c.getColumnIndexOrThrow(Contract.JOBS_SET_SENSOR_TEST_FLAG));
                entity.setDisconnectPowerState(c.getColumnIndexOrThrow(Contract.JOBS_DISCONNECT_POWER_STATE));

                entityList.add(entity);
            }
            c.close();
            db.close();
            for (Job job : entityList) {
                Log.d(TAG, job.toString());
            }
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

}
