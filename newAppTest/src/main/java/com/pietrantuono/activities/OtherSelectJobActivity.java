package com.pietrantuono.activities;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.crashlytics.android.core.CrashlyticsCore;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
//import server.Job;
import server.RetrofitRestServices;
import server.RetrofitRestServices.REST;
import server.pojos.ErrorFromServer;
import server.pojos.Firmware;
import server.pojos.Job;
import server.pojos.Sequence;
import server.pojos.Test;
import utils.DownloadTask;
import utils.MyDialogs;
import utils.DownloadTask.MyCallback;

@SuppressWarnings("ucd")
public class OtherSelectJobActivity extends AppCompatActivity implements MyCallback, JobHolder.Callback {
	private RecyclerView recyclerView;
	private ArrayList<server.pojos.Job> jobsFromServer;
	private ArrayList<server.pojos.Job> jobsFromdb;
	private static server.pojos.Job job;
	private JobAdapter adapter;
	private static DataProvider dataProvider = null;
	private static REST rest = null;
	private DownloadTask task;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) setTitle(getResources().getString(R.string.app_name) + " - DEV BUILD");
		if (!Fabric.isInitialized()) Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
		Intent intent = new Intent("com.pietrantuono.uplaod");
		sendBroadcast(intent);
		setContentView(R.layout.selectjobactivity);
		jobsFromdb = getDataProvider().getJobsFromDB();
		try {
		getJobsFromServer();}
		catch (Exception e){}
		OtherSelectJobActivityHelper.postTestsAndSepsXML(OtherSelectJobActivity.this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		menu.findItem(R.id.restart).setVisible(false);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.settings:
				Intent in = new Intent(OtherSelectJobActivity.this, SettingsActivity.class);
				startActivity(in);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		OrientationUtils.setOrientation(OtherSelectJobActivity.this);
	}

	private void populateList() {
//		removeInactiveJobsFromList(jobsFromServer);
		removeInactiveJobsFromList(jobsFromServer);
		recyclerView = (RecyclerView) findViewById(R.id.recycler);
		if (jobsFromServer != null && jobsFromServer.size()>0)
			adapter = new JobAdapter(jobsFromServer,
					OtherSelectJobActivity.this);
		else if (jobsFromdb != null && jobsFromdb.size()>0)
			adapter = new JobAdapter(jobsFromdb,
					OtherSelectJobActivity.this);
		recyclerView.setLayoutManager(new LinearLayoutManager(OtherSelectJobActivity.this));
		recyclerView.setAdapter(adapter);
		registerForContextMenu(recyclerView);

	}

	private void removeInactiveJobsFromList(ArrayList<Job> jobsList) {
		Iterator<Job> jobIterator=jobsList.iterator();
		while(jobIterator.hasNext()){
			if(jobIterator.next().getActive()!=0)jobIterator.remove();
		}
	}

	private void getJobsFromServer() {
		MyDialogs.showIndeterminateProgress(OtherSelectJobActivity.this,
				"Downloading jobs list", "Please wait...");
		getRest().getJobListActiveJobs( PeriCoachTestApplication.getDeviceid(), new Callback<List<Job>>() {
			@Override
			public void success(List<Job> arg0, Response arg1) {
				MyDialogs.dismissProgress();
				if (arg0 == null || arg0.size() <= 0) {
					showAlert("Empty list");
					jobsFromServer = null;
					//populateList();
					//getFirmwareListFromServer();
				} else {
					jobsFromServer = new ArrayList<server.pojos.Job>();
					jobsFromServer.addAll(arg0);
					for (int i = 0; i < jobsFromServer.size(); i++) {
						Log.d("JOB", jobsFromServer.get(i).getId() + " | " + jobsFromServer.get(i).getJobno());
					}
					populateList();
//					getFirmwareListFromServer();
					compareJobLists();

				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				MyDialogs.dismissProgress();
				ErrorFromServer error =null;
				try {error=(ErrorFromServer) arg0.getBodyAs(ErrorFromServer.class);	}
				catch (Exception e){}
				String message = arg0.getMessage() == null ? "" : arg0
						.getMessage();
				if(error!=null)message=error.getMessage()!=null?error.getMessage():message;
				showAlert(message);
				jobsFromServer = null;
				//populateList();
				//getFirmwareListFromServer();

			}
		});
	}

	private void compareJobLists() {

		if (jobsFromServer != null && jobsFromdb != null)
			for (int i = 0; i < jobsFromServer.size(); i++) {
				if (!jobsFromdb.contains(jobsFromServer.get(i)))
					addJobToDB(jobsFromServer.get(i));
			}
		if (jobsFromdb == null || jobsFromdb.size() <= 0) {
			for (int i = 0; i < jobsFromServer.size(); i++) {
				addJobToDB(jobsFromServer.get(i));
			}
		}
		if (jobsFromdb != null)
			for (int i = 0; i < jobsFromdb.size(); i++) {
				if (!jobsFromServer.contains(jobsFromdb.get(i)))
					removeJobFromDB(jobsFromdb.get(i));
			}
	}

	private void removeJobFromDB(Job job) {
		getDataProvider().removeJobFromDB(job);
	}

	private void addJobToDB(Job job) {
		getDataProvider().addJobToDB(job);

	}

	private DataProvider getDataProvider() {
		if (dataProvider != null)
			return dataProvider;
		else {
			dataProvider = new DataProvider(OtherSelectJobActivity.this);
			return dataProvider;
		}
	}

	private REST getRest() {
		if (rest != null)
			return rest;
		else {
			rest = RetrofitRestServices.getRest(OtherSelectJobActivity.this);
			return rest;

		}
	}

	public void getFirmwareListFromServer(long firmwareid) {
		MyDialogs.showIndeterminateProgress(OtherSelectJobActivity.this,
				"Downloading firmware list", "Please wait...");
		getRest().getFirmware(PeriCoachTestApplication.getDeviceid(),
				String.valueOf(firmwareid), new Callback<Firmware>() {
			@Override
			public void success(Firmware arg0, Response arg1) {
				MyDialogs.dismissProgress();
				if (arg0 == null ) {
					MyDialogs.showAlert(OtherSelectJobActivity.this,
							"Error Downloading", "Empty list");
				} else {
					PeriCoachTestApplication.setGetFirmware(arg0);
					downloadFirmware();
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				MyDialogs.dismissProgress();
				String message = arg0.getMessage() == null ? "" : arg0
						.getMessage();
				MyDialogs.showAlert(OtherSelectJobActivity.this,
						"Error Downloading", message);

			}
		});
	}

	private void downloadFirmware() {
		String url = PeriCoachTestApplication.getGetFirmware().getUrl();
		String filename = url.substring(url.lastIndexOf("/") + 1, url.length());
		task = new DownloadTask(OtherSelectJobActivity.this, url, filename);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onDownloadFileSuccess() {

//		firmwarefilepresent = true;
		if (job.getTestId() == 999) {		// Special job type 999 bypasses server defined sequence, uses internal one instead
			startMainActivity(job);
			return;
		}
		downloadSequence(job);
		//startMainActivity(job);
	}

	@Override
	public void onDownloadFileFailure() {
		Toast.makeText(OtherSelectJobActivity.this,
				"Firmware file not present", Toast.LENGTH_LONG)
				.show();
//		firmwarefilepresent = false;
	}

	private void downloadSequence(final server.pojos.Job job) {
		MyDialogs.showIndeterminateProgress(OtherSelectJobActivity.this,
				"Downloading sequence", "Please wait...");
		getRest().getSequence(PeriCoachTestApplication.getDeviceid(),job.getJobno(), new Callback<List<Test>>() {

			@Override
			public void success(List<Test> arg0, Response arg1) {
				MyDialogs.dismissProgress();
				if (arg0 == null || arg0.size() <= 0) {
					MyDialogs.showAlert(OtherSelectJobActivity.this,
							"Error Downloading", "Empty TEST Sequence List");
				} else {
					Sequence sequence= new Sequence();
					sequence.setTests(arg0);
					sequence.setLog(job.getIslogging() == 1);
					PeriCoachTestApplication.setSequence(sequence);
					startMainActivity(job);

				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				MyDialogs.dismissProgress();
				String message = arg0.getMessage() == null ? "" : arg0
						.getMessage();
				MyDialogs.showAlert(OtherSelectJobActivity.this,
						"Error Downloading", message);
			}
		});

	}

	private void startMainActivity(Job job) {
		Intent intent = new Intent(OtherSelectJobActivity.this,
				MainActivity.class);
		intent.putExtra(MainActivity.JOB,job);
		startActivity(intent);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		OtherSelectJobActivity.this.finish();
	}

	@Override
	protected void onDestroy() {
		if (task != null && !task.isCancelled())
			task.cancel(true);
		PeriCoachTestApplication.forceSync();
		PeriCoachTestApplication.getApplication().forceSyncDevices();
		super.onDestroy();
	}

	
	private void showAlert(String message){
		if(message.toLowerCase(Locale.US).contains( ("Fixture Not Valid").toLowerCase())){
			showAlertWrongFixture();
			return;
		}
		AlertDialog.Builder builder= new Builder(OtherSelectJobActivity.this);
		builder.setTitle("Error downloading jobs list");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {/* NOOP*/}
		});
        try {
            builder.create().show();
        }catch (Exception ingored){}
	}

	private void showAlertWrongFixture() {
		String message ="Your fixture ID is: "+PeriCoachTestApplication.getDeviceid();
		AlertDialog.Builder builder= new Builder(OtherSelectJobActivity.this);
		builder.setTitle("Fixture Not Valid");
		builder.setMessage(message);
		builder.setPositiveButton("Send fixture ID to support", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {sendDeviceID();}
		});
		builder.setNegativeButton("CLOSE", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {/* NOOP*/}
		});
		builder.create().show();
	}

	private void sendDeviceID() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"pbartlett@analyticamedical.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Fixture ID to register");
		i.putExtra(Intent.EXTRA_TEXT   , "Fixture ID to register: "+ PeriCoachTestApplication.getDeviceid());
		try {
		    startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(OtherSelectJobActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}		
	}


	@Override
	public void setJob(Job job) {
		OtherSelectJobActivity.job=job;
	}
}
