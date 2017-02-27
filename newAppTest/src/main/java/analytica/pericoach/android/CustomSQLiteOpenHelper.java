package analytica.pericoach.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.RetrofitRestServices;
import server.pojos.Job;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
public class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
    private static CustomSQLiteOpenHelper customSQLiteOpenHelper;
    private final Context context;
    private static final String DB_NAME = "PeriCoachTest";
    private static final int DB_VERSION = 6;
    private static String TAG;

    public CustomSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        TAG = getClass().getSimpleName();
    }

    public static CustomSQLiteOpenHelper getCustomSQLiteOpenHelper(Context context) {
        if (CustomSQLiteOpenHelper.customSQLiteOpenHelper == null) {
            CustomSQLiteOpenHelper.customSQLiteOpenHelper = new CustomSQLiteOpenHelper(context);
        }
        Log.d(TAG, "Database Version: " + DB_VERSION);
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
        db.execSQL(Contract.DevicesColumns.CREATE_DEVICES_TABLE);
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
        if (oldVersion < 3) {
            Log.d(TAG, "Upgrading database");
            String ADD_TESTTYPE_ID = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_STAGE_DEP + " NUMERIC";
            db.execSQL(ADD_TESTTYPE_ID);
        }
        if (oldVersion < 4) {
            Log.d(TAG, "Upgrading database");
            db.execSQL(Contract.DevicesColumns.CREATE_DEVICES_TABLE);
        }
        if (oldVersion < 5) {
            Log.d(TAG, "Upgrading Database");
            String ADD_SENSOR_TEST_FLAG = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_SET_SENSOR_TEST_FLAG + " NUMERIC";
            db.execSQL(ADD_SENSOR_TEST_FLAG);
//            getAndUpdateJobs(context);
        }
        if (oldVersion < 6) {
            String ADD_DISCONNECT_POWER_STATE = "ALTER TABLE " + Contract.JOBS_TABLE_NAME + " ADD COLUMN " + Contract.JOBS_DISCONNECT_POWER_STATE + " NUMERIC";
            db.execSQL(ADD_DISCONNECT_POWER_STATE);
            getAndUpdateJobs(context);
        }
    }

    private void getAndUpdateJobs(final Context context) {
        RetrofitRestServices.getRest(context).getJobListActiveJobs(PeriCoachTestApplication.getDeviceid(), new Callback<List<Job>>() {
            @Override
            public void success(List<Job> arg0, Response arg1) {
                CustomSQLiteOpenHelper helper = getCustomSQLiteOpenHelper(context);
                SQLiteDatabase database = helper.getWritableDatabase();
                if (arg0 == null || arg0.size() <= 0) {/*Do nothing*/} else {
                    try {
                        for (Job job : arg0) {

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
