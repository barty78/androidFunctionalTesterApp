package analytica.pericoach.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

public class DataManager
{
	// Internal constant, not in string resources by design
	private static final String PFMAT_DIRECTORY = "PeriCoachTest";
	
	static public String getPFMATDataDirectory()
	{
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state))
			return null; // Not available, or read-only
		
		File dir = Environment.getExternalStorageDirectory();
		if (dir == null)
			return null;
		
		// Check if the PFMAT subdirectory exists and create it if not
		String targetDir = dir.getAbsolutePath() + '/' + PFMAT_DIRECTORY;
		File subDir = new File(targetDir);
		if (!subDir.exists() && !subDir.mkdir())
			return null;
		
		return targetDir;
	}
}
