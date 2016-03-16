package com.pietrantuono.activities;

import java.util.ArrayList;
import java.util.List;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.fragments.SerialConsoleFragmentCallback;
import com.pietrantuono.fragments.devices.DevicesListFragment;
import com.pietrantuono.fragments.sequence.NewSequenceFragment;
import com.pietrantuono.activities.uihelper.ActivityCallback;
import com.pietrantuono.activities.uihelper.MyDialogInterface;
import com.pietrantuono.activities.uihelper.MyDialogs;
import com.pietrantuono.activities.uihelper.UIHelper;
import com.pietrantuono.activities.uihelper.UIHelper.ActivityUIHelperCallback;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;

import customclasses.NewSequence;

import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.PCBConnectedCallback;
import com.pietrantuono.ioioutils.PCBDetectHelper;
import com.pietrantuono.ioioutils.PCBDetectHelper.PCBDetectHelperInterface;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.recordsdb.NewRecordsSQLiteOpenHelper;
import com.pietrantuono.recordsdb.RecordsContract;
import com.pietrantuono.recordsdb.RecordsProcessor;
import com.pietrantuono.sensors.SensorTestCallback;
import com.pietrantuono.sequencedb.SequenceProviderHelper;
import com.pietrantuono.tests.implementations.GetBarcodeTest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.pojos.Device;
import server.pojos.Job;
import server.pojos.Test;
import server.pojos.records.TestRecord;
import server.utils.RecordFromSequenceCreator;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
        implements ActivtyWrapper, IOIOLooperProvider, NewIOIOActivityListener,
        PCBConnectedCallback, SensorTestCallback, ActivityUIHelperCallback,
        MyOnCancelListener.Callback, ActivityCallback, NewSequenceFragment.SequenceFragmentCallback, SerialConsoleFragmentCallback,
        DevicesListFragment.CallBack {
    private static IOIO myIOIO;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mJobNo = null;
    private final IOIOAndroidApplicationHelperWrapper ioioAndroidApplicationHelperWrapper = new IOIOAndroidApplicationHelperWrapper(this);
    private DigitalInput _PCB_Detect;
    //    private String serial = "";
//    private String mac = "";
    private Boolean destroying = false;
    private PCBDetectHelperInterface detectHelper = null;
    private ArrayList<ArrayList<NewMResult>> results = new ArrayList<ArrayList<NewMResult>>();
    private int CURRENT_ITERATION_NUMBER = -1;
    private UIHelper uiHelper;
    private static NewSequenceInterface newSequence;
    private static NewSequenceInterface sequenceForTests = null;
    private BTUtility btutility;
    static final String JOB = "job";
    private Job job = null;
    private BaseIOIOLooper looper;
    private boolean sequenceStarted;
    //    private String barcode;
    private SerialConsoleFragmentCallback serialConsoleFragmentCallback;
    private boolean hideRestart = true;
    private DevicesListFragment devicesListFragment;
    private boolean isDevicesListActionbar;
    private long recordId;
    private Device sequenceDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!Fabric.isInitialized())
            Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());

        detectHelper = PCBDetectHelper.getHelper();
        setContentView(R.layout.activity_main);
        uiHelper = new UIHelper(MainActivity.this, newSequence);
        PreferenceManager.setDefaultValues(this, R.xml.settingsscreen, false);
        Intent intent = getIntent();
        if (intent != null) {
            job = intent.getParcelableExtra(JOB);
            if (job != null) mJobNo = job.getJobno();
        }
        if (mJobNo != null)
            uiHelper.setJobId(MainActivity.this, mJobNo);
        uiHelper.setupChronometer(MainActivity.this);
        uiHelper.updateStats(job, MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        destroying = true;
        detectHelper.stopCheckingIfConnectionDrops();
        ioioAndroidApplicationHelperWrapper.stopAndDestroy();
        try {
            newSequence.stopAll(MainActivity.this);
        } catch (Exception e) {
        }
        try {
            IOIOUtils.getUtils().closeall(MainActivity.this, MainActivity.this);

        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OrientationUtils.setOrientation(MainActivity.this);
        if (ioioAndroidApplicationHelperWrapper != null)
            ioioAndroidApplicationHelperWrapper.createAndStartHelperIfNotAlreadyStarted();

    }

    public synchronized void goAndExecuteNextTest() {
        if (!sequenceStarted) return;
        if (MainActivity.this.isFinishing()) return;
        if (newSequence.isSequenceEnded()) {
            Log.d(TAG, "Sequence Ended");
            onCurrentSequenceEnd();
            return;
        }
        if (newSequence.isSequenceStarted()) {
            if (newSequence.getCurrentTest().isBlockingTest() && !newSequence.getCurrentTest().isSuccess()) {
                Log.d(TAG, "Blocking TEST Failed - Sequence Ended");
                onCurrentSequenceEnd();
                return;
            }
        }

        Log.e(TAG, "goAndExecuteNextTest " + newSequence.getNextTest().getDescription());
        newSequence.executeCurrentTest();
        uiHelper.setCurrentAndNextTaskinUI();
    }

    @Override
    @SuppressWarnings("ucd")
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
        if (looper == null) {
            looper = new MyLooper(MainActivity.this);
        }
        return looper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (!isDevicesListActionbar) {
            inflater.inflate(R.menu.menu, menu);
            menu.findItem(R.id.restart).setVisible(!hideRestart);
        } else {
            inflater.inflate(R.menu.context_menu, menu);
            final Switch aSwitch = (Switch) MenuItemCompat.getActionView(menu.findItem(R.id.currentjobonly));
            if (devicesListFragment != null)
                aSwitch.setChecked(devicesListFragment.isThisJobOnly());
            if (aSwitch.isChecked()) aSwitch.setText("Current job only");
            else aSwitch.setText("All jobs");
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (devicesListFragment != null) devicesListFragment.currentJobOnly(true);
                        aSwitch.setText("Current job only");
                    } else {
                        if (devicesListFragment != null) devicesListFragment.currentJobOnly(false);
                        aSwitch.setText("All jobs");
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent in = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(in);
            return true;
        }
        if (!isDevicesListActionbar) {
            switch (item.getItemId()) {
                case R.id.settings:
                    Intent in = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(in);
                    return true;
                case R.id.restart:
                    restartSequence();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } else {
            if (item.getItemId() == R.id.click) {
                PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(item.getItemId()));
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.sort_by_barcode:
                                if (devicesListFragment != null)
                                    devicesListFragment.sortByBarcode();
                                return true;
                            case R.id.sort_by_result:
                                if (devicesListFragment != null)
                                    devicesListFragment.sortByResult();
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("ucd")
    public void setSerialBT(final String serial, final Boolean success) {
        if (serial != null && !serial.isEmpty()) {
            uiHelper.addView("Serial (BT reading): ", serial, false);
        }
    }

    @Override
    public void onBackPressed() {
        MyDialogs.
                createAlertDialog(MainActivity.this, "Close TEST", "Are you sure you want to close this test?", "YES, let's close", "NO, let's continue", new MyOnCancelListener(MainActivity.this), new MyDialogInterface() {
                    @Override
                    public void yes() {
                        closeActivity();
                    }

                    @Override
                    public void no() {
                    }
                });
    }

    @Override
    public void onPCBConnectionLostRestartSequence() {
        stopAndResetSequence();
        setStatusMSG("FIXTURE CONNECTION LOST", false);
        MyDialogs.createAlertDialog(MainActivity.this, "Fixture connection lost", "Connection lost with fixture, please check and restart test", "OK", null, new MyOnCancelListener(MainActivity.this), new MyDialogInterface() {
            @Override
            public void yes() {
                if (getIterationNumber() > 1)
                    results.remove(getIterationNumber() - 1);
                if (getIterationNumber() >= 0)
                    decreaseIterationNumber();
                uiHelper.cleanUI(MainActivity.this);
                waitForPCBConnected();
            }

            @Override
            public void no() {
            }
        });
    }

    private void stopAndResetSequence() {
        sequenceStarted = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newSequence.stopAll(MainActivity.this);
                newSequence.reset();
                try {
                    Voltage.interrupt();
                } catch (Exception e) {
                }
                IOIOUtils.getUtils().closeall(MainActivity.this, MainActivity.this);
                if (btutility != null) {
                    try {
                        btutility.abort();
                    } catch (Exception e) {
                    }
                }
                detectHelper.stopCheckingIfConnectionDrops();
                uiHelper.playSound(MainActivity.this);
                uiHelper.stopChronometer(MainActivity.this);
            }
        });
    }

    public void closeActivity() {
        destroying = true;
        detectHelper.stopCheckingIfConnectionDrops();
        try {
            // IOIOUtils.stopDiscoverBroadcastReceiver(MainActivity.this);
        } catch (Exception e) {
        }
        if (btutility != null) {
            btutility.stop();
            btutility = null;
        }
        BTUtility.unregisterIOIOAddressREceiver();
        MainActivity.this.finish();
    }

    public void onCurrentSequenceEnd() {
        hideRestart = false;
        if (!isDevicesListActionbar) invalidateOptionsMenu();
        IOIOUtils.getUtils().stopUartThread();
        PeriCoachTestApplication.setLastPos(0);
        sequenceStarted = false;
        newSequence.setEndtime(System.currentTimeMillis());
        final boolean overallresult = newSequence.getOverallResultBool();
        PeriCoachTestApplication.getApplication().forceSyncDevices();
        if (job.getIslogging() == 1) {
            newSequence.deleteUnusedTests();
            if (newSequence.getCurrentTestNumber() != 0) {    //  Don't create a record if the first test failed,
                // usually Barcode TEST.
                // 	TODO - Maybe check if barcode is actually set instead,
                // if no barcode then no record
                NewRecordsSQLiteOpenHelper newRecordsHelper = NewRecordsSQLiteOpenHelper.getInstance(MainActivity.this);
                TestRecord record = RecordFromSequenceCreator.createRecordFromSequence(newSequence,sequenceDevice);
                //MyDatabaseUtils.RecontructRecord(record);
                long id = RecordsProcessor.saveRecord(MainActivity.this, record, newRecordsHelper);
                if (id > 0) {

                    String selection = "Id = ?";
                    String[] selectionArgs = new String[]{"" + id};
                    Cursor c = newRecordsHelper.getWritableDatabase().query(RecordsContract.TestRecords.TABLE, null, selection, selectionArgs, null, null, null);
                    if (c.getCount() > 0) {
                        List<TestRecord> records = RecordsProcessor.reconstructRecords(MainActivity.this, c,newRecordsHelper);
                        if (records.size() > 0) {
                            Gson gson = new GsonBuilder()
                                    .excludeFieldsWithoutExposeAnnotation()
                                    .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                                    .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                                    .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                                    .create();
                            String recordstring = gson.toJson(records.get(0), TestRecord.class);
                            Log.d(TAG, "Created record: " + recordstring);
                        }
                    }
                    PeriCoachTestApplication.forceSync();
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (btutility != null) { // OK
                    btutility.stop();
                    btutility = null;
                }
                uiHelper.updateStats(job, MainActivity.this);
                uiHelper.stopChronometer(MainActivity.this);
                setStatusMSG("TEST FINISHED", true);// OK
                detectHelper.stopCheckingIfConnectionDrops();// OK
                detectHelper.stopPCBSleepMonitor();
//				uiHelper.setOverallFailOrPass(overallresult);// NA
                uiHelper.setOverallFailOrPass(true, getBarcode());// NA
                PeriCoachTestApplication.forceSync();
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitForPCBDisconnected();
            }
        });

    }

    public String getSerial() {
        return sequenceDevice.getSerial();
    }

    @Override
    public String getMac() {
        return sequenceDevice.getBt_addr();
    }

    @Override
    public void setBarcode(String barcode) {
        sequenceDevice.setBarcode(barcode);
    }

    @Override
    public String getBarcode() {
        return sequenceDevice.getBarcode();
    }

    private void waitForPCBConnected() {
        sequenceStarted = false;
        Log.d(TAG, "Wait for PCB to connect");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_PCB_Detect != null) {
                    setStatusMSG("LOAD FIXTURE", null);
                    detectHelper.waitForPCBDetect(MainActivity.this, _PCB_Detect);
                }
            }
        });
    }

    private void waitForPCBDisconnected() {
        sequenceStarted = false;
        Log.d(TAG, "Wait for PCB to disconnect");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_PCB_Detect != null) {
                    setStatusMSG("UNLOAD FIXTURE", null);
                    detectHelper.waitForPCBDisconneted(MainActivity.this, _PCB_Detect);
                }
            }
        });
    }

    @Override
    public void onPCBConnectedStartNewSequence() {
        sequenceStarted = false;
        if (isFinishing()) return;
        PeriCoachTestApplication.forceSync();
        PeriCoachTestApplication.getApplication().forceSyncDevices();
        uiHelper.cleanUI(MainActivity.this);
        uiHelper.removeOverallFailOrPass();
        hideRestart = true;
        if (!isDevicesListActionbar) invalidateOptionsMenu();
        start();
    }

    private void start() {
        sequenceStarted = true;
//        barcode = null;
        newSequence = null;
        sequenceDevice = new Device();
        newSequence = getNewSequence();
        newSequence.setStarttime(System.currentTimeMillis());
        try {
            newSequence.setJobNo(Long.parseLong(mJobNo));
        } catch (Exception e) {
        }
        uiHelper.setSequence(newSequence);
        recordId = SequenceProviderHelper.createNewRecord(MainActivity.this);
        uiHelper.setRecordId(recordId);
        uiHelper.setStatusMSG("TEST \nSTARTED", null);
        results.add(((NewSequenceInterface) newSequence).getEmptyResultsList());
        uiHelper.startChronometer(MainActivity.this);

        increaseIterationNumber();// NA
        newSequence.reset();
        IOIOUtils.getUtils().initialize(MainActivity.this, myIOIO, MainActivity.this);
        uiHelper.setCurrentAndNextTaskinUI();
        if (BuildConfig.DEBUG) {
            uiHelper.addView("Max V: ", String.valueOf(PeriCoachTestApplication.getMaxBatteryVoltage()), false);
            uiHelper.addView("Min V: ", String.valueOf(PeriCoachTestApplication.getMinBatteryVoltage()), false);
            uiHelper.addView("Grad: ", String.valueOf(PeriCoachTestApplication.getGradient()), false);
        }
        detectHelper.setPCBDetectCallback(MainActivity.this);
        //TODO - Only do dropped connection testing for open test.
        if (job.getTesttypeId() == 1) {
            detectHelper.startCheckingIfConnectionDrops(_PCB_Detect);// TODO
            // attention
            // here!
        }
        goAndExecuteNextTest();
    }

    @Override
    public void onPCBDisconnected() {
        sequenceStarted = false;
        if (job.getTesttypeId() == 1) {
            uiHelper.cleanUI(MainActivity.this);
        }
        IOIOUtils.getUtils().closeall(MainActivity.this, MainActivity.this);
        waitForPCBConnected();
    }

    @Override
    public void onPCBSleep() {
        stopAndResetSequence();
        uiHelper.setStatusMSG("DEVICE GONE TO SLEEP", false);
        MyDialogs.createAlertDialog(MainActivity.this, "Device Entered Sleep Mode", "Device under test has entered sleep mode unexpectedly", "OK", null, new MyOnCancelListener(MainActivity.this), new MyDialogInterface() {
            @Override
            public void yes() {
                onCurrentSequenceEnd();
            }

            @Override
            public void no() {
            }
        });
    }

    @Override
    public void onSensorTestCompleted(NewMSensorResult mSensorResult, server.pojos.Test testToBeParsed) {
        try {
            results.get(getIterationNumber()).set(newSequence.getCurrentTestNumber(), mSensorResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        uiHelper.addSensorTestCompletedRow(mSensorResult, testToBeParsed, recordId);
        Handler h = new Handler(android.os.Looper.getMainLooper());
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goAndExecuteNextTest();
            }
        }, 100);

    }

    private void increaseIterationNumber() {
        CURRENT_ITERATION_NUMBER++;
    }

    private void decreaseIterationNumber() {
        CURRENT_ITERATION_NUMBER--;
    }

    public int getIterationNumber() {
        return CURRENT_ITERATION_NUMBER;
    }


    public void startPCBSleepMonitor() {
        detectHelper.startPCBSleepMonitor(MainActivity.this);
    }

    @Override
    public void addView(String label, String text, int color, boolean goAndExecuteNextTest) {
        uiHelper.addView(label, text, color, goAndExecuteNextTest);
    }

    @Override
    public void setStatusMSG(String serial, Boolean success) {
        uiHelper.setStatusMSG(serial, success);

    }

    @Override
    public ArrayList<ArrayList<NewMResult>> getResults() {
        return results;
    }

    private void addFailOrPass(Boolean istest, Boolean success, String reading, String otherreading, String description, Test testToBeParsed) {
        uiHelper.addFailOrPass(istest, success, reading, otherreading, description, false, testToBeParsed, recordId);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goAndExecuteNextTest();
            }
        }, 100);
    }

    @Override
    public void addFailOrPass(Boolean istest, Boolean success, String reading, String description, Test testToBeParsed) {
        addFailOrPass(istest, success, reading, null, description, testToBeParsed);
    }

    @Override
    public void addFailOrPass(String otherreadig, Boolean istest, Boolean success, String description) {
        addFailOrPass(istest, success, null, otherreadig, description, null);
    }

    @Override
    public void addFailOrPass(Boolean istest, Boolean success, String reading, String description) {
        addFailOrPass(istest, success, reading, null, description, null);
    }

    @Override
    public synchronized void addFailOrPass(final Boolean istest, final Boolean success, String reading, Test testToBeParsed) {
        addFailOrPass(istest, success, reading, null, null, testToBeParsed);
    }

    public void addFailOrPass(Boolean istest, Boolean success, String reading) {
        addFailOrPass(istest, success, reading, null, null, null);
    }

    @Override
    public void addFailOrPass(final Boolean istest, final Boolean success, String reading, String description, boolean isSensorTest, Test testToBeParsed) {
        uiHelper.addFailOrPass(istest, success, reading, null, description, true, testToBeParsed, recordId);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goAndExecuteNextTest();
            }
        }, 100);
    }

    private void restartSequence() {
        detectHelper.stopCheckingIfConnectionDrops();// OK
        detectHelper.stopWaitingForPCBDisconnected();
        onPCBDisconnected();
    }

    @Override
    public void setSerial(String serial) {
        sequenceDevice.setSerial(serial);
    }

    @Override
    public void setMacAddress(String mac) {
        sequenceDevice.setBt_addr(mac);
    }

    public BTUtility getBtutility() {
        return btutility;
    }

    public void setBtutility(BTUtility btutility) {
        this.btutility = btutility;
    }

    public boolean isMainActivityBeingDestroyed() {
        return destroying;
    }


    @Override
    public void onUploadTestFinished(boolean istest, boolean success, String description, String failReason) {
        uiHelper.onUploadTestFinished(success, description, recordId, failReason);
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goAndExecuteNextTest();
            }
        }, 100);
    }

    @Override
    public boolean isActivityFinishing() {
        return MainActivity.this.isFinishing();
    }

    private void toast(String text, int lenght) {
        Toast.makeText(MainActivity.this, text, lenght).show();
    }

    @Override
    public void onIOIOLooperSetup(final IOIO ioio) {
        myIOIO = ioio;
        try {
            _PCB_Detect = ioio.openDigitalInput(22, DigitalInput.Spec.Mode.PULL_UP);
        } catch (ConnectionLostException e) {
            toast(e.toString(), Toast.LENGTH_LONG);
            Crashlytics.logException(e);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uiHelper.setConnected(true);
                uiHelper.cleanUI(MainActivity.this);
                waitForPCBConnected();
            }
        });
    }


    @Override
    public void onIOIOLooperDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uiHelper.setConnected(false);
                if (!destroying) toast("CONNECTION LOST !!!", Toast.LENGTH_LONG);
            }
        });
    }
    //**
    //Methods used by tests
    //**


    public void setLooper(BaseIOIOLooper looper) {
        this.looper = looper;
    }

    public NewSequenceInterface getNewSequence() {
        NewSequenceInterface newSequenceInterface = null;
        if (job.getTestId() == 999) {
            newSequenceInterface = new NewSequence(MainActivity.this, myIOIO, job);
        } else {
            Gson gson = new Gson();
            System.out.println("TESTING: " + gson.toJson(PeriCoachTestApplication.getSequence()));
            if (sequenceForTests == null) {
                newSequenceInterface = new NewSequence(MainActivity.this, myIOIO, job, PeriCoachTestApplication.getSequence());
            } else {
                newSequenceInterface = sequenceForTests;
            }
        }
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < newSequenceInterface.getSequence().size(); i++) {
                if (newSequenceInterface.getSequence().get(i) instanceof GetBarcodeTest) break;
                setSequenceDevice(getSequenceDevice().setBarcode(getResources().getString(R.string.barcode_for_test)));
            }
        }
        return newSequenceInterface;
    }

    public void setNewSequence(NewSequenceInterface newSequence) {
        MainActivity.sequenceForTests = newSequence;
    }

    public void setBTUtility(BTUtility btUtility) {
        this.btutility = btUtility;
    }


    @Override
    public void registerSequenceFragment(NewSequenceFragment sequenceFragment) {
        if (uiHelper != null && sequenceFragment != null)
            uiHelper.registerSequenceFragment(sequenceFragment);
    }

    @Override
    public void unregisterSequenceFragment() {
        uiHelper.unregisterSequenceFragment();
    }

    @Override
    public void updateUI(String text) {
        if (serialConsoleFragmentCallback != null) serialConsoleFragmentCallback.updateUI(text);
    }

    @Override
    public void clearSerialConsole() {
        if (serialConsoleFragmentCallback != null)
            serialConsoleFragmentCallback.clearSerialConsole();
    }

    @Override
    public void setCallback(SerialConsoleFragmentCallback serialConsoleFragmentCallback) {
        this.serialConsoleFragmentCallback = serialConsoleFragmentCallback;
    }

    @Override
    public void removeCallback() {
        this.serialConsoleFragmentCallback = null;
    }

    @Override
    public void setDevicesListFragment(DevicesListFragment devicesListFragment) {
        this.devicesListFragment = devicesListFragment;
    }

    @Override
    public void setDevicesFragmentActionBar(boolean isDevicesListActionbar) {
        this.isDevicesListActionbar = isDevicesListActionbar;
        invalidateOptionsMenu();
    }

    public Device getSequenceDevice() {
        return sequenceDevice;
    }

    public void setSequenceDevice(Device sequenceDevice) {
        this.sequenceDevice = sequenceDevice;
    }
}