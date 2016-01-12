package analytica.pericoach.android;

import hydrix.pfmat.generic.Device;
import hydrix.pfmat.generic.Device.Information;
import hydrix.pfmat.generic.DeviceInfo;
import hydrix.pfmat.generic.DeviceInfo.Info;
import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.Result;
import hydrix.pfmat.generic.TEST;
import hydrix.pfmat.generic.TestLimits;
import hydrix.pfmat.generic.TestSamples;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.RetrofitRestServices;
import server.pojos.ErrorFromServer;
import utils.MyDialogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoachengineering.classes.OpenTestResult;

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

	private final String DB_NAME = "PeriCoachTest";
	private final int DB_VERSION = 2;

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

		String strSQL = "INSERT INTO jobs(jobNo,test_id,totalqty) "
				+ "VALUES(?,?,?);";

		Object[] values = new Object[] { entity.getJobNo(), entity.getTestID(),
				entity.getTotalQty() };

		Log.d("SQL: ", strSQL);
		try {
			db.execSQL(strSQL, values);
		} catch (Exception e) {

		}

		db.close();
	}

	public void updateJob(Job entity) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL = "UPDATE jobs " + "SET " + " LastReportedRecord=?"
				+ ", ReportNumber=?" + "WHERE jobNo=?";

		Object[] values = new Object[] { entity.getLastReportedRecord(),
				entity.getLastReportNumber(), entity.getJobNo() };

		try {
			db.execSQL(strSQL, values);
		} catch (Exception e) {
			Log.d("FAIL:", "FAIL!");
		}

		db.close();

	}

	public void closeJob(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL = "UPDATE jobs " + "SET " + "active=1 " + "WHERE jobNo=?";

		Object[] values = new Object[] { jobNo };

		try {
			db.execSQL(strSQL, values);
		} catch (Exception e) {

		}

		db.close();
	}

	public void updateTestedQty(String jobNo, Boolean passed) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
		String strSQL;
		if (passed) {
			strSQL = "UPDATE jobs " + "SET " + "testedqty=testedqty + 1 "
					+ "WHERE jobNo=?";
		} else {
			strSQL = "UPDATE jobs " + "SET " + "testedqty=testedqty + 1 "
					+ ", passedqty=passedqty + 1 " + "WHERE jobNo=?";
		}

		Object[] values = new Object[] { jobNo };

		db.execSQL(strSQL, values);
		db.close();

	}

	public Integer jobExists(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT COUNT(*), rowid " + "FROM jobs " + "WHERE jobNo=?";

		String[] values = new String[] { jobNo };

		try {
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				// Log.d("COUNT:", Integer.toString(c.getInt(0)));
				if (c.getInt(0) == 0)
					db.close();
				return c.getInt(1);
			}

		} catch (Exception e) {

		}
		db.close();
		return 0;
	}

	public Job getJobDetails(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT rowid, * " + "FROM jobs " + "WHERE jobNo=?";

		String[] values = new String[] { jobNo };

		Job entity = new Job();

		try {
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				entity.setId(c.getInt(0));
				entity.setJobNo(c.getString(1));
				entity.setTestID(c.getInt(2));
				entity.setTotalQty(c.getInt(3));
				entity.setTestedQty(c.getInt(4));
				entity.setPassedQty(c.getInt(5));
				entity.setDate(c.getString(6));
				entity.setLastUpdated(c.getString(7));
				entity.setLastReportedRecord(c.getInt(8));
				entity.setLastReportNumber(c.getInt(9));
				entity.setActive(c.getInt(10));
			}

		} catch (Exception e) {

		}
		db.close();
		return entity;
	}

	public ArrayList<Job> getAllJobs() {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<Job> entityList = new ArrayList<Job>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery("SELECT * FROM jobs;", null);

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

	public ArrayList<Job> getAllActiveJobs() {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<Job> entityList = new ArrayList<Job>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery("SELECT * FROM jobs " + "WHERE active=0;",
					null);

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

	public ArrayList<Job> getAllActiveJobsForTest(Integer testType) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<Job> entityList = new ArrayList<Job>();

		String[] values = new String[] { Integer.toString(testType) };

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

	public ArrayList<Job> getAllCompletedJobs() {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<Job> entityList = new ArrayList<Job>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery("SELECT * FROM jobs "
					+ "WHERE totalquantity=testedquantity;", null);

			while (c.moveToNext()) {
				Job entity = new Job();

				entity.setId(c.getLong(0));
				entity.setJobNo(c.getString(1));
				entity.setTotalQty(c.getInt(2));
				entity.setTestedQty(c.getInt(3));
				entity.setPassedQty(c.getInt(4));
				entity.setDate(c.getString(5));
				entity.setLastUpdated(c.getString(6));
				entity.setActive(c.getInt(7));

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

	public ArrayList<DeviceList> getUniqueTestedDevicesforJob(Job job) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT * FROM deviceRecords d INNER JOIN testRecords t ON d.rowid=t.dev_id"
				+ " WHERE t.job_id=(SELECT rowid from jobs WHERE jobNo=?) GROUP BY d.devid ORDER BY d.rowid;";

		String[] values = new String[] { job.getJobNo() };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<DeviceList> deviceList = new ArrayList<DeviceList>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);
			Integer count = 0;
			while (c.moveToNext()) {
				count++;
				DeviceList device = new DeviceList();
				device.setDevId(c.getString(0));
				device.setSerial(null);
				device.setFirmwareVersion(c.getString(2));

				deviceList.add(device);
			}
			Log.d("DEVICE COUNT: ", Integer.toString(count));
			db.close();
			return deviceList;

		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}
		db.close();
		return null;
	}

	public Integer getLastReportNumber(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT ReportNumber " + "FROM jobs " + "WHERE jobNo=?";

		String[] values = new String[] { jobNo };

		try {
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				// Log.d("COUNT:", Integer.toString(c.getInt(0)));
				db.close();
				return c.getInt(0);
			}

		} catch (Exception e) {

		}
		db.close();
		return 0;
	}

	public ArrayList<DeviceList> getAllTestedDevicesforJob(Job job) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT * FROM deviceRecords d INNER JOIN testRecords t ON d.rowid=t.dev_id"
				+ " WHERE t.job_id=(SELECT rowid from jobs WHERE jobNo=?) GROUP BY d.devid ORDER BY d.rowid;";

		String[] values = new String[] { job.getJobNo() };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<DeviceList> deviceList = new ArrayList<DeviceList>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);
			Integer count = 0;
			while (c.moveToNext()) {
				count++;
				DeviceList device = new DeviceList();
				device.setDevId(c.getString(0));
				device.setSerial(null);
				device.setFirmwareVersion(c.getString(2));

				deviceList.add(device);
			}
			Log.d("DEVICE COUNT: ", Integer.toString(count));
			db.close();
			return deviceList;

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

		String[] values = new String[] { devid };

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

		String[] devID = { devid };

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

			Object[] values = new Object[] { devid, serial, version };

			Log.d("SQL: ", strSQL);

			db.execSQL(strSQL, values);

		} catch (Exception e) {
			// Insert failed
			db.close();
			return -1;

		}

		// Find rowid for inserted devid so that we can return index
		strSQL = "SELECT rowid FROM deviceRecords WHERE devid=?;";
		String[] devID = { devid };

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

	public Integer countResultsforJob(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT COUNT(*)" + " FROM testRecords"
				+ " WHERE job_id=(SELECT rowid from jobs WHERE jobNo=?)";

		String[] values = new String[] { jobNo };

		try {
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				// Log.d("COUNT:", Integer.toString(c.getInt(0)));
				return c.getInt(0);
			}

		} catch (Exception e) {

		}
		db.close();
		return null;
	}

	public Boolean jobEmpty(String jobNo) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT COUNT(*)" + " FROM testRecords"
				+ " WHERE job_id=(SELECT rowid from jobs WHERE jobNo=?)";

		String[] values = new String[] { jobNo };

		try {
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				// Log.d("COUNT:", Integer.toString(c.getInt(0)));
				if (c.getInt(0) == 0) {
					db.close();
					return true;
				}
			}

		} catch (Exception e) {

		}
		db.close();
		return false;
	}

	public void insertTestRecord(String jobNo, Integer testType,
			ClosedTestResult testResult) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL = "";
		String[] jobNum = { jobNo };
		Object[] values = new Object[] {};

		strSQL = "SELECT rowid FROM jobs WHERE jobNo=?;";

		try {
			// ask the database object to create the cursor.
			Log.d("SQL: ", strSQL);

			Cursor c = db.rawQuery(strSQL, jobNum);

			while (c.moveToNext()) {
				testResult.setJobId(c.getInt(0));
				Log.d("JOBID-SQL:", Integer.toString(c.getInt(0)));
			}
		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}

		Log.d("DB INSERT:", Integer.toString(testType));
		switch (testType) {
		case TEST.NO_TEST:
			db.close();
			break;
		case TEST.CLOSED_TEST: {
			strSQL = "INSERT INTO testRecords" + " (job_id, dev_id,"
					+ " zero0min, zero0max, zero0avg,"
					+ " zero1min, zero1max, zero1avg,"
					+ " zero2min, zero2max, zero2avg,"
					+ " weight0min, weight0max, weight0avg,"
					+ " weight1min, weight1max, weight1avg,"
					+ " weight2min, weight2max, weight2avg,"
					+ " test_id, result, operator)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

			values = new Object[] {
					testResult.getJobId(),
					testResult.getDevId(),
					testResult.getSamples().getMinSamples().get(0)
					.getLiteralSensor(0),
					testResult.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(0),
					testResult.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(0),
					testResult.getSamples().getMinSamples().get(0)
					.getLiteralSensor(1),
					testResult.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(1),
					testResult.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(1),
					testResult.getSamples().getMinSamples().get(0)
					.getLiteralSensor(2),
					testResult.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(2),
					testResult.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(2),
					testResult.getSamples().getMinSamples().get(1)
					.getLiteralSensor(0),
					testResult.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(0),
					testResult.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(0),
					testResult.getSamples().getMinSamples().get(1)
					.getLiteralSensor(1),
					testResult.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(1),
					testResult.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(1),
					testResult.getSamples().getMinSamples().get(1)
					.getLiteralSensor(2),
					testResult.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(2),
					testResult.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(2), testResult.getTestId(),
					testResult.getResult().toString(), testResult.getOperator() };

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
		case TEST.OPEN_TEST:
			strSQL = "INSERT INTO testRecords" + " (job_id, dev_id,"
					+ " zero0min, zero0max, zero0avg,"
					+ " zero1min, zero1max, zero1avg,"
					+ " zero2min, zero2max, zero2avg,"
					+ " weight0min, weight0max, weight0avg,"
					+ " weight1min, weight1max, weight1avg,"
					+ " weight2min, weight2max, weight2avg,"
					+ " test_id, result, operator)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

			if (testResult.getSamples().getMinSamples().size() < 3) {
				values = new Object[] {
						testResult.getJobId(),
						testResult.getDevId(),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getMinSamples().get(1)
						.getLiteralSensor(0),
						testResult.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(0),
						testResult.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(0),
						testResult.getSamples().getMinSamples().get(1)
						.getLiteralSensor(1),
						testResult.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(1),
						testResult.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(1),
						testResult.getSamples().getMinSamples().get(1)
						.getLiteralSensor(2),
						testResult.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(2),
						testResult.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(2), testResult.getTestId(),
						testResult.getResult().toString(),
						testResult.getOperator() };
			}
			if (testResult.getSamples().getMinSamples().size() > 2) {
				values = new Object[] {
						testResult.getJobId(),
						testResult.getDevId(),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(0),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(1),
						testResult.getSamples().getMinSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(2),
						testResult.getSamples().getMinSamples().get(2)
						.getLiteralSensor(0),
						testResult.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(0),
						testResult.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(0),
						testResult.getSamples().getMinSamples().get(2)
						.getLiteralSensor(1),
						testResult.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(1),
						testResult.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(1),
						testResult.getSamples().getMinSamples().get(2)
						.getLiteralSensor(2),
						testResult.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(2),
						testResult.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(2), testResult.getTestId(),
						testResult.getResult().toString(),
						testResult.getOperator() };
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

	// Gets job results after mLastReportNumber
	public ArrayList<ClosedTestResult> getAllResultsforJob(Job job) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT rowid,* FROM testRecords"
				+ " WHERE job_id=(SELECT rowid from jobs WHERE jobNo=?);";

		String[] values = new String[] { job.getJobNo() };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<ClosedTestResult> entityList = new ArrayList<ClosedTestResult>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);
			// Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
			while (c.moveToNext()) {
				if (c.getInt(0) > job.getLastReportedRecord()) {
					ClosedTestResult entity = new ClosedTestResult();
					TestSamples samples = new TestSamples(5);
					Log.d("ID", Integer.toString(c.getInt(0)));
					entity.setResultRecordID(c.getInt(0));
					entity.setJobId(c.getInt(1));
					entity.setDevId(c.getInt(2));
					// Zero Weight Samples
					samples.add(
							"Rest",
							(new Force((short) c.getInt(3),
									(short) c.getInt(6), (short) c.getInt(9))),
									(new Force((short) c.getInt(4),
											(short) c.getInt(7), (short) c.getInt(10))),
											(new Force((short) c.getInt(5),
													(short) c.getInt(8), (short) c.getInt(11))),
													new Result(c.getString(12)));
					// Test Weight Samples
					samples.add(
							"Rest",
							(new Force((short) c.getInt(12), (short) c
									.getInt(15), (short) c.getInt(18))),
									(new Force((short) c.getInt(13), (short) c
											.getInt(16), (short) c.getInt(19))),
											(new Force((short) c.getInt(14), (short) c
													.getInt(17), (short) c.getInt(20))),
													new Result(c.getString(12)));
					entity.setSamples(samples);
					entity.setTestId(c.getInt(21));
					if (c.getString(22).equals("PASS")) {
						entity.setResult(new Result(TEST.PASS));
					} else {
						entity.setResult(new Result(TEST.FAIL));
					}
					entity.setOperator(c.getString(23));
					entity.setDate(c.getString(24));
					entityList.add(entity);

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

	public Boolean resultsToReport(String jobNo) {

		ArrayList<ClosedTestResult> resultList = new ArrayList<ClosedTestResult>();

		Job job = new Job();
		job = getJobDetails(jobNo);

		resultList = getAllResultsforJob(job);

		if (resultList.size() > 0) {
			return true;
		}

		return false;
	}

	public ArrayList<TestLimits> getLimitsforTest(Integer testType) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
		
		String strSQL;

		strSQL = "SELECT * FROM testlimit"
				+ " WHERE test_id=(SELECT rowid FROM test WHERE type=?);";

		String[] values = new String[] { testType.toString() };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<TestLimits> entityList = new ArrayList<TestLimits>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);

			while (c.moveToNext()) {
				// TODO - Hardcoded Object size, Needs to be implemented more
				// efficiently
				TestLimits entity = new TestLimits();
				entity.setDesc(c.getString(0));
				entity.setTestID(c.getInt(1));
				entity.setSeqNo(c.getInt(2));
				entity.setLowerLimits(new Force((short) c.getInt(3), (short) c
						.getInt(4), (short) c.getInt(5)));
				entity.setUpperLimits(new Force((short) c.getInt(6), (short) c
						.getInt(7), (short) c.getInt(8)));
				entity.setStability(c.getInt(9));
				entity.setCreatedDate(c.getString(10));
				entity.setModifiedDate(c.getString(11));

				entityList.add(entity);
			}

			db.close();
			return entityList;

		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
		}

		return null;
	}

	public String getTestResultDate() {
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		String testResultDate = null;
		Cursor c = null;
		Cursor c1 = null;
		this.db = helper.getWritableDatabase();
		Long lastId = null;
		String query = "SELECT ROWID from testRecords order by ROWID DESC limit 1";
		try {
			c = db.rawQuery(query, null);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		if (c != null && c.moveToFirst()) {
			lastId = c.getLong(0); // The 0 is the column index, we only have 1
			// column, so the index is 0
		}
		String query1 = "SELECT created from testRecords WHERE rowid=?";
		String[] rowindstring = new String[] { Long.toString(lastId) };
		if (lastId != 0)
			try {
				c1 = db.rawQuery(query1, rowindstring);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		if (c1 != null && c1.moveToFirst()) {
			testResultDate = c1.getString(0);
		}
		db.close();
		String path = db.getPath();
		return testResultDate;

	}

	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		private static final String JOBS_TABLE_NAME = "jobs";
		private static final String JOBS_TESTTYPE_ID_COLUMN = "testtype_id";
		private static final String JOBS_JOB_ID_COLUMN = "job_id";
		private static final String JOBS_JOB_NUMBER_COLUMN = "jobNo";
		private Context context;
		public CustomSQLiteOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			this.context=context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String s;
			try {
				Toast.makeText(context, "1", 2000).show();
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
				Toast.makeText(context, t.toString(), 50000).show();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if(oldVersion<2){
				Log.d(TAG, "Upgrading database");
				String ADD_TESTTYPE_ID="ALTER TABLE "+JOBS_TABLE_NAME+" ADD COLUMN "+JOBS_TESTTYPE_ID_COLUMN+" NUMERIC";
				db.execSQL(ADD_TESTTYPE_ID);
				String ADD_JOB_ID="ALTER TABLE "+JOBS_TABLE_NAME+" ADD COLUMN "+JOBS_JOB_ID_COLUMN+" NUMERIC";
				db.execSQL(ADD_JOB_ID);
				getAndUpdateJobs(context);
			}
		}

		private void getAndUpdateJobs(final Context context) {
			RetrofitRestServices.getRest(context).getJobListActiveJobs(PeriCoachTestApplication.getDeviceid(), new Callback<List<server.pojos.Job>>() {
				@Override
				public void success(List<server.pojos.Job> arg0, Response arg1) {
					CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
					db=helper.getWritableDatabase();
					if (arg0 == null || arg0.size() <= 0) {/*Do nothing*/} else
					{

					for(server.pojos.Job job:arg0){

						ContentValues values=new ContentValues();
						values.put(JOBS_JOB_ID_COLUMN,job.getId());
						values.put(JOBS_TESTTYPE_ID_COLUMN,job.getTesttypeId());
						String selection=JOBS_JOB_NUMBER_COLUMN+" = ?";
						String[] selectionArgs = new String[]{job.getJobno()};
						db.update(JOBS_TABLE_NAME,values,selection,selectionArgs);
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
		String[] jobNum = { mJobNo };
		Object[] values = new Object[] {};

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

			values = new Object[] {
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
					result.getResult().toString(), result.getOperator() };

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
		case TEST.EXPERIMENTAL_CLOSED_TEST:{
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

			values = new Object[] {
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
					result.getResult().toString(), result.getOperator() };

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
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
			}
			if (result.getSamples().getMinSamples().size() > 2) {
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
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
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
			}
			if (result.getSamples().getMinSamples().size() > 2) {
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
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
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
			}
			if (result.getSamples().getMinSamples().size() > 2) {
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
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
	public ArrayList<OpenTestResult> getAllOpenResultsforJob(Job job) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL;

		strSQL = "SELECT rowid,* FROM testRecords"
				+ " WHERE job_id=(SELECT rowid from jobs WHERE jobNo=?);";

		String[] values = new String[] { job.getJobNo() };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<OpenTestResult> entityList = new ArrayList<OpenTestResult>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);
			// Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
			while (c.moveToNext()) {
				if (c.getInt(0) > job.getLastReportedRecord()) {
					OpenTestResult entity = new OpenTestResult();
					TestSamples samples = new TestSamples(5);
					Log.d("ID", Integer.toString(c.getInt(0)));
					entity.setResultRecordID(c.getInt(0));
					entity.setJobId(c.getInt(1));
					entity.setDevId(c.getInt(2));
					// Zero Weight Samples
					samples.add(
							"Rest",
							(new Force((short) c.getInt(3),
									(short) c.getInt(6), (short) c.getInt(9))),
									(new Force((short) c.getInt(4),
											(short) c.getInt(7), (short) c.getInt(10))),
											(new Force((short) c.getInt(5),
													(short) c.getInt(8), (short) c.getInt(11))),
													new Result(c.getString(12)));
					// Test Weight Samples
					samples.add(
							"Rest",
							(new Force((short) c.getInt(12), (short) c
									.getInt(15), (short) c.getInt(18))),
									(new Force((short) c.getInt(13), (short) c
											.getInt(16), (short) c.getInt(19))),
											(new Force((short) c.getInt(14), (short) c
													.getInt(17), (short) c.getInt(20))),
													new Result(c.getString(12)));
					entity.setSamples(samples);
					entity.setTestId(c.getInt(21));
					if (c.getString(22).equals("PASS")) {
						entity.setResult(new Result(TEST.PASS));
					} else {
						entity.setResult(new Result(TEST.FAIL));
					}
					entity.setOperator(c.getString(23));
					entity.setDate(c.getString(24));
					entity.setBarcode(c.getString(25));
					entityList.add(entity);

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
		Cursor  cursor = db.rawQuery("SELECT * from devices",null);
		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, null);
			// Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
			if(c!=null && c.getCount() > 0){
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

			Object[] values = new Object[] {devid};

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

			Object[] values = new Object[] {scancode};

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
		Cursor  cursor = db.rawQuery("SELECT * from scancodes",null);
		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, null);
			// Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
			if(c!=null && c.getCount() > 0){
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
	public Boolean checkIfResultsToReport(String jobnumber) {
		Boolean recordsAvailable=false;
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		Job job=getJobDetails(jobnumber);
		this.db = helper.getWritableDatabase();
		
		String strSQL;

		strSQL = "SELECT rowid,* FROM testRecords"
				+ " WHERE job_id=(SELECT rowid from jobs WHERE jobNo=?);";

		String[] values = new String[] { jobnumber };

		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<ClosedTestResult> entityList = new ArrayList<ClosedTestResult>();

		try {
			// ask the database object to create the cursor.
			Cursor c = db.rawQuery(strSQL, values);
			// Log.d("ALLRESULTSROWS:", Integer.toString(c.getCount()));
			while (c.moveToNext()) {
				if (c.getInt(0) > job.getLastReportedRecord()) {
					recordsAvailable=true;

				}

			}
			db.close();
			return recordsAvailable;

		} catch (SQLException e) {
			Log.e("DB Error", e.toString());
			e.printStackTrace();
			db.close();
			return false;
		}
		
		
	}
	public long insertTestRecordNew(String mJobNo, int mTestType, String scancode,
			ClosedTestResult result) {

		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();

		String strSQL = "";
		String[] jobNum = { mJobNo };
		//Object[] values = new Object[] {};
		ContentValues valuetobeinserted = new ContentValues();
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
		
		case TEST.NEW_CLOSED_TEST: 
		case TEST.EXPERIMENTAL_CLOSED_TEST:
		{
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
			/*
			values = new Object[] {
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
					result.getResult().toString(), result.getOperator() };
			*/
			Log.d("SQL-INSERT: ", strSQL);
			String tmp = "";
			valuetobeinserted.put("job_id", result.getJobId());
			valuetobeinserted.put("dev_id", result.getDevId());
			valuetobeinserted.put("scancode", scancode);
			
			valuetobeinserted.put("zero0min", result.getSamples().getMinSamples().get(0)
					.getLiteralSensor(0));
			valuetobeinserted.put("zero0max", result.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(0));
			valuetobeinserted.put("zero0avg", result.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(0));
			valuetobeinserted.put("zero1min", result.getSamples().getMinSamples().get(0)
					.getLiteralSensor(1));
			valuetobeinserted.put("zero1max", result.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(1));
			valuetobeinserted.put("zero1avg", result.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(1));
			valuetobeinserted.put("zero2min", result.getSamples().getMinSamples().get(0)
					.getLiteralSensor(2));
			valuetobeinserted.put("zero2max", result.getSamples().getMaxSamples().get(0)
					.getLiteralSensor(2));
			valuetobeinserted.put("zero2avg", result.getSamples().getAvgSamples().get(0)
					.getLiteralSensor(2));
			valuetobeinserted.put("weight0min", result.getSamples().getMinSamples().get(1)
					.getLiteralSensor(0));
			valuetobeinserted.put("weight0max", result.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(0));
			valuetobeinserted.put("weight0avg", result.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(0));
			valuetobeinserted.put("weight1min", result.getSamples().getMinSamples().get(1)
					.getLiteralSensor(1));
			valuetobeinserted.put("weight1max", result.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(1));
			valuetobeinserted.put("weight1avg", result.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(1));
			valuetobeinserted.put("weight2min", result.getSamples().getMinSamples().get(1)
					.getLiteralSensor(2));
			valuetobeinserted.put("weight2max", result.getSamples().getMaxSamples().get(1)
					.getLiteralSensor(2));
			valuetobeinserted.put("weight2avg", result.getSamples().getAvgSamples().get(1)
					.getLiteralSensor(2));
			
			valuetobeinserted.put("test_id", result.getTestId());
			valuetobeinserted.put("result", result.getResult().toString());
			valuetobeinserted.put("operator", result.getOperator());
			/*
			for (int i = 0; i < values.length; i++) {
				tmp = tmp + values[i] + ",";
			}
			*/
			Log.d("SQL-INSERT", tmp);
			try {
				//db.execSQL(strSQL, values);
				long rowid=db.insert("testRecords", null, valuetobeinserted);
				db.close();
				return rowid;
			} catch (Exception e) {
				Log.d("SQL: ", "INSERT FAILED!");
				db.close();
				return -1;
			}

		}
		
		case TEST.INTEGRATED_OPEN_TEST: 
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
				valuetobeinserted.put("job_id", result.getJobId());
				valuetobeinserted.put("dev_id", result.getDevId());
				valuetobeinserted.put("scancode", scancode);
				
				valuetobeinserted.put("zero0min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero0max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero0avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero1min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero1max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero1avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero2min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("zero2max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("zero2avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight0min", result.getSamples().getMinSamples().get(1)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight0max", result.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight0avg", result.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight1min", result.getSamples().getMinSamples().get(1)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight1max", result.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight1avg", result.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight2min", result.getSamples().getMinSamples().get(1)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight2max", result.getSamples().getMaxSamples().get(1)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight2avg", result.getSamples().getAvgSamples().get(1)
						.getLiteralSensor(2));
				
				valuetobeinserted.put("test_id", result.getTestId());
				valuetobeinserted.put("result", result.getResult().toString());
				valuetobeinserted.put("operator", result.getOperator());
				/*
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
						*/
			}
			if (result.getSamples().getMinSamples().size() > 2) {
				valuetobeinserted.put("job_id", result.getJobId());
				valuetobeinserted.put("dev_id", result.getDevId());
				valuetobeinserted.put("scancode", scancode);
				
				valuetobeinserted.put("zero0min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero0max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero0avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(0));
				valuetobeinserted.put("zero1min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero1max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero1avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(1));
				valuetobeinserted.put("zero2min", result.getSamples().getMinSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("zero2max", result.getSamples().getMaxSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("zero2avg", result.getSamples().getAvgSamples().get(0)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight0min", result.getSamples().getMinSamples().get(2)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight0max", result.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight0avg", result.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(0));
				valuetobeinserted.put("weight1min", result.getSamples().getMinSamples().get(2)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight1max", result.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight1avg", result.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(1));
				valuetobeinserted.put("weight2min", result.getSamples().getMinSamples().get(2)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight2max", result.getSamples().getMaxSamples().get(2)
						.getLiteralSensor(2));
				valuetobeinserted.put("weight2avg", result.getSamples().getAvgSamples().get(2)
						.getLiteralSensor(2));
				
				valuetobeinserted.put("test_id", result.getTestId());
				valuetobeinserted.put("result", result.getResult().toString());
				valuetobeinserted.put("operator", result.getOperator());
				/*
				values = new Object[] {
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
						result.getResult().toString(), result.getOperator() };
						*/
			}
			Log.d("SQL-INSERT: ", strSQL);
			String temp = "";
			/*
			for (int i = 0; i < values.length; i++) {
				temp = temp + values[i] + ",";
			}
			*/
			Log.d("SQL-INSERT", temp);
			try {
			//		db.execSQL(strSQL, values);
				long rowid=db.insert("testRecords", null, valuetobeinserted);
				db.close();
				return rowid;
			} catch (Exception e) {
				Log.d("SQL: ", "INSERT FAILED!");
				db.close();
				return -1;
			}
		}


		}
		return 0;

	}
	
	public String getDateAndTime(long rowid){
		String datandtime= null;
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
		Cursor cursor = db.
				  rawQuery("select created from testRecords where rowid = ?", new String[] { Long.toString(rowid) }); 
		if (cursor != null)
	        {
			cursor.moveToFirst();
			
			datandtime=cursor.getString(0);
			db.close();
			
	        }
		db.close();    
		return datandtime;
		
	}

	public String newInsert(String mJobNo, int mTestType, String scancode,
			ClosedTestResult result){
			long rowid=insertTestRecordNew(mJobNo, mTestType, scancode, result);
			if(rowid!=-1){
				String dateandtime=getDateAndTime(rowid);
				if(dateandtime!=null){return dateandtime;}
				else {return null;}
			}
			else return null;
		
		
	}
}
