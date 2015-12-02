package server;

import ioio.lib.api.IOIO;
import server.pojos.Job;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.pietrantuono.tests.implementations.AwakeModeCurrentTest;
import com.pietrantuono.tests.implementations.BTConnectCurrent;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTestForTesting;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTestForTesting;
import com.pietrantuono.tests.implementations.Charge_termination_test;
import com.pietrantuono.tests.implementations.DummyTest;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.pietrantuono.tests.implementations.LedCheckTest;
import com.pietrantuono.tests.implementations.ReadDeviceInfoSerialNumberTest;
import com.pietrantuono.tests.implementations.ReadFirmwareversionTest;
import com.pietrantuono.tests.implementations.ReadModelNumberTest;
import com.pietrantuono.tests.implementations.UUTCurrentTest;
import com.pietrantuono.tests.implementations.UploadFirmwareTest;
import com.pietrantuono.tests.implementations.VoltageTest;
import com.pietrantuono.tests.superclass.Test;

public class TestsParser {

	private static String TAG = "TestsParser";

	public static Test parseTest(final server.pojos.Test testToBeParsed,
			final Activity activity, IOIO ioio, Job job) {
		Test test = null;
		int dummycounter = 0;

		switch ((int) testToBeParsed.getTestclassId().intValue()) {
		case 1:
			test = new GetBarcodeTest(activity, ioio, job);
			break;
		case 2:
			float tolerance = (float) testToBeParsed.getTolerance().doubleValue();
			float nominal = (float) testToBeParsed.getNominal().doubleValue();
			int pinnumber = (int) testToBeParsed.getIoiopinnum();
			test = new UUTCurrentTest(activity, ioio,
					getDescription(testToBeParsed));
			break;
		case 3:
			tolerance = (float) testToBeParsed.getTolerance().floatValue();
			nominal = (float) testToBeParsed.getNominal().floatValue();
			pinnumber = (int) testToBeParsed.getIoiopinnum();
			test = new VoltageTest(activity, ioio, pinnumber, nominal,
					tolerance, "Voltage Measurement - DC_PRES (5V_DC Off)");
			break;
		case 4:
			test = new LedCheckTest(activity, getDescription(testToBeParsed),
					getDescription(testToBeParsed));
			break;
		case 5:
			test = new Charge_termination_test(activity, ioio,
					getDescription(testToBeParsed));
			break;
		case 6:
			test = new UploadFirmwareTest(activity, ioio);
			break;
		case 7:
			test = new GetDeviceSerialTest(activity, ioio);
			break;
		case 8:
			//test = new WakeDeviceTest(activity, ioio);
			break;
		case 9:
			test = new AwakeModeCurrentTest(activity, ioio,
					getDescription(testToBeParsed));
			break;
		case 10:
			test = new BluetoothDiscoverableModeTestForTesting(activity);//TODO 
			break;
		case 11:
			test = new BluetoothConnectTestForTesting(activity);//TODO
			break;
		case 12:
			test = new BTConnectCurrent(activity, ioio);
			break;
		case 13:
			test = new ReadDeviceInfoSerialNumberTest(activity);
			break;
		case 14:
			test = new ReadModelNumberTest(activity);
			break;
		case 15:
			test = new ReadFirmwareversionTest(activity);
			break;
		case 16:
			tolerance = (float) testToBeParsed.getTolerance().floatValue();
			nominal = (float) testToBeParsed.getNominal().floatValue();
			test = new BatteryLevelUUTVoltageTest(activity, nominal, tolerance,
					getDescription(testToBeParsed),
					(int) testToBeParsed.getNominal().doubleValue());// TODO check voltage
			break;
		case 17:
			//test=new SensorTestWrapper(activity, ioio, getDescription(testToBeParsed),
			//TestLimitIndex, isload, voltage); //TODO missing data
			break;
		case 18:
			test = new DummyTest(activity, getDescription(testToBeParsed) + " " + dummycounter + 1,  false, true);
			dummycounter++;
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
