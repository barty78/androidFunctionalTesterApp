package server;

import ioio.lib.api.IOIO;
import server.pojos.Job;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.ioioutils.Units;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.AccelerometerSelfTest;
import com.pietrantuono.tests.implementations.AwakeModeCurrentTest;
import com.pietrantuono.tests.implementations.BTConnectCurrent;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTestForTesting;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTest;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTestForTesting;
import com.pietrantuono.tests.implementations.BluetoothRFLevelTest;
import com.pietrantuono.tests.implementations.ChargeLedCheckTest;
import com.pietrantuono.tests.implementations.ChargingTest;
import com.pietrantuono.tests.implementations.ChargingTerminationTest;
import com.pietrantuono.tests.implementations.CurrentTest;
import com.pietrantuono.tests.implementations.DummyTest;
import com.pietrantuono.tests.implementations.GetFirmwareVersionUARTTest;
import com.pietrantuono.tests.implementations.upload.DummyUploadFirmwareTest;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.pietrantuono.tests.implementations.GetMacAddressTest;
import com.pietrantuono.tests.implementations.GetNFCTest;
import com.pietrantuono.tests.implementations.LedCheckTest;
import com.pietrantuono.tests.implementations.ListenToUart;
import com.pietrantuono.tests.implementations.MagnetWakeDeviceTest;
import com.pietrantuono.tests.implementations.ReadDeviceInfoSerialNumberTest;
import com.pietrantuono.tests.implementations.ReadFirmwareversionTest;
import com.pietrantuono.tests.implementations.ReadModelNumberTest;
import com.pietrantuono.tests.implementations.SensorTestWrapper;
import com.pietrantuono.tests.implementations.UUTCurrentTest;
import com.pietrantuono.tests.implementations.UartBlockWriteTest;
import com.pietrantuono.tests.implementations.UartLoopbackTest;
import com.pietrantuono.tests.implementations.upload.UploadFirmwareTest;
import com.pietrantuono.tests.implementations.VoltageTest;
import com.pietrantuono.tests.implementations.WakeDeviceTest;
import com.pietrantuono.tests.implementations.steps.PauseStep;
import com.pietrantuono.tests.implementations.steps.PromptStep;
import com.pietrantuono.tests.implementations.steps.SetDigitalOutputStep;
import com.pietrantuono.tests.implementations.steps.SetSensorVoltagesStep;
import com.pietrantuono.tests.superclass.Test;

public class TestsParser {

    private static final String TAG = TestsParser.class.getSimpleName();

