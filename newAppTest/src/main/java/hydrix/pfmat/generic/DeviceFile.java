package hydrix.pfmat.generic;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;

import com.pietrantuono.pericoachengineering.classes.OpenTestResult;

import analytica.pericoach.android.ClosedTestResult;
import analytica.pericoach.android.DBManager;
import analytica.pericoach.android.DataManager;
import analytica.pericoach.android.DeviceList;
import analytica.pericoach.android.Job;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import hydrix.pfmat.generic.Force;

public class DeviceFile
{
	Context context;

	// Constants
	final char DELIMITER = ',';
	

	// Members
	protected BufferedWriter mFile = null;
	
	protected DBManager db;
	
	public Job job;
	
	public Integer mLastReportNumber = 0;
	
	public DeviceFile(Context context)
	{
		this.context = context;
		
	}
	
	public Long getSize(String directory, String jobNo) {
		
		String filename = jobNo + ".csv";
		
		File file = new File(directory, filename);
		
		//Log.d("LENGTH:", String.valueOf(file.length()));
		return file.length();
	}
	
	public void delete(Integer testType, String jobNo) {
		
		// Make sure we close the file prior to deletion
		close ();
		
		String filename = "";
		
		switch (testType) {
		case TEST.OPEN_TEST:
			break;
		case TEST.CLOSED_TEST:
			filename = "ClosedTest - " + jobNo + "_" + job.getLastReportNumber() + ".csv";
			break;
		
		}
		File file = new File(DataManager.getPFMATDataDirectory(), filename);
		
		try {
			Log.d("DELETE:", filename);
			Boolean ok = file.delete();
			if (ok) {
				Log.d("CSV: ", "File deleted ok");
			}			
		} catch (Exception e) {
			
		}

	}
	
	public final boolean create(String directory, String jobNo)
	{
		// Make sure we close any previous file if this object is being reused
		close();
		
		// Params must all be supplied and non-null
		if (directory == null || jobNo == null)
			return false;
		
		// Generate filename in the format <jobNo>.csv
		
		String filename = jobNo + ".csv";
		Log.d("Filename:", filename);
		
		// Form full path
		//String fullPath = directory + '/' + filename;
		try 
		{
			// Create new file
			File file = new File(directory, filename);
			if(!file.exists())
			{
				if (!file.createNewFile()) {
					return false;
				} //else if (!writeStaticHeader()){
					//return false;
	
				//}
										
			}
			
			// Create a buffered write stream for it
			FileWriter writer = new FileWriter(file, true);
			if (writer != null)
				mFile = new BufferedWriter(writer);
		}
		catch (IOException e)
		{
	    	e.printStackTrace();
		}
		if (mFile == null)
			return false;
		
		// Open and ready to write data records
		return true;
	}
	
