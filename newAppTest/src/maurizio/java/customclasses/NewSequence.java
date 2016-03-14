package customclasses;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.tests.implementations.BluetoothConnectTest;
import com.pietrantuono.tests.implementations.BluetoothConnectTestForTesting;
import com.pietrantuono.tests.implementations.GetMacAddressTest;
import com.pietrantuono.tests.implementations.ReadDeviceInfoSerialNumberTest;
import com.pietrantuono.tests.implementations.SensorTestWrapper;
import com.pietrantuono.tests.implementations.steps.Step;
import com.pietrantuono.tests.implementations.upload.DummyUploadFirmwareTest;
import com.pietrantuono.tests.implementations.upload.TestUploadFirmwareTest;
import com.pietrantuono.tests.superclass.Test;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import server.TestsParser;
import server.pojos.Device;
import server.pojos.Job;
import ioio.lib.api.IOIO;
import server.pojos.Sequence;

@SuppressWarnings("unused")
public class NewSequence implements NewSequenceInterface {
    private List<Test> sequence = null;
    private volatile AtomicInteger currentStepNumber = new AtomicInteger(-1);
    private volatile Test currentStep = null;
    private long starttime = 0;
    private long endtime = 0;
    private long jobNo = 0;
    private boolean log = true;
    private Job job;

	/* (non-Javadoc)
     * @see com.pietrantuono.constants.NewSequenceInterface#Next()
	 */

    @Override
    public synchronized void executeCurrentTest() {
        try {
        } catch (Exception e) {
        }
        currentStepNumber.incrementAndGet();
        currentStep = sequence.get(currentStepNumber.get());
        currentStep.execute();
    }

    @Override
    public Boolean isSequenceStarted() {
        if (currentStepNumber.get() == -1)
            return false;
        else
            return true;
    }

    public synchronized int getCurrentTestNumber() {
        try {
        } catch (Exception e) {
        }
        return currentStepNumber.get();
    }

    @Override
    public synchronized Test getCurrentTest() {
        currentStep = sequence.get(currentStepNumber.get());
        return currentStep;
    }

    @Override
    public synchronized Test getNextTest() {
        return sequence.get(currentStepNumber.get() + 1);
    }

    private synchronized int getNexttTestNumber() {
        return currentStepNumber.get() + 1;
    }

    public synchronized String getCurrentTestNumberAsString() {
        if (currentStepNumber.get() >= 0)
            return Integer.toString(getCurrentTestNumber());
        else
            return Integer.toString(0);
    }

    @Override
    public synchronized String getCurrentTestDescription() {
        if (currentStepNumber.get() >= 0)
            return getCurrentTest().getDescription();
        else
            return sequence.get(0).getDescription();
    }

    @Override
    public synchronized String getNextTestDescription() throws Exception {
        if (currentStepNumber.get() >= 0 && currentStepNumber.get() < sequence.size() - 1)
            return getNextTest().getDescription();
        else
            return sequence.get(1).getDescription();
    }

    @Override
    public synchronized void reset() {
        currentStepNumber.set(-1);
        // currentStep=sequence.get(currentStepNumber);
    }

    public synchronized String getNexttTestNumberAsAString() {
        if (currentStepNumber.get() >= 0 && currentStepNumber.get() < sequence.size() - 1)
            return Integer.toString(getNexttTestNumber());
        else
            return ("" + 1);
    }

    public ArrayList<NewMResult> getEmptyResultsList() {
        ArrayList<NewMResult> results = new ArrayList<NewMResult>();
        for (int i = 0; i < sequence.size(); i++) {
            if (!sequence.get(i).isSensorTest())
                results.add(new NewMResult(sequence.get(i)));
                // else if(sequence.get(i) instanceof
                // ClosedTestWrapper)results.add(new
                // ClosedTestResult(sequence.get(i)));
            else
                results.add(new NewMSensorResult(sequence.get(i)));
        }
        return results;
    }

    @Override
    public void deleteUnusedTests() {
        int currentTestNumber = getCurrentTestNumber();
        sequence.subList(currentTestNumber + 1, sequence.size()).clear();
    }

    @Override
    public String getBT_Addr() {
        String mac = "";
        for (Test test : sequence) {
            if (test instanceof GetMacAddressTest) mac = ((GetMacAddressTest) test).getBT_Addr();
        }
        return mac;
    }

    @Override
    public Device getDevice() {
        return null;
    }

