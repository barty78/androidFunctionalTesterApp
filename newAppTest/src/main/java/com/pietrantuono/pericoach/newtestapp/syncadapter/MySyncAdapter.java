package com.pietrantuono.pericoach.newtestapp.syncadapter;

import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.RetrofitRestServices;
import server.pojos.records.TestRecord;
import server.pojos.records.response.Response;
import server.utils.MyDatabaseUtils;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.application.PeriCoachTestApplication;
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
	public static final int SLEEP_TIME_IN_SECS = 5;
	int notificationId = 001;
	private String TAG="MySyncAdapter";

	public MySyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;

	}

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
				records = new Select().from(TestRecord.class).execute();
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
				if (!record.isLog()) {
					continue;
				}
				Log.d(TAG, "Posting record: " + recordstring);
				RetrofitRestServices.getRest(context).postResults(PeriCoachTestApplication.getDeviceid(),
						Long.toString(record.getJobNo()), record, new Callback<Response>() {

							@Override
							public void success(Response arg0, retrofit.client.Response arg1) {
								Log.d(TAG, "success: "+arg0.getMessage());
								issuePositiveNotification(record);
								MyDatabaseUtils.deteteRecod(record);
								record.delete();
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
				.setSmallIcon(R.drawable.attention).setContentText("Failed to uplaod " + error.getMessage() == null ? "No cause description" : error.getMessage())
				.setContentTitle("PeriCoach: "+getUnprocessedRecords() + " unprocessed records");

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
				.setContentText("Record " + record.getId() + " uplaoded");
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(notificationId, mBuilder.build());
	}

	private int getUnprocessedRecords(){
		List<Model> records = new Select().from(TestRecord.class).execute();
		return records.size();
	}
}