	public final void close()
	{
		if (mFile != null)
		{
			try
			{
				// Flush any remaining write then close the file
				mFile.flush();
				mFile.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			mFile = null;
		}
	}
	
	/*
	public final boolean writeStaticHeader()
	{
		String line = "Time/Date" + DELIMITER +
					  "Device ID" + DELIMITER +
					  "Firmware Ver" + DELIMITER +
					  "Sensor ID" + DELIMITER +
					  "S0 Cal" + DELIMITER +
					  "S1 Cal" + DELIMITER +
					  "S2 Cal" + DELIMITER;
		for (int i = 0 ; i < testForces.size() ; i++) {
			line = line + DELIMITER + Integer.toString(testForces.get(i).getLiteralSensor0()) + DELIMITER + Integer.toString(testForces.get(i).getLiteralSensor1()) + DELIMITER + Integer.toString(testForces.get(i).getLiteralSensor2());

		
		
		if (!writeLine(line))
			return false;
		
		return true;
	}*/
	
	public boolean writeJobResultstoFile(Integer testType, String jobNo) {
		
		ArrayList<ClosedTestResult> resultList = new ArrayList<ClosedTestResult>();
		ArrayList<DeviceList> deviceList = new ArrayList<DeviceList>();
		job = new Job();

		db = (new DBManager(this.context));
		// Get current job details
		job = db.getJobDetails(jobNo);
		Log.d("JOB: ", "Last record is " + Integer.toString(job.getLastReportedRecord()));
		
		//Log.d("REPORT#: ", Integer.toString(job.getLastReportNumber()));
		//Gets job results after mLastReportNumber
		resultList = db.getAllResultsforJob(job);
		Log.d("RESULTS#: ", Integer.toString(resultList.size()));
		
		deviceList = db.getAllTestedDevicesforJob(job);
		//Log.d("DEVICES$:", Integer.toString(deviceList.size()));
		
		switch (testType) {
		case TEST.OPEN_TEST:
			if (resultList.size() > 0){
				ArrayList<OpenTestResult> openResultList  = new ArrayList<OpenTestResult>();
				openResultList =db.getAllOpenResultsforJob(job);
				mLastReportNumber = job.getLastReportNumber();
				String filename = "OpenTest - " + jobNo + "_" + Integer.toString(mLastReportNumber);
				if (!create(DataManager.getPFMATDataDirectory(),  filename))
					return false;
				
				if (getSize(DataManager.getPFMATDataDirectory(), filename) == 0) 
					writeOpenHeader();
				
				for (int i = 0; i < openResultList.size(); i++) {
					writeOpenTestResult(deviceList.get(openResultList.get(i).getDevId() - 1), openResultList.get(i));
				}				
			} else {
				//Toast.makeText(context, "No new results to report", 50000).show();
				return false;
			}
			break;
		case TEST.NEW_OPEN_TEST:
			if (resultList.size() > 0){
				ArrayList<OpenTestResult> openResultList  = new ArrayList<OpenTestResult>();
				openResultList =db.getAllOpenResultsforJob(job);
				mLastReportNumber = job.getLastReportNumber();
				String filename = "OpenTest - " + jobNo + "_" + Integer.toString(mLastReportNumber);
				if (!create(DataManager.getPFMATDataDirectory(),  filename))
					return false;
				
				if (getSize(DataManager.getPFMATDataDirectory(), filename) == 0) 
					writeOpenHeader();
				
				for (int i = 0; i < openResultList.size(); i++) {
					writeOpenTestResult(deviceList.get(openResultList.get(i).getDevId() - 1), openResultList.get(i));
				}				
			} else {
				//Toast.makeText(context, "No new results to report", 50000).show();
				return false;
			}
			break;
		case TEST.CLOSED_TEST:
			// Check if we got any results returned, SQL query 
			if (resultList.size() > 0){
				mLastReportNumber = job.getLastReportNumber();
				String filename = "ClosedTest - " + jobNo + "_" + Integer.toString(mLastReportNumber);
				if (!create(DataManager.getPFMATDataDirectory(),  filename))
					return false;
				
				if (getSize(DataManager.getPFMATDataDirectory(), filename) == 0) 
					writeClosedHeader();
				
				for (int i = 0; i < resultList.size(); i++) {
					Log.d("RECORD " + Integer.toString(i) + ": ", resultList.get(i).toString());
					writeClosedTestResult(deviceList.get(resultList.get(i).getDevId() - 1), resultList.get(i));
				}				
			} else {
				//Toast.makeText(context, "No new results to report", 50000).show();
				return false;
			}
			break;
		case TEST.NEW_CLOSED_TEST:
		case TEST.EXPERIMENTAL_CLOSED_TEST:
			// Check if we got any results returned, SQL query 
			if (resultList.size() > 0){
				mLastReportNumber = job.getLastReportNumber();
				String filename = "ClosedTest - " + jobNo + "_" + Integer.toString(mLastReportNumber);
				if (!create(DataManager.getPFMATDataDirectory(),  filename))
					return false;
				
				if (getSize(DataManager.getPFMATDataDirectory(), filename) == 0) 
					writeClosedHeader();
				
				for (int i = 0; i < resultList.size(); i++) {
					Log.d("RECORD " + Integer.toString(i) + ": ", resultList.get(i).toString());
					writeClosedTestResult(deviceList.get(resultList.get(i).getDevId() - 1), resultList.get(i));
				}				
			} else {
				//Toast.makeText(context, "No new results to report", 50000).show();
				return false;
			}
			break;
		case TEST.INTEGRATED_OPEN_TEST:
		case TEST.DYNAMIC_TEST:
			// Check if we got any results returned, SQL query 
			if (resultList.size() > 0){
				ArrayList<OpenTestResult> openResultList  = new ArrayList<OpenTestResult>();
				openResultList =db.getAllOpenResultsforJob(job);
				mLastReportNumber = job.getLastReportNumber();
				String filename = "OpenTest - " + jobNo + "_" + Integer.toString(mLastReportNumber);
				if (!create(DataManager.getPFMATDataDirectory(),  filename))
					return false;
				
				if (getSize(DataManager.getPFMATDataDirectory(), filename) == 0) 
					writeOpenHeader();
				
				for (int i = 0; i < openResultList.size(); i++) {
					writeOpenTestResult(deviceList.get(openResultList.get(i).getDevId() - 1), openResultList.get(i));
				}				
			} else {
				//Toast.makeText(context, "No new results to report", 50000).show();
				return false;
			}
		}
		
		// Update job table with last test record id so we know where to start for next report.
		//job.setLastReportedRecord(resultList.get(resultList.size() - 1).getResultRecordID());
		job.setLastReportNumber(job.getLastReportNumber() + 1);
		Log.d("RES SIZE: ", Integer.toString(resultList.size()));
		job.setLastReportedRecord(resultList.get(resultList.size() - 1).getResultRecordID());
		db.updateJob(job);
		
		return true;
	}
	
	public final boolean writeClosedHeader()
	{
		
		// Write the header field names
		String line = "DATE/TIME" + DELIMITER +
				  "DeviceId" + DELIMITER +
				  "FW Version" + DELIMITER +
				  "S0Min-Zero" + DELIMITER +
  				  "S0Max-Zero" + DELIMITER +
  				  "S0Avg-Zero" + DELIMITER +
  				  "S1Min-Zero" + DELIMITER +
  				  "S1Max-Zero" + DELIMITER +
  				  "S1Avg-Zero" + DELIMITER +
  				  "S2Min-Zero" + DELIMITER +
  				  "S2Max-Zero" + DELIMITER +
  				  "S2Avg-Zero" + DELIMITER +
				  "S0Min-Weight" + DELIMITER +
				  "S0Max-Weight" + DELIMITER +
				  "S0Avg-Weight" + DELIMITER +
				  "S1Min-Weight" + DELIMITER +
				  "S1Max-Weight" + DELIMITER +
				  "S1Avg-Weight" + DELIMITER +
				  "S2Min-Weight" + DELIMITER +
				  "S2Max-Weight" + DELIMITER +
				  "S2Avg-Weight" + DELIMITER +
				  "RESULT" + DELIMITER +
				  "OPERATOR";
		Log.d("LINE:", line);
		if (!writeLine(line))
			return false;
		
		return true;
	}
	
	public final boolean writeOpenHeader()
{
		
		// Write the header field names
		String line = "DATE/TIME" + DELIMITER +
				  "DeviceId" + DELIMITER +
				  "FW Version" + DELIMITER +
				  "S0Min-Zero" + DELIMITER +
  				  "S0Max-Zero" + DELIMITER +
  				  "S0Avg-Zero" + DELIMITER +
  				  "S1Min-Zero" + DELIMITER +
  				  "S1Max-Zero" + DELIMITER +
  				  "S1Avg-Zero" + DELIMITER +
  				  "S2Min-Zero" + DELIMITER +
  				  "S2Max-Zero" + DELIMITER +
  				  "S2Avg-Zero" + DELIMITER +
				  "S0Min-Weight" + DELIMITER +
				  "S0Max-Weight" + DELIMITER +
				  "S0Avg-Weight" + DELIMITER +
				  "S1Min-Weight" + DELIMITER +
				  "S1Max-Weight" + DELIMITER +
				  "S1Avg-Weight" + DELIMITER +
				  "S2Min-Weight" + DELIMITER +
				  "S2Max-Weight" + DELIMITER +
				  "S2Avg-Weight" + DELIMITER +
				  "RESULT" + DELIMITER +
				  "BARCODE" +  DELIMITER +
				  "OPERATOR";
		Log.d("LINE:", line);
		if (!writeLine(line))
			return false;
		
		return true;
	}
	
	public final boolean writeClosedTestResult(DeviceList device, ClosedTestResult testResult) {
	
		String line = testResult.getDate() + DELIMITER + 
					device.getDevId() + DELIMITER +
					device.getFirmwareVersion();
		for (int i = 0; i < 2; i++){
			for (int j = 0; j < 3; j++) {
				line = line + DELIMITER + testResult.getSamples().getMinSamples().get(i).getLiteralSensor(j) +
							  DELIMITER + testResult.getSamples().getMaxSamples().get(i).getLiteralSensor(j) +
							  DELIMITER + testResult.getSamples().getAvgSamples().get(i).getLiteralSensor(j);
			}	
		}
		
		
		line = line + DELIMITER + testResult.getResult().toString() +
					DELIMITER + testResult.getOperator();
		
		Log.d("LINE:", line);
		return writeLine(line);
	}
	public final boolean writeOpenTestResult(DeviceList device, OpenTestResult testResult) {
		
		String line = testResult.getDate() + DELIMITER + 
					device.getDevId() + DELIMITER +
					device.getFirmwareVersion();
		for (int i = 0; i < 2; i++){
			for (int j = 0; j < 3; j++) {
				line = line + DELIMITER + testResult.getSamples().getMinSamples().get(i).getLiteralSensor(j) +
							  DELIMITER + testResult.getSamples().getMaxSamples().get(i).getLiteralSensor(j) +
							  DELIMITER + testResult.getSamples().getAvgSamples().get(i).getLiteralSensor(j);
			}	
		}
		
		
		line = line + DELIMITER + testResult.getResult().toString()+ 
				DELIMITER + testResult.getBarcode() +
					DELIMITER + testResult.getOperator() ;
		
		Log.d("LINE:", line);
		return writeLine(line);
	}
	
	
	public final boolean writeOpenTestResult(ClosedTestResult testResult) {
		
		
		return true;
	}
	
	public final boolean writeDeviceClosedTest(String date, String deviceID, String firmwareVer, String sensorVal, String sensCo0, String sensCo1, String sensCo2, TestSamples mTestSamples, String mResult)
	{
		
		String line = date + DELIMITER +
					  deviceID + DELIMITER +
					  firmwareVer + DELIMITER +
					  sensorVal + DELIMITER +
					  sensCo0 + DELIMITER +
					  sensCo1 + DELIMITER +
					  sensCo2;
		
		for (int i = 0; i < 3; i++) {
		
				line = line + DELIMITER + mTestSamples.getMinSamples().get(i).getLiteralSensor(i) +
							  DELIMITER + mTestSamples.getMaxSamples().get(i).getLiteralSensor(i) +
							  DELIMITER + mTestSamples.getAvgSamples().get(i).getLiteralSensor(i);
				
		}
		
		line = line + DELIMITER + mResult;
		
		return writeLine(line);
	}
	
	public final boolean writeDeviceOpenTest(String date, String barCode, String deviceID, String firmwareVer, String sensCo0, String sensCo1, String sensCo2, TestSamples mTestSamples, String mResult)
	{
		
		String line = date + DELIMITER +
					  barCode + DELIMITER +
					  deviceID + DELIMITER +
					  firmwareVer + DELIMITER +
					  sensCo0 + DELIMITER +
					  sensCo1 + DELIMITER +
					  sensCo2;
		
		for (int i = 0 ; i < mTestSamples.getMinSamples().size() ; i++) {
			line = line + DELIMITER + mTestSamples.getMinSamples().get(i).getLiteralSensor0() +
						  DELIMITER + mTestSamples.getMaxSamples().get(i).getLiteralSensor0() +
						  DELIMITER + mTestSamples.getAvgSamples().get(i).getLiteralSensor0() +
						  DELIMITER + mTestSamples.getMinSamples().get(i).getLiteralSensor1() +
						  DELIMITER + mTestSamples.getMaxSamples().get(i).getLiteralSensor1() +
						  DELIMITER + mTestSamples.getAvgSamples().get(i).getLiteralSensor1() +
						  DELIMITER + mTestSamples.getMinSamples().get(i).getLiteralSensor2() +
						  DELIMITER + mTestSamples.getMaxSamples().get(i).getLiteralSensor2() +
						  DELIMITER + mTestSamples.getAvgSamples().get(i).getLiteralSensor2();
						  
			/*
					minTestForces.getSamples().get(i).mForce.getLiteralSensor0() + 
					DELIMITER + maxTestForces.getSamples().get(i).mForce.getLiteralSensor0() + 
					DELIMITER + avgTestForces.getSamples().get(i).mForce.getLiteralSensor0() + 
					DELIMITER + minTestForces.getSamples().get(i).mForce.getLiteralSensor1() + 
					DELIMITER + maxTestForces.getSamples().get(i).mForce.getLiteralSensor1() + 
					DELIMITER + avgTestForces.getSamples().get(i).mForce.getLiteralSensor1() + 
					DELIMITER + minTestForces.getSamples().get(i).mForce.getLiteralSensor2() + 
					DELIMITER + maxTestForces.getSamples().get(i).mForce.getLiteralSensor2() + 
					DELIMITER + avgTestForces.getSamples().get(i).mForce.getLiteralSensor2();
					
					*/
		}
		
		line = line + DELIMITER + mResult;
		
		return writeLine(line);
	}
	
	public final boolean writeForce(String sensor)
	{
		
		String line = sensor + DELIMITER;
		return writeLine(line);
	}
	
	
	public final boolean writeForces(String sensor0, String sensor1, String sensor2)
	{
		
		String line = sensor0 + DELIMITER +
					  sensor1 + DELIMITER +
					  sensor2 + DELIMITER;
		return writeLine(line);
	}
	
	public final boolean writeNewLine()
	{
		
		return writeLineEnd();
	}
	
	protected final boolean writeLine(String line)
	{
		if (mFile == null)
			return false;
		try
		{
			mFile.append(line);
			mFile.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected final boolean writeLineEnd()
	{
		if (mFile == null)
			return false;
		try
		{
			mFile.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public Integer getLastReport() {
		return mLastReportNumber;
	}

}

