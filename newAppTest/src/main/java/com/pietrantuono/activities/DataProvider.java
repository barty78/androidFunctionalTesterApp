package com.pietrantuono.activities;

import java.util.ArrayList;

import analytica.pericoach.android.DBManager;
import server.pojos.Job;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

class DataProvider {
	private final Context context;
	private final String TAG = getClass().getSimpleName();

	DataProvider(Activity activity) {
		this.context=(Context)activity;
		
	}
	void addJobToDB(server.pojos.Job jobToBeInserted) {
		DBManager dbManager= new DBManager(context);
		analytica.pericoach.android.Job job = new analytica.pericoach.android.Job();
		job.setJobNo(jobToBeInserted.getJobno());
		job.setTestID((int) jobToBeInserted.getTestId());
		job.setTotalQty((int) (jobToBeInserted.getQuantity()));
		job.setId(jobToBeInserted.getId());
		job.setActive((int) jobToBeInserted.getActive());
		job.setDisconnectPowerState(jobToBeInserted.getDisconnectPowerState());
		job.setSetSensorTestFlag(jobToBeInserted.getSetSensorTestFlag());
		job.setStage_dep(jobToBeInserted.getStage_dep());
		//job.setJobNo("11");
		//job.setTestID(1);
		//job.setTotalQty(3);
		dbManager.insertJob(job);
		
	}
	void removeJobFromDB(server.pojos.Job jobToBeClosed) {
		DBManager dbManager = new DBManager(context); 
		dbManager.closeJob(jobToBeClosed.getJobno());
	}

	void updateJobOnDB(server.pojos.Job jobToBeUpdated) {
		DBManager dbManager = new DBManager(context);
		dbManager.updateJob(jobToBeUpdated);
	}

	public analytica.pericoach.android.Job getJobFromDB(String jobNo) {
		DBManager dbManager = new DBManager(context);
		return dbManager.getJob(jobNo);
	}
	
	public ArrayList<Job> getJobsFromDB() {
		ArrayList<Job> jobsFromdb;
		DBManager db = (new DBManager(context));
		ArrayList<analytica.pericoach.android.Job> jobs= new ArrayList<analytica.pericoach.android.Job>();
		jobs.addAll(db.getAllActiveJobsForTest());
		if(jobs!=null && jobs.size()>0){
			jobsFromdb= new ArrayList<Job>();
			for (analytica.pericoach.android.Job job : jobs) {
				Log.d(TAG + " DB_JOB", job.toString());
				server.pojos.Job tmpjob = new server.pojos.Job();
				if(job.getJobNo()==null)continue;
				else {tmpjob.setJobno(job.getJobNo());}
				tmpjob.setId(job.getId());
				job.setTotalQty(job.getTotalQty());
				jobsFromdb.add(tmpjob);
			}
		}
		else {jobsFromdb=null;}
		return jobsFromdb;
		
	}
	
}


