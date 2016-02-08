package com.pietrantuono.recordsyncadapeter;

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
import server.pojos.records.TestRecord;
import server.pojos.records.response.Response;
import server.service.ServiceDBHelper;
import server.utils.MyDatabaseUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MySyncAdapter extends AbstractThreadedSyncAdapter {
	private MyUploader myuploader;
	private Context context;
	int notificationId = 001;
	private String TAG="MySyncAdapter";

	public MySyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;

	}

	@SuppressWarnings("unused")
	public MySyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		this.context = context;

	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
			SyncResult syncResult) {
		myuploader = new MyUploader();
		myuploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		Log.d(TAG, "onPerformSync");

		RetrofitRestServices.getRest(context).getLastDevicesAsync(PeriCoachTestApplication.getDeviceid(), "" + ServiceDBHelper.getMaxDeviceID(), new Callback<DevicesList>() {
			@Override
			public void success(DevicesList arg0, retrofit.client.Response arg1) {
				if (arg0 != null) ServiceDBHelper.addDevices(arg0);
			}

			@Override
			public void failure(RetrofitError arg0) {
			}
		});

		return;
	}

	class MyUploader extends AsyncTask<Void, Void, Void> {

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
			List<TestRecord> records = null;
			try {
				records = new Select().from(TestRecord.class).where("uploaded = ?",false).execute();
			} catch (Exception e) {
			}
			if (records == null || records.size() <= 0) {
				return null;
			}
			Iterator<TestRecord> iterator = records.iterator();
			while (iterator.hasNext()) {
				final TestRecord record = iterator.next();
				MyDatabaseUtils.RecontructRecord(record);
				Gson gson = new GsonBuilder()
					        .excludeFieldsWithoutExposeAnnotation()
					        .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
					        .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
					        .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
					        .create();
					String recordstring=gson.toJson(record, TestRecord.class);

				Log.d(TAG, "Posting record: " + recordstring);
				RetrofitRestServices.getRest(context).postResults(PeriCoachTestApplication.getDeviceid(),
						Long.toString(record.getJobNo()), record, new Callback<Response>() {

							@Override
							public void success(Response arg0, retrofit.client.Response arg1) {
								Log.d(TAG, "success: "+arg0.getMessage());
								issuePositiveNotification(record);
								record.setUploaded(true);
								//MyDatabaseUtils.deteteRecod(record);
								try {
									// Get a file channel for the file
									File dbFile = context.getDatabasePath("containsmac");

									//File file = new File(dbFile);
									FileChannel channel = new RandomAccessFile(dbFile, "rw").getChannel();

									// Use the file channel to create a lock on the file.
									// This method blocks until it can retrieve the lock.
									FileLock lock = channel.lock();
									ActiveAndroid.beginTransaction();
									try{
										record.save();
										ActiveAndroid.setTransactionSuccessful();
										if(BuildConfig.DEBUG)Log.d(TAG,"setTransactionSuccessful");
									}
									catch (Exception e){
										if(BuildConfig.DEBUG)Log.e(TAG,e.toString());
									}
									finally{
										ActiveAndroid.endTransaction();
									}
									// Release the lock
									lock.release();
									// Close the file
									channel.close();
								} catch (Exception e) {
									e.printStackTrace();
								}

							}

							@Override
							public void failure(RetrofitError arg0) {
								Log.d(TAG, "failure");
								issueNegativeNotification(record, arg0);

							}

						});
			}

			return null;
		}
	}

	private void issueNegativeNotification(TestRecord record, RetrofitError error) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PeriCoachTestApplication.getContext())
				.setSmallIcon(R.drawable.attention).setContentText("Failed to upload " + error.getMessage() == null ? "No cause description" : error.getMessage())
				.setContentTitle("PeriCoach: " + getUnprocessedRecords() + " unprocessed records");

		Intent intent=new Intent(context,StartSyncAdapterService.class);
		PendingIntent pIntent=PendingIntent.getService(context, (int) System.currentTimeMillis(), intent, 0);
		mBuilder.addAction(R.drawable.ic_av_replay, "Retry sync", pIntent);
		mBuilder.setContentIntent(pIntent);
		mBuilder.setPriority(Notification.PRIORITY_MAX);
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(notificationId, mBuilder.build());
	}

	private void issuePositiveNotification(TestRecord record) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PeriCoachTestApplication.getContext())
				.setSmallIcon(R.drawable.ok_icon).setContentTitle("PeriCoach, record uploaded")
				.setContentText("Record " + record.getId() + " uploaded");
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		try {mNotifyMgr.notify(notificationId, mBuilder.build());}
		catch (Exception e){}
	}

	private int getUnprocessedRecords(){
		List<Model> records = new Select().from(TestRecord.class).where("uploaded = ?", false).execute();
		return records.size();
	}
}
