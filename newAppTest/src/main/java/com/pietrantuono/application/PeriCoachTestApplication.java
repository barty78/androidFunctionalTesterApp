package com.pietrantuono.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cyanogenmod.updater.utils.MD5;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import server.pojos.Firmware;
import server.pojos.Sequence;

public class PeriCoachTestApplication extends  com.activeandroid.app.Application {
	private static Context context=null;
	private static Firmware GetFirmware=null;
	private static File firmware=null;
	private static File firmwareCheckFile=null;
	private static Account mAccount;
	private static final String AUTHORITY = "com.example.android.datasync.provider";
	private static String IOIOAddress="";
	private static String android_id="";
	@SuppressWarnings("unused")
	private String TAG=getClass().getSimpleName();
	private static File dir;
	private static AssetManager assetManager;
	private static final String LAST_ID="last_id";

	public static File getFirmwareCheckFile() {
		return firmwareCheckFile;
	}

	public static File getFirmware() { return firmware;	}

	public static void setFirmware(String filename) {
		PeriCoachTestApplication.firmware = new File(context.getFilesDir(),filename);
		PeriCoachTestApplication.firmwareCheckFile = new File(context.getFilesDir(),"check"+filename);

	}

	@Override
	public void onCreate() {
		super.onCreate();
		context=getApplicationContext();
		mAccount = CreateSyncAccount(context);
		setUpSyncApapter();
		android_id = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
		if(android_id==null)android_id="";
		//forceSync();
	    assetManager = getAssets();
	    dir=getFilesDir();

	}
	
	public static Context getContext(){
		return context;
	}
	
	public static Firmware getGetFirmware() {
		return GetFirmware;
	}

	public static void setGetFirmware(Firmware getFirmware) {
		GetFirmware = getFirmware;
	}
	

	public static void setSequence(Sequence sequence) {
	}
	

	private static Account CreateSyncAccount(Context context) {
		String TAG = "CreateSyncAccount";
		// Create the account type and default account

		// An account type, in the form of a domain name
		final String ACCOUNT_TYPE = "example.com";
		// The account name
		final String ACCOUNT = "dummyaccount";
		// Instance fields
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
			Log.e(TAG, "addAccountExplicitly FAILED");
		}
		return newAccount;
	}
	
	@SuppressWarnings("static-access")
	private void setUpSyncApapter() {
		getContentResolver();
		// Turn on automatic syncing for the default account and authority
		ContentResolver.setMasterSyncAutomatically(true); 
		ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
		ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

	}
	
	public static void forceSync(){
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		/*
		 * Request the sync for the default account, authority, and manual sync
		 * settings
		 */
		ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
		
	}

	public static void setIOIOAddress(String address) {
		PeriCoachTestApplication.IOIOAddress=address;
		
	}

	private static String getIOIOAddress() {
		return PeriCoachTestApplication.IOIOAddress;
		
	}
	public static String getFixtureIdintification() {
		 String id=PeriCoachTestApplication.android_id.concat(getIOIOAddress());
		 return MD5.calculateMD5(id);
		
	}
	public static String getDeviceid() {
		return android_id;//TODO put back adnroid_id
	}
	
	
	
	
	public static String getLastId(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(LAST_ID, "");
	}
	
	public static void setFirmwareFileForTest(){
		copyAssets("check");
		copyAssets("");
		setFirmware("PeriCoach-0188-2.0.0.0.bin");
	}
	private static void copyAssets(String prefix) {
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    if (files != null) for (String filename : files) {
	    	if(filename.toLowerCase().contains(("Peri").toLowerCase())) continue;
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(filename);
	          File outFile = new File(dir, prefix+filename);
	          out = new FileOutputStream(outFile);
	          copyFile(in, out);
	        } catch(IOException e) {
	            Log.e("tag", "Failed to copy asset file: " + filename, e);
	        }     
	        finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    // NOOP
	                }
	            }
	            if (out != null) {
	                try {
	                    out.close();
	                } catch (IOException e) {
	                    // NOOP
	                }
	            }
	        }  
	    }
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
}
