package server;

import ioio.lib.api.IOIO;
import server.pojos.Job;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.ioioutils.Current;
import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.tests.implementations.AccelerometerSelfTest;
import com.pietrantuono.tests.implementations.AwakeModeCurrentTest;
import com.pietrantuono.tests.implementations.BTConnectCurrent;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTestForTesting;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTest;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTestForTesting;
import com.pietrantuono.tests.implementations.ChargeLedCheckTest;
import com.pietrantuono.tests.implementations.ChargingTest;
import com.pietrantuono.tests.implementations.ChargingTerminationTest;
import com.pietrantuono.tests.implementations.CurrentTest;
import com.pietrantuono.tests.implementations.DummyTest;
import com.pietrantuono.tests.implementations.DummyUploadFirmwareTest;
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
import com.pietrantuono.tests.implementations.UploadFirmwareTest;
import com.pietrantuono.tests.implementations.VoltageTest;
import com.pietrantuono.tests.implementations.WakeDeviceTest;
import com.pietrantuono.tests.implementations.steps.PauseStep;
import com.pietrantuono.tests.implementations.steps.PromptStep;
import com.pietrantuono.tests.implementations.steps.SetDigitalOutputStep;
import com.pietrantuono.tests.implementations.steps.SetSensorVoltagesStep;
import com.pietrantuono.tests.superclass.Test;

public class TestsParser {

    private static String TAG = TestsParser.class.getSimpleName();

    public static Test parseTest(final server.pojos.Test testToBeParsed,
                                 final Activity activity, IOIO ioio, Job job) {
        Test test = null;
        int dummycounter = 0;
        int classID = (int) testToBeParsed.getTestclassId().intValue();

        if (classID == activity.getResources().getInteger(R.integer.GetBarcodeTest)) {
            float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
            test = new GetBarcodeTest(activity, ioio, job, limitParam1);

        } else if (classID == activity.getResources().getInteger(R.integer.CurrentTest)) {
            boolean isNominal = testToBeParsed.getIsNominal() == 1;
            float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
            float limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
            int pinnumber = (int) testToBeParsed.getIoiopinnum();
            test = new CurrentTest(activity, ioio, pinnumber, Current.Units.mA, isNominal, limitParam1, limitParam2,
                    getDescription(testToBeParsed));

        } else if (classID == activity.getResources().getInteger(R.integer.VoltageTest)) {
            boolean isNominal = testToBeParsed.getIsNominal() == 1;
            float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
            float limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
            boolean isBlocking = testToBeParsed.getBlocking() != 0;
            int pinnumber = (int) testToBeParsed.getIoiopinnum();
            test = new VoltageTest(activity, ioio, pinnumber, Voltage.Units.V, isBlocking, isNominal, limitParam1, limitParam2,
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.LedCheckTest)) {
            test = new LedCheckTest(activity, getDescription(testToBeParsed),
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.ChargingTerminationTest)) {
            test = new ChargingTerminationTest(activity, ioio,
                    getDescription(testToBeParsed));

        } else if (classID == activity.getResources().getInteger(R.integer.UploadFirmwareTest)) {
            test = new UploadFirmwareTest(activity, ioio);
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

            test = new BluetoothConnectTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.BluetoothConnectTestForTesting)) {
            test = new BluetoothConnectTestForTesting(activity);//TODO
        } else if (classID == activity.getResources().getInteger(R.integer.BTConnectCurrent)) {
            test = new BTConnectCurrent(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadDeviceInfoSerialNumberTest)) {
            test = new ReadDeviceInfoSerialNumberTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadModelNumberTest)) {
            test = new ReadModelNumberTest(activity);
        } else if (classID == activity.getResources().getInteger(R.integer.ReadFirmwareversionTest)) {
            test = new ReadFirmwareversionTest(activity);
        }
        else if (classID == activity.getResources().getInteger(R.integer.BatteryLevelUUTVoltageTest)) {
            float tolerance = (float) testToBeParsed.getScaling().floatValue();//TODO doublecheck if is correct to use Scaling
            test = new BatteryLevelUUTVoltageTest(activity,
                    testToBeParsed.getLimitId(),
                    tolerance,
                    getDescription(testToBeParsed),
                    (int) testToBeParsed.getNominal().doubleValue());// TODO check voltage
        } else if (classID == activity.getResources().getInteger(R.integer.SensorTestWrapper)) {
            test = new SensorTestWrapper(job.getTesttypeId()!=0, activity, ioio, (int) ((long) testToBeParsed.getLimitId()), testToBeParsed.getLimitParam1(), testToBeParsed.getLimitParam2(), testToBeParsed.getLimitParam3(), testToBeParsed.getName());//TODO dublecheck
        } else if (classID == activity.getResources().getInteger(R.integer.DummyTest)) {
            test = new DummyTest(activity, getDescription(testToBeParsed) + " " + dummycounter + 1, false, true);
            dummycounter++;
        } else if (classID == activity.getResources().getInteger(R.integer.ChargingTest)) {
            test = new ChargingTest(activity, ioio,
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.AccelerometerSelfTest)) {
            test = new AccelerometerSelfTest(activity, ioio);
        } else if (classID == activity.getResources().getInteger(R.integer.ChargeLedCheckTest)) {
            test = new ChargeLedCheckTest(activity, ioio, getDescription(testToBeParsed),
                    getDescription(testToBeParsed));
        } else if (classID == activity.getResources().getInteger(R.integer.DummyUploadFirmwareTest)) {
            test = new DummyUploadFirmwareTest(activity, ioio, false); //TODO - Get boolean for loopback from db field.
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
        } else if (classID == activity.getResources().getInteger(R.integer.PauseStep)) {
            test = new PauseStep(activity, "Pause Step");
        } else if (classID == activity.getResources().getInteger(R.integer.PromptStep)) {
            test = new PromptStep(activity, "Prompt Step");
        } else if (classID == activity.getResources().getInteger(R.integer.SetDigitalOutputStep)) {
            boolean value = testToBeParsed.getBlocking() > 0 ? true : false;
            test = new SetDigitalOutputStep(activity, testToBeParsed.getIoiopinnum(), value, "Set Digital Output Step");
        } else if (classID == activity.getResources().getInteger(R.integer.SetSensorVoltagesStep)) {
            test = new SetSensorVoltagesStep(activity, (short) ((int) testToBeParsed.getIoiopinnum()), (short) ((int) testToBeParsed.getScaling()), "Set Sensor Voltages Step");
            //);
        }
        if (test == null) {
            Log.e(TAG, "Unable to parse test n " + testToBeParsed.getId() + " !!!");
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

        if (testToBeParsed.getIstest() != 1) {
            test.setIsTest(false);// Default is Test (true)
        }

        if (testToBeParsed.getActive() != 1) {
            test.setActive(false);// Default is active
        }
        if (testToBeParsed.getBlocking() == 1) {
            test.setBlockingTest(true);// Default is non blocking
        }
        test.setIdTest(testToBeParsed.getId());
        return test;
    }

    private static String getDescription(server.pojos.Test test) {
        if (test.getName() != null)
            return test.getName();
        else
            return "No test name availble";
    }
}
