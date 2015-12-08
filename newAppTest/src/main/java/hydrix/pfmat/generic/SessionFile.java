package hydrix.pfmat.generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SessionFile
{
	// Constants
	final char DELIMITER = ',';
	
	// Members
	protected BufferedWriter mFile = null;
	
	public final boolean create(String directory, String jobNo, Date timestamp)
	{
		// Make sure we close any previous file if this object is being reused
		close();
		
		// Params must all be supplied and non-null
		if (directory == null || jobNo == null || timestamp == null)
			return false;
		
		// Generate filename in the format <username>-YYYY-MM-DD-HH-MM-SS.csv
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String filename = jobNo + "-" + formatter.format(timestamp) + ".csv";
		
		// Form full path
		//String fullPath = directory + '/' + filename;
		try 
		{
			// Create new file
			File file = new File(directory, filename);
			if (!file.createNewFile())
				return false;
			
			// Create a buffered write stream for it
			FileWriter writer = new FileWriter(file);
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
	
	public final boolean writeHeader(String username, Date timestamp, String deviceID, String programmeName, Force userBaseline, Force userMaxForce)
	{
		// Make sure all data is supplied
		if (username == null || timestamp == null || deviceID == null || userBaseline == null || userMaxForce == null)
			return false;
		
		// Write the header field names
		String line = "Username" + DELIMITER +
					  "SessionTime" + DELIMITER +
					  "DeviceId" + DELIMITER +
					  "Programme" + DELIMITER +
					  "Sensor0Base" + DELIMITER +
					  "Sensor1Base" + DELIMITER +
					  "Sensor2Base" + DELIMITER +
					  "Sensor0Max" + DELIMITER +
					  "Sensor1Max" + DELIMITER +
					  "Sensor2Max";
		if (!writeLine(line))
			return false;
		
		// Write the header data
		line = username + DELIMITER +
				new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp) + DELIMITER +
				deviceID + DELIMITER +
				(programmeName == null ? "" : programmeName) + DELIMITER +
				String.valueOf(userBaseline.getLiteralSensor0()) + DELIMITER +
				String.valueOf(userBaseline.getLiteralSensor1()) + DELIMITER +
				String.valueOf(userBaseline.getLiteralSensor2()) + DELIMITER +
				String.valueOf(userMaxForce.getLiteralSensor0()) + DELIMITER +
				String.valueOf(userMaxForce.getLiteralSensor1()) + DELIMITER +
				String.valueOf(userMaxForce.getLiteralSensor2());
		if (!writeLine(line))
			return false;
		
		// Write the sample field names
		line = "TimeOffset" + DELIMITER +
			   "PercentOfMax" + DELIMITER +
			   "TargetPercentOfMax" + DELIMITER +
			   "Sensor0" + DELIMITER +
			   "Sensor1" + DELIMITER +
			   "Sensor2";
		if (!writeLine(line))
			return false;
		
		// REady for samples
		return true;
	}
	
	public final boolean writeLine(String serial_number, String uid_number, float sensorCo0, float sensorCo1, float sensorCo2)
	{
		String line = serial_number + DELIMITER +
					  uid_number + DELIMITER +
					  String.valueOf(sensorCo0) + DELIMITER +
					  String.valueOf(sensorCo1) + DELIMITER +
					  String.valueOf(sensorCo2);
		return writeLine(line);
	}
	
	protected final boolean writeLine(String line)
	{
		if (mFile == null)
			return false;
		try
		{
			mFile.write(line);
			mFile.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}