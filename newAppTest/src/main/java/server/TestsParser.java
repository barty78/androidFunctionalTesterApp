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
import com.pietrantuono.tests.superclass.Test;

public class TestsParser {

	private static String TAG = TestsParser.class.getSimpleName();

	public static Test parseTest(final server.pojos.Test testToBeParsed,
			final Activity activity, IOIO ioio, Job job) {
		Test test = null;
		int dummycounter = 0;

		switch ((int) testToBeParsed.getTestclassId().intValue()) {
			case R.integer.GetBarcodeTest:
				float limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();

				test = new GetBarcodeTest(activity, ioio, job, limitParam1);
				break;

			case R.integer.CurrentTest:
				boolean isNominal = testToBeParsed.getIsNominal() == 1;
				limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
				float limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
				int pinnumber = (int) testToBeParsed.getIoiopinnum();
				test = new CurrentTest(activity, ioio, pinnumber, Current.Units.mA, isNominal, limitParam1, limitParam2,
						getDescription(testToBeParsed));
				break;

			case R.integer.VoltageTest:
				isNominal = testToBeParsed.getIsNominal() == 1;
				limitParam1 = (float) testToBeParsed.getLimitParam1().doubleValue();
				limitParam2 = (float) testToBeParsed.getLimitParam2().doubleValue();
				pinnumber = (int) testToBeParsed.getIoiopinnum();
				test = new VoltageTest(activity, ioio, pinnumber, Voltage.Units.V, isNominal, limitParam1, limitParam2,
						"Voltage Measurement - DC_PRES (5V_DC Off)");
				break;

			case R.integer.LedCheckTest:
				test = new LedCheckTest(activity, getDescription(testToBeParsed),
						getDescription(testToBeParsed));
				break;

			case R.integer.ChargingTerminationTest:
				test = new ChargingTerminationTest(activity, ioio,
						getDescription(testToBeParsed));
				break;

			case R.integer.UploadFirmwareTest:
				test = new UploadFirmwareTest(activity, ioio);
				break;

			case R.integer.GetDeviceSerialTest:
				test = new GetDeviceSerialTest(activity, ioio);
				break;

			case R.integer.WakeDeviceTest:
				test = new WakeDeviceTest(activity, ioio);
				break;

			case R.integer.AwakeModeCurrentTest:
				test = new AwakeModeCurrentTest(activity, ioio,
						getDescription(testToBeParsed));
				break;

			case R.integer.BluetoothDiscoverableModeTest:
				test = new BluetoothDiscoverableModeTest(activity);
				break;

			case R.integer.BluetoothDiscoverableModeTestForTesting:
				test = new BluetoothDiscoverableModeTestForTesting(activity);//TODO
				break;

			case R.integer.BluetoothConnectTest:
				test = new BluetoothConnectTest(activity);
				break;

			case R.integer.BluetoothConnectTestForTesting:
				test = new BluetoothConnectTestForTesting(activity);//TODO
				break;

			case R.integer.BTConnectCurrent:
				test = new BTConnectCurrent(activity, ioio);
				break;

			case R.integer.ReadDeviceInfoSerialNumberTest:
				test = new ReadDeviceInfoSerialNumberTest(activity);
				break;

			case R.integer.ReadModelNumberTest:
				test = new ReadModelNumberTest(activity);
				break;

			case R.integer.ReadFirmwareversionTest:
				test = new ReadFirmwareversionTest(activity);
				break;

			case R.integer.BatteryLevelUUTVoltageTest:
				float tolerance = (float) testToBeParsed.getTolerance().floatValue();
				float nominal = (float) testToBeParsed.getNominal().floatValue();
				test = new BatteryLevelUUTVoltageTest(activity, nominal, tolerance,
						getDescription(testToBeParsed),
						(int) testToBeParsed.getNominal().doubleValue());// TODO check voltage
				break;

			case R.integer.SensorTestWrapper:
				//test= new SensorTestWrapper.SensorTestWrapperBuilder().Builder()
				break;

			case R.integer.DummyTest:
				test = new DummyTest(activity, getDescription(testToBeParsed) + " " + dummycounter + 1, false, true);
				dummycounter++;
				break;

			case R.integer.ChargingTest:
				test = new ChargingTest(activity, ioio,
						getDescription(testToBeParsed));
				break;

			case R.integer.AccelerometerSelfTest:
				test = new AccelerometerSelfTest(activity, ioio);
				break;

			case R.integer.ChargeLedCheckTest:
				test = new ChargeLedCheckTest(activity, ioio, getDescription(testToBeParsed),
						getDescription(testToBeParsed));
				break;

			case R.integer.DummyUploadFirmwareTest:
				test = new DummyUploadFirmwareTest(activity, ioio, false); //TODO - Get boolean for loopback from db field.
				break;

			case R.integer.GetMacAddressTest:
				test = new GetMacAddressTest(activity, ioio);
				break;

			case R.integer.ListenToUart:
				test = new ListenToUart(activity, ioio);
				break;

			case R.integer.MagnetWakeDeviceTest:
				test = new MagnetWakeDeviceTest(activity, ioio);
				break;

			case R.integer.UartBlockWriteTest:
				test = new UartBlockWriteTest(activity, ioio);
				break;

			case R.integer.UartLoopbackTest:
				test = new UartLoopbackTest(activity, ioio);
				break;

			case R.integer.UUTCurrentTest:
				test = new UUTCurrentTest(activity, ioio,
						getDescription(testToBeParsed));
				break;

		}
			

		if (test == null) {
			Log.e(TAG,"Unable to parse test n "+ testToBeParsed.getId() + " !!!");
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
