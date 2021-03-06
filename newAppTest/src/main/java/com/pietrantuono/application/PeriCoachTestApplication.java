package com.pietrantuono.application;

import java.io.File;
import java.util.ArrayList;

import com.cyanogenmod.updater.utils.MD5;
import com.pietrantuono.pericoach.newtestapp.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
	private static Job primaryJob=null;
	private static Account mAccount;
	private static int lastPos = 0;
	private static final String AUTHORITY = "com.example.android.datasync.provider";
	private static String IOIOAddress="";
	private static String android_id="";
	private static float gradient = 0;
	private static float maxBatteryVoltage;
	private static float minBatteryVoltage;
	private static Sequence sequence;
	private static int testType=0;
	private static int testCount=0;
	private static float jobAvgTestTime;
	private static String appVersion=null;
	private static String appCode=null;
	private static String ioioFirmwareVersion=null;
	private static String ioioHardwareVersion=null;
	private static String ioioLibVersion=null;

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

	public static String getVersion() { return appVersion; }

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
		android_id = AndroidID.getID(context);
		if(android_id==null)android_id="";
		//forceSync();
		addDevicesSyncAccount();
		application=this;
		try {
			appVersion=getAppVersion();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

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

	public static Job getPrimaryJob() {return primaryJob;}

	public static void setPrimaryJob(Job job) {PeriCoachTestApplication.primaryJob = job;}

	public static Firmware getGetFirmware() {
		return GetFirmware;
	}

	public static void setGetFirmware(Firmware getFirmware) {
		GetFirmware = getFirmware;
	}

	public static int getTestType() {return testType;}

	public static void setTestType(int testType) {PeriCoachTestApplication.testType = testType;}

	public static int getTestCount() { return testCount;}

	public static void setTestCount(int testCount) {PeriCoachTestApplication.testCount = testCount;}

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

	private Account createAccount() {
		Account newAccount = new Account(
				getResources().getString(R.string.devices_sync_account),getResources().getString(R.string.devices_sync_account_type));
		AccountManager accountManager =
				(AccountManager) context.getSystemService(
						Context.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {/*TODO*/} else {/*TODO*/}
		return newAccount;
	}

	public static void setIOIOFirmwareVersion(String version) {
		PeriCoachTestApplication.ioioFirmwareVersion=version;
	}

	public static String getIOIOFirmwareVersion() {
		return PeriCoachTestApplication.ioioFirmwareVersion;
	}

	public static void setIOIOHardwareVersion(String version) {
		PeriCoachTestApplication.ioioHardwareVersion=version;
	}

	public static String getIOIOHardwareVersion() {
		return PeriCoachTestApplication.ioioHardwareVersion;
	}

	public static void setIOIOLibraryVersion(String version) {
		PeriCoachTestApplication.ioioLibVersion=version;
	}

	public static String getIOIOLibraryVersion() {
		return PeriCoachTestApplication.ioioLibVersion;
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

	public String getAppVersion() throws PackageManager.NameNotFoundException {
		PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		String version = pInfo.versionName;
		return version;
	}

	public int getAppCode() throws PackageManager.NameNotFoundException {
		PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		int code = pInfo.versionCode;
		return code;
	}
	
}
