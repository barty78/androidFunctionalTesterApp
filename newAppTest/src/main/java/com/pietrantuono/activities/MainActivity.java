package com.pietrantuono.activities;

import java.util.ArrayList;
import java.util.List;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pietrantuono.activities.fragments.SequenceFragment;
import com.pietrantuono.activities.fragments.SerialConsoleFragmentCallback;
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
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.sensors.SensorTestCallback;
import com.pietrantuono.tests.superclass.Test;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import io.fabric.sdk.android.Fabric;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import server.MyDoubleTypeAdapter;
import server.MyIntTypeAdapter;
import server.MyLongTypeAdapter;
import server.RetrofitRestServices;
import server.pojos.Device;
import server.pojos.DevicesList;
import server.pojos.Job;
import server.pojos.records.TestRecord;
import server.service.ServiceDBHelper;
import server.utils.MyDatabaseUtils;
import server.utils.TestFromSequenceCreator;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
        implements ActivtyWrapper, IOIOLooperProvider, NewIOIOActivityListener,
        PCBConnectedCallback, SensorTestCallback, ActivityUIHelperCallback,
        MyOnCancelListener.Callback, ActivityCallback, SequenceFragment.SequenceFragmentCallback, SerialConsoleFragmentCallback {
    private static IOIO myIOIO;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String mJobNo = null;
    private final IOIOAndroidApplicationHelperWrapper ioioAndroidApplicationHelperWrapper = new IOIOAndroidApplicationHelperWrapper(this);
    private DigitalInput _PCB_Detect;
    private String serial = "";
    private String mac = "";
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
    private String barcode;
    private SerialConsoleFragmentCallback serialConsoleFragmentCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!Fabric.isInitialized()) Fabric.with(this, new Crashlytics());
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
            uiHelper.setJobId(mJobNo, true);
        uiHelper.setupChronometer(MainActivity.this);
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

    @Override
    public synchronized void goAndExecuteNextTest() {
        if (!sequenceStarted) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.this.isFinishing()) return;
                if (newSequence.isSequenceEnded()) {
                    Log.d(TAG, "Sequence Ended");
                    onCurrentSequenceEnd();
                    return;
                }
                if (newSequence.isSequenceStarted()) {
                    if (newSequence.getCurrentTest().isBlockingTest && !newSequence.getCurrentTest().isSuccess()) {
                        Log.d(TAG, "Blocking Test Failed - Sequence Ended");
                        onCurrentSequenceEnd();
                        return;
                    }
                }
                newSequence.Next();
                uiHelper.setCurrentAndNextTaskinUI();
                newSequence.executeCurrentTest();
                Log.e(TAG, "goAndExecuteNextTest");
            }
        });
    }

    @Override
    public synchronized ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading) {
        return uiHelper.addFailOrPass(istest, success, reading, null);
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
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent in = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                createAlertDialog(MainActivity.this, "Close Test", "Are you sure you want to close this test?", "YES, let's close", "NO, let's continue", new MyOnCancelListener(MainActivity.this), new MyDialogInterface() {
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
                setStatusMSG("FIXTURE CONNECTION LOST", false);
                uiHelper.stopChronometer(MainActivity.this);
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
        sequenceStarted = false;
        newSequence.setEndtime(System.currentTimeMillis());
        final boolean overallresult = newSequence.getOverallResultBool();

        if (job.getIslogging() == 1) {
            newSequence.deleteUnusedTests();
            if (newSequence.getCurrentTestNumber() != 0) {    //  Don't create a record if the first test failed,
                // usually Barcode Test.
                // 	TODO - Maybe check if barcode is actually set instead,
                // if no barcode then no record
                TestRecord record = TestFromSequenceCreator.createRecordFromSequence(newSequence);
                MyDatabaseUtils.RecontructRecord(record);
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .registerTypeAdapter(Long.class, new MyLongTypeAdapter())
                        .registerTypeAdapter(Double.class, new MyDoubleTypeAdapter())
                        .registerTypeAdapter(Integer.class, new MyIntTypeAdapter())
                        .create();
                String recordstring = gson.toJson(record, TestRecord.class);
                Log.d(TAG, "Created record: " + recordstring);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (btutility != null) { // OK
                    btutility.stop();
                    btutility = null;
                }
                uiHelper.stopChronometer(MainActivity.this);
                setStatusMSG("TEST FINISHED", true);// OK
                detectHelper.stopCheckingIfConnectionDrops();// OK
//				uiHelper.setOverallFailOrPass(overallresult);// NA
                uiHelper.setOverallFailOrPass(true);// NA
                PeriCoachTestApplication.forceSync();
                waitForPCBDisconnected();
            }
        });

    }

    @Override
    public void setResult(boolean success) {
        uiHelper.setResult(success);
    }

    @Override
    public String getMac() {
        return newSequence.getBT_Addr();
    }

    @Override
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String getBarcode() {
        return barcode;
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyDialogs.showProgress(MainActivity.this);
                RetrofitRestServices.getRest(MainActivity.this).getLastDevices(PeriCoachTestApplication.getDeviceid(), PeriCoachTestApplication.getLastId(), new Callback<DevicesList>() {
                    @Override
                    public void success(DevicesList arg0, Response arg1) {
                        if (isFinishing()) return;
                        MyDialogs.dismisProgress(MainActivity.this);
                        if (arg0 != null) ServiceDBHelper.addDevices(arg0);
                        else
                            Toast.makeText(MainActivity.this, "Failed devices download, continuing anyway", Toast.LENGTH_LONG).show();
                        start();
                    }

                    @Override
                    public void failure(RetrofitError arg0) {
                        Toast.makeText(MainActivity.this, "Failed devices download, continuing anyway", Toast.LENGTH_LONG).show();
                        MyDialogs.dismisProgress(MainActivity.this);
                        start();
                    }
                });
            }
        });

    }

    private void start() {
        sequenceStarted = true;
        barcode = null;
        newSequence = null;
        newSequence = getNewSequence();
        newSequence.setStarttime(System.currentTimeMillis());
        try {
            newSequence.setJobNo(Long.parseLong(mJobNo));
        } catch (Exception e) {
        }
        uiHelper.setSequence(newSequence);
        uiHelper.setStatusMSG("TEST \nSTARTED", null);
        results.add(((NewSequenceInterface) newSequence).getEmptyResultsList());
        uiHelper.startChronometer(MainActivity.this);
        ;
        increaseIterationNumber();// NA
        newSequence.reset();
        IOIOUtils.getUtils().initialize(MainActivity.this, myIOIO, MainActivity.this);
        uiHelper.setCurrentAndNextTaskinUI();
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
        uiHelper.cleanUI(MainActivity.this);
        IOIOUtils.getUtils().closeall(MainActivity.this, MainActivity.this);
        waitForPCBConnected();
    }

    @Override
    @SuppressWarnings("ucd")
    public void onSensorTestCompleted(NewMSensorResult mSensorResult) {
        try {
            results.get(getIterationNumber()).set(newSequence.getCurrentTestNumber(), mSensorResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        uiHelper.addSensorTestCompletedRow(mSensorResult);
        Handler h = new Handler(android.os.Looper.getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                goAndExecuteNextTest();
            }
        }, 1 * 1000);  //TODO - Why 5 sec delay here? 1 sec works.

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


    public String getSerial() {
        return serial;
    }

    @Override
    public void addView(String label, String text, boolean goAndExecuteNextTest) {
        uiHelper.addView(label, text, goAndExecuteNextTest);

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

    @Override
    @SuppressWarnings("ucd")
    public ProgressAndTextView addFailOrPass(String otherreadig, Boolean istest, Boolean success, String description) {
        return uiHelper.addFailOrPass(istest, success, null, otherreadig, description);
    }

    @Override
    public ProgressAndTextView addFailOrPass(Boolean istest, Boolean success, String reading, String description) {
        return uiHelper.addFailOrPass(istest, success, reading, null, description);
    }

    @Override
    public void manuallyRedoCurrentTest() {
        newSequence.executeCurrentTest();

    }

    @Override
    public void restartSequence() {
        detectHelper.stopCheckingIfConnectionDrops();// OK
        detectHelper.stopWaitingForPCBDisconnected();
        onPCBDisconnected();
    }

    @Override
    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public void setMacAddress(String mac) {
        this.mac = mac;
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
    @SuppressWarnings("ucd")
    public ProgressAndTextView createUploadProgress(boolean b, boolean c, String description) {
        return uiHelper.createUploadProgress(b, c, description);
    }

    @Override
    public boolean isActivityFinishing() {
        return MainActivity.this.isFinishing();
    }

    @Override
    public void toast(String text, int lenght) {
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
        if (job.getTestId() == 999) return new NewSequence(MainActivity.this, myIOIO, job);
        Gson gson = new Gson();
        System.out.println("TESTING: " + gson.toJson(PeriCoachTestApplication.getSequence()));
        if (sequenceForTests == null)
            return new NewSequence(MainActivity.this, myIOIO, job, PeriCoachTestApplication.getSequence());
        else return sequenceForTests;
    }

    public void setNewSequence(NewSequenceInterface newSequence) {
        MainActivity.sequenceForTests = newSequence;
    }

    public void setBTUtility(BTUtility btUtility) {
        this.btutility = btUtility;
    }


    @Override
    public void registerSequenceFragment(SequenceFragment sequenceFragment) {
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
}