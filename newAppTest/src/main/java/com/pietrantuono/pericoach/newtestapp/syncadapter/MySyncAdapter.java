package com.pietrantuono.pericoach.newtestapp.syncadapter;

import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import server.RetrofitRestServices;
import server.pojos.records.TestRecord;
import server.pojos.records.response.Response;
import server.utils.MyDatabaseUtils;

import com.activeandroid.query.Select;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class MySyncAdapter extends AbstractThreadedSyncAdapter {
	private MyUploader myuploader;
	private Context context;
	public static final int SLEEP_TIME_IN_SECS = 5;
	int mNotificationId = 001;
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
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon)
				.setContentTitle("Uploader service is running").setContentText("Uploader service is running");
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify(mNotificationId, mBuilder.build());
		myuploader = new MyUploader();
		myuploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		Log.d(TAG, "onPerformSync");

		return;
	}

	class MyUploader extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.icon)
					.setContentTitle("Uploader service is stopped").setContentText("Uploader service is stopped");
			NotificationManager mNotifyMgr = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
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
				if (!record.isLog()) {
					continue;
				}
				Log.d(TAG, "postResults");
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
				.setSmallIcon(R.drawable.attention).setContentTitle("Failed to uplaod " + record.getId())
				.setContentText(error.getMessage() == null ? "No cause description" : error.getMessage());
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify((int) (long) record.getId(), mBuilder.build());
	}

	private void issuePositiveNotification(TestRecord record) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(PeriCoachTestApplication.getContext())
				.setSmallIcon(R.drawable.attention).setContentTitle("Record " + record.getId() + " uplaoded")
				.setContentText("Record " + record.getId() + " uplaoded");
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifyMgr.notify((int) (long) record.getId(), mBuilder.build());
	}
}
