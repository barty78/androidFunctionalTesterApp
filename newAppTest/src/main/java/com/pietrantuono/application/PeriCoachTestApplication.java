package com.pietrantuono.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cyanogenmod.updater.utils.MD5;
import com.pietrantuono.pericoach.newtestapp.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;

import customclasses.AndroidID;
import server.pojos.Firmware;
import server.pojos.Job;
import server.pojos.Sequence;

public class PeriCoachTestApplication extends Application {
	private static Context context=null;
	private static Firmware GetFirmware=null;
	private static File firmware=null;
	private static File firmwareCheckFile=null;
	private static Job job=null;
	private static Account mAccount;
	private static int lastPos = 0;
	private static final String AUTHORITY = "com.example.android.datasync.provider";
	private static String IOIOAddress="";
	private static String android_id="";
	private static float gradient = 0;
	private static float maxBatteryVoltage;
	private static float minBatteryVoltage;
	private static Sequence sequence;

	private static boolean isretestallowed;
	private static PeriCoachTestApplication application;


	public static boolean getIsRetestAllowed() { return isretestallowed;}

	public static void setIsRetestAllowed(boolean isretestallowed) {
		PeriCoachTestApplication.isretestallowed = isretestallowed;
	}

	public static File getFirmwareCheckFile() {
		return firmwareCheckFile;
	}

	public static File getFirmware() { return firmware;	}

	public static void setFirmware(String filename) {
		PeriCoachTestApplication.firmware = new File(context.getFilesDir(),filename);
		PeriCoachTestApplication.firmwareCheckFile = new File(context.getFilesDir(),"check"+filename);

	}

	public static Sequence getSequence() {
		return sequence;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context=getApplicationContext();
		mAccount = CreateSyncAccount(context);
		setUpSyncApapter();
		android_id = AndroidID.getID(PeriCoachTestApplication.this);
		if(android_id==null)android_id="";
		//forceSync();
		addDevicesSyncAccount();
		application=this;
	}

	public static PeriCoachTestApplication getApplication(){
		return application;
	}

	public static void setLastPos(int lastPos) {PeriCoachTestApplication.lastPos = lastPos;}

	public static float getMaxBatteryVoltage() {
		return maxBatteryVoltage;
	}

	public static void setMaxBatteryVoltage(float voltage) {PeriCoachTestApplication.maxBatteryVoltage = voltage;}

	public static float getMinBatteryVoltage() {
		return minBatteryVoltage;
	}

	public static void setMinBatteryVoltage(float voltage) {PeriCoachTestApplication.minBatteryVoltage = voltage;}


	public static float getGradient() {
		return gradient;}

	public static void setGradient(float gradient) {PeriCoachTestApplication.gradient = gradient;}
	
	public static Context getContext(){
		return context;
	}

	public static Job getCurrentJob() {return job;}

	public static void setCurrentJob(Job job) {PeriCoachTestApplication.job = job;}

	public static Firmware getGetFirmware() {
		return GetFirmware;
	}

	public static void setGetFirmware(Firmware getFirmware) {
		GetFirmware = getFirmware;
	}
	

	public static void setSequence(Sequence sequence) {
		PeriCoachTestApplication.sequence=sequence;
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
		ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
	}
	public void forceSyncDevices() {
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(createAccount(), getResources().getString(R.string.devices_sync_provider_authority), settingsBundle);
	}

	public Account createAccount() {
		Account newAccount = new Account(
				getResources().getString(R.string.devices_sync_account),getResources().getString(R.string.devices_sync_account_type));
		AccountManager accountManager =
				(AccountManager) context.getSystemService(
						Context.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {/*TODO*/} else {/*TODO*/}
		return newAccount;
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


	private void addDevicesSyncAccount() {
		Account newAccount = new Account(getString(R.string.devices_sync_account)
				,getResources().getString(R.string.devices_sync_account_type));
		AccountManager accountManager =
				(AccountManager) context.getSystemService(
						Context.ACCOUNT_SERVICE);

		if (accountManager.addAccountExplicitly(newAccount, null, null)) {} else {  }

	}
	
}