    @Override
    public void setDevice(Device device) {

    }

    @Override
    public int getNumberOfSteps() {
        return sequence.size();
    }

    @Override
    public Boolean isSequenceEnded() {
        return currentStepNumber.get() >= sequence.size() - 1;
    }


    @Override
    public void stopAll(MainActivity mainActivity) {
        if (sequence == null || sequence.size() <= 0)
            return;
        mainActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (Test test : sequence) {
                    try {
                        test.interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public List<Test> getSequence() {
        return sequence;
    }

    @Override
    public String getDuration() {
        DateTime start = new DateTime(starttime);
        DateTime end = new DateTime(endtime);
        Duration duration = new Duration(start, end);
        PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
        builder.minimumPrintedDigits(2);
        builder.printZeroAlways().appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSeconds();
        PeriodFormatter formatter = builder.toFormatter();
        return formatter.print(duration.toPeriod());
    }

    @Override
    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    @Override
    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    @Override
    public long getJobNo() {
        return jobNo;
    }

    @Override
    public void setJobNo(long jobNo) {
        this.jobNo = jobNo;
    }

    @Override
    public String getStartTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm:ss.SSS");
        return dateFormat.format(starttime);
    }

    @Override
    public long getOverallResult() {
        long overallresult = 1;
        for (int i = 0; i < sequence.size(); i++) {
            if (sequence.get(i) instanceof Step) continue;
            if (!sequence.get(i).isSuccess())
                overallresult = 0;
        }

        return overallresult;
    }

    @Override
    public boolean getOverallResultBool() {
        for (int i = 0; i < sequence.size(); i++) {
            if (sequence.get(i) instanceof Step) continue;
            if (!sequence.get(i).isSuccess()) return false;
        }
        return true;
    }

    public NewSequence(Activity activity, IOIO ioio, Job job, Sequence sequence) {
        setLog(false);
        this.job = job;

        this.sequence = new ArrayList<Test>();
        for (server.pojos.Test test : sequence.getTests()) {
            Test result = TestsParser.parseTest(test, activity, ioio, job);
            if (result != null) this.sequence.add(result);
        }
    }

    /**
     * ATTENTION!!! CREATES RANDOM SEQUENCE, FOR TEST ONLY!
     *
     * @param activity
     * @param ioio
     */
    public NewSequence(Activity activity, IOIO ioio, Job job) {
        setLog(true);
        this.job = job;

        sequence = new ArrayList<Test>();

        // Dummy Test Setup
        for (int i = 0; i < 10; i++) {
            //sequence.add(new DummyTest(activity, "Dummy Test "+i,  false, true));
        }


        // OLD TESTS
//		sequence.add(new VoltageTest(activity, ioio, 40, 3.0f, 0.1f, "Voltage Measurement - V_3V0"));
        // sequence.add(new VoltageTest(activity, ioio, 38, 0f, 0.01f, "Voltage
        // Measurement - DC_PRES (5V_DC Off)"));
        // sequence.add(new VoltageTest(activity, ioio, 38, 3f, 0.3f, true,
        // false, false, false, "Voltage Measurement - DC_PRES (5V_DC On)"));
        // sequence.add(new LedCheckTest(activity, "Pink", "Pink LED Check"));
//		 sequence.add(new Charge_termination_test(activity, ioio, "Battery
//		 Charge Termination Test"));
        // sequence.add(new VoltageTest(activity, ioio, 37, 2f, 0.2f, true,
        // true, false, null, "Voltage Measurement - Sleep Mode (BAT_MON)"));
        // //sequence.add(new UploadFirmwareTest(activity, ioio));
        // sequence.add(new VoltageTest(activity, ioio, 40, 3.0f, 0.1f, false,
        // null, true, false, "Voltage Measurement - Sleep Mode (V_3V0) "));
        // sequence.add(new VoltageTest(activity, ioio, 39, 0f, 0.1f, "Voltage
        // Measurement - Sleep Mode (V_3V0_SW)"));
        // sequence.add(new VoltageTest(activity, ioio, 44, 0f, 0.1f, "Voltage
        // Measurement - Sleep Mode (V_1V8)"));
        // sequence.add(new VoltageTest(activity, ioio, 32, 0f, 0.1f, "Voltage
        // Measurement - Sleep Mode (V_REF_AN)"));
        // //sequence.add(new GetDeviceSerialTest(activity, ioio));
        // //sequence.add(new WakeDeviceTest(activity, ioio));
        // sequence.add(new LedCheckTest(activity, "Green", "Green LED Check"));
        // sequence.add(new AwakeModeCurrentTest(activity, ioio, "Current
        // Measurement - Awake Mode"));
        // sequence.add(new VoltageTest(activity, ioio, 40, 3f, 0.01f, "Voltage
        // Measurement - Awake Mode (V_3V0)"));
        // sequence.add(new VoltageTest(activity, ioio, 39, 3f, 0.1f, "Voltage
        // Measurement - Awake Mode (V_3V0_SW)"));
        // sequence.add(new VoltageTest(activity, ioio, 44, 1.8f, 0.1f, "Voltage
        // Measurement - Awake Mode (V_1V8)"));
        // sequence.add(new VoltageTest(activity, ioio, 33, 3f, 0.01f, "Voltage
        // Measurement - Awake Mode (V_BT)"));
        // sequence.add(new VoltageTest(activity, ioio, 32, 1.5f, 0.2f, "Voltage
        // Measurement - Awake Mode (V_REF_AN)"));
        // sequence.add(new BluetoothDiscoverableModeTestForTesting(activity));

        // DIAG TESTS
//		sequence.add(new UartLoopbackTest(activity, ioio));

        // NEW TESTS
//		sequence.add(new GetBarcodeTest(activity,ioio,job));

//		sequence.add(new CurrentTest(activity, ioio, 42, 50, 1002, Scale.uA, true, (float)100, (float)0,
//				"Current Measurement - UUT Unprogrammed"));

//		sequence.add(new VoltageTest(activity, ioio, 44, 3.1f, 0.2f,
//				"Voltage Measurement - V_3V1"));
//		sequence.add(new VoltageTest(activity, ioio, 40, 1.8f, 0.2f,
//				"Voltage Measurement - V_1V8"));

//		sequence.add(new ChargingTerminationTest(activity, ioio,
//				"Battery Charging Termination Test"));
//		sequence.add(new VoltageTest(activity, ioio, 38, 0f, 0.1f,
//				"Voltage Measurement - DC_PRES (5V_DC Off)"));
//		sequence.add(new VoltageTest(activity, ioio, 38, 1.6f, 0.1f, true, false, false, false,
//				"Voltage Measurement - DC_PRES (5V_DC On)"));

//		sequence.add(new ChargingTest(activity, ioio,
//				"Battery Charging Test"));

//		sequence.add(new UploadFirmwareTest(activity, ioio));

//		sequence.add(new GetDeviceSerialTest(activity, ioio));
//		sequence.add(new AccelerometerSelfTest(activity, ioio));

//		sequence.add(new VoltageTest(activity, ioio, 39, 0f, 0.1f,
//				"Voltage Measurement - Sleep Mode (V_1V8_SW)"));
//		sequence.add(new VoltageTest(activity, ioio, 33, 0f, 0.1f,
//				"Voltage Measurement - Sleep Mode (V_BT)"));

//		sequence.add(new MagnetWakeDeviceTest(activity, ioio));

//		sequence.add(new CurrentTest(activity, ioio, 42, 50, 2, Scale.mA, false, (float)28, (float)0.1,
//				"Current Measurement - Awake"));
//		sequence.add(new VoltageTest(activity, ioio, 44, 3.1f, 0.1f,
//				"Voltage Measurement - Awake Mode (V_3V1)"));
//		sequence.add(new VoltageTest(activity, ioio, 39, 1.8f, 0.1f,
//				"Voltage Measurement - Awake Mode (V_1V8_SW)"));
//		sequence.add(new VoltageTest(activity, ioio, 33, 3.3f, 0.1f,
//				"Voltage Measurement - Awake Mode (V_BT)"));
//		sequence.add(new VoltageTest(activity, ioio, 32, true, -1f, -1.4f, 0.1f,
//				"Voltage Measurement - Awake Mode (V_REF_AN)"));
//		sequence.add(new VoltageTest(activity, ioio, 41, true, -1f, -0f, 0.2f,
//				"Voltage Measurement - Awake Mode (V_ZERO_AN)"));

//		sequence.add(new VoltageTest(activity, ioio, 31, -6.0f, 0.2f,
//				"Voltage Measurement - Awake Mode (-6V_RAIL)"));

//		sequence.add(new LedCheckTest(activity, "Green", "Green LED Check"));
//		sequence.add(new BluetoothConnectTestForTesting(activity));

//		sequence.add(new CurrentTest(activity, ioio, 42, 50, 2, Scale.mA, false, (float)33, (float)0.1,
//				"Current Measurement - BT Connected"));
//		sequence.add(new VoltageTest(activity, ioio, 32, true, -1f, -1.4f, 0.1f,
//				"Voltage Measurement - BT Connected Mode (V_REF_AN)"));
//		sequence.add(new LedCheckTest(activity, "Blue", "Blue LED Check"));
//		sequence.add(new ReadDeviceInfoSerialNumberTest(activity));
//		sequence.add(new ReadModelNumberTest(activity));
//		sequence.add(new ReadFirmwareversionTest(activity));

//		sequence.add(new BatteryLevelUUTVoltageTest(activity, 15, 0.1f,
//				"Battery Level - UUT voltage @ 3.5V", 100));
//		sequence.add(new BatteryLevelUUTVoltageTest(activity, 85, 0.1f,
//				"Battery Level - UUT voltage @ 4.1V", 15));

//		sequence.add(new SetSensorVoltagesStep(activity, (short)25, "Set Sensor Voltage level to 25"));
//		sequence.add(new VoltageTest(activity, ioio, 32, false, true, -1f, -0.3f, 1f,
//				"Voltage Measurement(V_REF_AN)"));
//		sequence.add(new SetSensorVoltagesStep(activity, (short)230, "Set Sensor Voltage level to 230"));
//		sequence.add(new VoltageTest(activity, ioio, 32, false, true, -1f, -2.7f, 1f,
//				"Voltage Measurement(V_REF_AN)"));
//		sequence.add(new PauseStep(activity, "Pause Step"));
//
//		sequence.add(new ChargeLedCheckTest(activity, ioio, "Pink", "Pink LED Check"));
//		sequence.add(new SensorTestWrapper(false, activity, ioio,
//				"Sensor Input Test, NO LOAD, GAIN @ 127", 0, false, (short) 127));
//		sequence.add(new SensorTestWrapper(false, activity, ioio,
//				"Sensor Input Test, LOADED, GAIN @ 127", 1, true, (short) 127));
//		sequence.add(new SensorTestWrapper(false, activity, ioio,
//				"Sensor Input Test, LOADED, GAIN @ 25", 1, true, (short) 25));
//		sequence.add(new SensorTestWrapper(false, activity, ioio,
//				"Sensor Input Test, LOADED, GAIN @ 230", 1, true, (short) 230));
//		sequence.add(new SensorTestWrapper(false, activity, ioio, "Sensor Input Test, LOADED, GAIN @ 127", 2, true,
//				(short) 127));
//		sequence.add(new SensorTestWrapper(false, activity, ioio, "Sensor Input Test, LOADED, GAIN @ 127", 3, true,
//				(short) 127));

        MyDummyTest test = new MyDummyTest.Builder().setActivity(activity).setDescription("first dummy").setIoio(ioio).setIsBlockingTest(false).createMyDummyTest();
        test.setErrorcode(999l);
        test.setSuccess(true);
        test.setIdTest(1);
        test.setValue(1);
        sequence.add(test);

        test = new MyDummyTest.Builder().setActivity(activity).setDescription("second dummy").setIoio(ioio).setIsBlockingTest(false).createMyDummyTest();
        test.setErrorcode(666l);
        test.setSuccess(true);
        test.setIdTest(1);
        test.setValue(1);
        sequence.add(test);

        test = new MyDummyTest.Builder().setActivity(activity).setDescription("third dummy").setIoio(ioio).setIsBlockingTest(false).createMyDummyTest();
        test.setErrorcode(333l);
        test.setSuccess(true);
        test.setIdTest(1);
        test.setValue(1);
        sequence.add(test);

        sequence.add(new BluetoothConnectTestForTesting(activity));

        sequence.add(new ReadDeviceInfoSerialNumberTest(activity));

        sequence.add(new SensorTestWrapper(true, activity, ioio, 3, 0, 50, 50,
                "Sensor Input Test, LOADED, GAIN/ZERO @ 127/50"));

    }


    @Override
    public void setLog(boolean log) {
        this.log = log;
    }

    @Override
    public void addTest(Test test) {
        // TODO Auto-generated method stub
    }

}