    public static Test parseTest(final server.pojos.Test testToBeParsed,
                                 final Activity activity, IOIO ioio, Job job) {
        Test test = null;

        Log.d(TAG, "TEST ID: " + testToBeParsed.getId());
        int classID = (int) testToBeParsed.getTestclassId().intValue();

        if (classID == activity.getResources().getInteger(R.integer.GetBarcodeTest)) {
            float retries = (float) testToBeParsed.getIoiopinnum();
            Log.d(TAG, getDescription(testToBeParsed) + " - Retries(" + retries + ")");
            test = new GetBarcodeTest(activity, ioio, job, retries);

        } else if (classID == activity.getResources().getInteger(R.integer.CurrentTest)) {
            boolean isNominal = testToBeParsed.getIsNominal() == 1;
            float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
            float limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
            int pinnumber = (int) testToBeParsed.getIoiopinnum();
            Log.d(TAG, getDescription(testToBeParsed) + " - Limits(" + limitParam1 + "/" + limitParam2 + "|" + isNominal + ")");
            @Units int units = testToBeParsed.getUnits();

            test = new CurrentTest(activity, ioio, pinnumber, units, isNominal, limitParam1, limitParam2,
                    getDescription(testToBeParsed));
            test.setTestToBeParsed(testToBeParsed);

        } else if (classID == activity.getResources().getInteger(R.integer.VoltageTest)) {
            boolean isNominal = testToBeParsed.getIsNominal() == 1;
            float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
            float limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
            Log.d(TAG, getDescription(testToBeParsed) + "  - Limits(" + limitParam1 + "/" + limitParam2 + "|" + isNominal + ")");
            boolean isBlocking = testToBeParsed.getBlocking() == 1;
            int pinnumber = (int) testToBeParsed.getIoiopinnum();
            @Units int units = testToBeParsed.getUnits();
            if (testToBeParsed.getScaling() != 0) {
                float scaling = (float) testToBeParsed.getScaling().doubleValue();
                Log.d(TAG, "Scaling set to " + scaling);
                test = new VoltageTest(activity, ioio, pinnumber, units, isBlocking, scaling, isNominal, limitParam1, limitParam2,
                        getDescription(testToBeParsed));
            } else {
                Log.d(TAG, "Default Scaling = 1.0");

                test = new VoltageTest(activity, ioio, pinnumber, units, isBlocking, isNominal, limitParam1, limitParam2,
                        getDescription(testToBeParsed));
            }
            test.setTestToBeParsed(testToBeParsed);

        } else if (classID == activity.getResources().getInteger(R.integer.LedCheckTest)) {
            test = new LedCheckTest(activity, getDescription(testToBeParsed),
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.ChargingTerminationTest)) {
            test = new ChargingTerminationTest(activity, ioio,
                    getDescription(testToBeParsed));

        } else if (classID == activity.getResources().getInteger(R.integer.UploadFirmwareTest)) {
            boolean eraseEEPROM = testToBeParsed.getIoiopinnum() == 1;
            Log.d(TAG, "Firmware Upload Erase EEPROM - " + eraseEEPROM);
            test = new UploadFirmwareTest(activity, ioio, eraseEEPROM, getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.GetDeviceSerialTest)) {
            test = new GetDeviceSerialTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.WakeDeviceTest)) {
            test = new WakeDeviceTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.AwakeModeCurrentTest)) {
            test = new AwakeModeCurrentTest(activity, ioio,
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothDiscoverableModeTest)) {
            test = new BluetoothDiscoverableModeTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothDiscoverableModeTestForTesting)) {
            test = new BluetoothDiscoverableModeTestForTesting(activity);//TODO

        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothConnectTest)) {
            boolean connectViaMac = testToBeParsed.getIoiopinnum() == 1;
            test = new BluetoothConnectTest(activity, connectViaMac);
        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothConnectTestForTesting)) {
            boolean connectViaMac = testToBeParsed.getIoiopinnum() == 1;
            test = new BluetoothConnectTestForTesting(activity, connectViaMac);//TODO
        } else if (classID == activity.getResources().getInteger(R.integer.BTConnectCurrent)) {
            test = new BTConnectCurrent(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadDeviceInfoSerialNumberTest)) {
            test = new ReadDeviceInfoSerialNumberTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadModelNumberTest)) {
            test = new ReadModelNumberTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadFirmwareversionTest)) {
            test = new ReadFirmwareversionTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.BatteryLevelUUTVoltageTest)) {
            test = new BatteryLevelUUTVoltageTest(activity, ioio,
                    testToBeParsed.getLimitParam1(),
                    testToBeParsed.getLimitParam2(),
                    getDescription(testToBeParsed),
//                    (int) (float)testToBeParsed.getScaling());
                    testToBeParsed.getScaling());
            test.setTestToBeParsed(testToBeParsed);
        } else if (classID == activity.getResources().getInteger(R.integer.SensorTestWrapper)) {
            boolean hasPrompt = testToBeParsed.getIoiopinnum() == 1;        //Using IOIOPin number to indicate to single sensor closed test if prompt is needed.
            Long limitParam1 = testToBeParsed.getLimitParam1().longValue();
            Long limitParam2 = testToBeParsed.getLimitParam2().longValue();
            Long limitParam3 = testToBeParsed.getLimitParam3().longValue();
            Log.d(TAG, getDescription(testToBeParsed) + " - LIMITS: (" + limitParam1 + "|" + limitParam2 + "-" + limitParam3 + ")");
            test = new SensorTestWrapper(job.getTesttypeId() == 2, activity, ioio, (int) ((long) testToBeParsed.getLimitId()),
                    limitParam2, limitParam1, limitParam3, hasPrompt, getDescription(testToBeParsed));
            test.setTestToBeParsed(testToBeParsed);
        } else if (classID == activity.getResources().getInteger(R.integer.DummyTest)) {
            test = new DummyTest(activity, getDescription(testToBeParsed) + " " + testToBeParsed.getNumber(), false, true);
        } else if (classID == activity.getResources().getInteger(R.integer.ChargingTest)) {
            test = new ChargingTest(activity, ioio,
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.AccelerometerSelfTest)) {
            test = new AccelerometerSelfTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.ChargeLedCheckTest)) {
            test = new ChargeLedCheckTest(activity, ioio, getDescription(testToBeParsed),
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.DummyUploadFirmwareTest)) {
            test = new DummyUploadFirmwareTest((AppCompatActivity) activity, ioio, false); //TODO - Get boolean for loopback from db field.
        } else if (classID == activity.getResources().getInteger(R.integer.GetMacAddressTest)) {
            test = new GetMacAddressTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.ListenToUart)) {
            test = new ListenToUart(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.MagnetWakeDeviceTest)) {
            test = new MagnetWakeDeviceTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.UartBlockWriteTest)) {
            test = new UartBlockWriteTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.UartLoopbackTest)) {
            test = new UartLoopbackTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.UUTCurrentTest)) {
            test = new UUTCurrentTest(activity, ioio,
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.GetNFCTest)) {
            test = new GetNFCTest(activity, ioio, 0);
        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothRFLevelTest)) {
            Integer limit = testToBeParsed.getLimitParam1().intValue();
            test = new BluetoothRFLevelTest(activity, ioio, limit);
        } else if (classID == activity.getResources().getInteger(R.integer.GetFirmwareVersionUARTTest)) {
            test = new GetFirmwareVersionUARTTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.PauseStep)) {
            test = new PauseStep(activity, "Pause Step");
        } else if (classID == activity.getResources().getInteger(R.integer.PromptStep)) {
            test = new PromptStep(activity, "Prompt Step");
        } else if (classID == activity.getResources().getInteger(R.integer.SetDigitalOutputStep)) {
            boolean value = testToBeParsed.getScaling() != 0;
            test = new SetDigitalOutputStep(activity, testToBeParsed.getIoiopinnum(), value, getDescription(testToBeParsed));
            Log.d(TAG, getDescription(testToBeParsed) + " - IO State - " + value);
        } else if (classID == activity.getResources().getInteger(R.integer.SetSensorVoltagesStep)) {
            Log.d(TAG, getDescription(testToBeParsed) + " - GAIN/ZERO - " + testToBeParsed.getIoiopinnum() + "/" + testToBeParsed.getScaling());
            test = new SetSensorVoltagesStep(activity, (short) ((float) testToBeParsed.getScaling()), (short) ((int) testToBeParsed.getIoiopinnum()), getDescription(testToBeParsed));
            //);
        }
        if (test == null) {
            Log.e(TAG, "Unable to parse test #" + testToBeParsed.getId() + " !!!");
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Toast.makeText(
                            activity,
                            "Unable to parse test n� "
                                    + testToBeParsed.getId() + " !!!",
                            Toast.LENGTH_LONG).show();

                }
            });

            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return test;
        }
        test.setIdTest(testToBeParsed.getId());
        test.setIsTest(testToBeParsed.getIstest() != 0);// Default is TEST (true)
        test.setActive(testToBeParsed.getActive() != 0);// Default is active
        test.setBlockingTest(testToBeParsed.getBlocking() != 0);// Default is non blocking
        return test;
    }

    private static String getDescription(server.pojos.Test test) {
        if (test.getName() != null)
            return test.getName();
        else
            return "No test name availble";
    }
}
