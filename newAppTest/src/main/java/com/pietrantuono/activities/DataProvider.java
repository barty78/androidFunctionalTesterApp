package com.pietrantuono.activities;

import hydrix.pfmat.generic.TEST;

import java.util.ArrayList;

import analytica.pericoach.android.DBManager;
import android.app.Activity;
import android.content.Context;

class DataProvider {
	private Context context;

	DataProvider(Activity activity) {
		this.context=(Context)activity;
		
	}
	void addJobToDB(server.pojos.Job jobToBeInserted) {
		DBManager dbManager= new DBManager(context);
		analytica.pericoach.android.Job job = new analytica.pericoach.android.Job();
		job.setJobNo(jobToBeInserted.getJobno());
		job.setTestID((TEST.OPEN_TEST));//TODO please note
		job.setTotalQty((int) (jobToBeInserted.getQuantity()));
		job.setId(jobToBeInserted.getId());
		//job.setJobNo("11");
		//job.setTestID(1);
		//job.setTotalQty(3);
		dbManager.insertJob(job);
		
	}
	void removeJobFromDB(server.pojos.Job jobToBeClosed) {
		DBManager dbManager = new DBManager(context); 
		dbManager.closeJob(jobToBeClosed.getJobno());
	}
	
	public ArrayList<server.pojos.Job> getJobsFromDB() {	
		ArrayList<server.pojos.Job> jobsFromdb=null;
		DBManager db = (new DBManager(context));
		ArrayList<analytica.pericoach.android.Job> jobs= new ArrayList<analytica.pericoach.android.Job>();
		jobs.addAll(db.getAllActiveJobsForTest(TEST.OPEN_TEST));
		if(jobs!=null && jobs.size()>0){
			jobsFromdb= new ArrayList<server.pojos.Job>();
			for(int i=0;i<jobs.size();i++){
				server.pojos.Job job= new server.pojos.Job();
				if(jobs.get(i).getJobNo()==null)continue;
				else {job.setJobno(jobs.get(i).getJobNo());}
				job.setId(jobs.get(i).getId());
				
				job.setQuantity(jobs.get(i).getTotalQty());
				jobsFromdb.add(job);
			}
		}
		else {jobsFromdb=null;}
		return jobsFromdb;
		
	}
	
}


