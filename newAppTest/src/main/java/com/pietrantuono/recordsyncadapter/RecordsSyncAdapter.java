package com.pietrantuono.recordsyncadapter;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.RetrofitRestServices;
import server.pojos.DevicesList;
import server.pojos.records.Readings;
import server.pojos.records.Sensors;
import server.pojos.records.Test;
import server.pojos.records.TestRecord;
import server.pojos.records.response.Response;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.recordsdb.RecordsProcessor;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.recordsdb.NewRecordsSQLiteOpenHelper;
import com.pietrantuono.recordsdb.RecordsContract;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RecordsSyncAdapter extends AbstractThreadedSyncAdapter {
    private RecordUploader recorduploader;
    private Context context;
    int notificationId = 001;
    private String TAG = "RecordsSyncAdapter";

    public RecordsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;

    }

    @SuppressWarnings("unused")
    public RecordsSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.context = context;

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        recorduploader = new RecordUploader();
        recorduploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d(TAG, "onPerformSync");
    }

    class RecordUploader extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void result) {
//			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon)
//					.setContentTitle("Uploader service is stopped").setContentText("Uploader service is stopped");
//			NotificationManager mNotifyMgr = (NotificationManager) context
//					.getSystemService(Context.NOTIFICATION_SERVICE);
//			mNotifyMgr.notify(mPositiveNotificationId, mBuilder.build());
            Log.d(TAG, "onPostExecute");

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground");
            NewRecordsSQLiteOpenHelper newRecordsSQLiteOpenHelper = NewRecordsSQLiteOpenHelper.getInstance(context);
            String selection = RecordsContract.TestRecords.UPLOADED + " = ?";
            String[] selectionargs = new String[]{"0"};
            Cursor cursor = newRecordsSQLiteOpenHelper.getReadableDatabase().query(RecordsContract.TestRecords.TABLE, null, selection, selectionargs, null, null, null);
            List<TestRecord> records = RecordsProcessor.reconstructRecords(context, cursor, newRecordsSQLiteOpenHelper);
            Iterator<TestRecord> iterator = records.iterator();
            while (iterator.hasNext()) {
                final TestRecord record = iterator.next();

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                        .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                        .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                        .create();
                String recordstring = gson.toJson(record, TestRecord.class);

                Log.d(TAG, "Posting record: " + recordstring);
                retrofit.client.Response response=null;
                try {
                    response= RetrofitRestServices.getRest(context).postResultsSync(PeriCoachTestApplication.getDeviceid(),
                            Long.toString(record.getJobNo()), record);
                } catch(Exception ignored){
                    Log.d(TAG,ignored.toString());
                }
                if (response != null && 200 <=response.getStatus() && response.getStatus()<300) {
                    updateRecordUploaded(record.getID(),newRecordsSQLiteOpenHelper);
                }
            }

            return null;
        }
    }



    private void issuePositiveNotification(TestRecord record) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PeriCoachTestApplication.getContext())
                .setSmallIcon(R.drawable.ok_icon).setContentTitle("PeriCoach, record uploaded")
                .setContentText("Record for job " + record.getJobNo() + " uploaded");
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            mNotifyMgr.notify(notificationId, mBuilder.build());
        } catch (Exception e) {
        }
    }



    public static void updateRecordUploaded(long id, SQLiteOpenHelper helper) {
        ContentValues values = new ContentValues();
        values.put(RecordsContract.TestRecords.UPLOADED, 1);
        String selection=RecordsContract.TestRecords.ID+" = ?";
        String[] selectioArgs= new String[]{""+id};
        helper.getWritableDatabase().update(RecordsContract.TestRecords.TABLE, values, selection,selectioArgs );
    }

}
