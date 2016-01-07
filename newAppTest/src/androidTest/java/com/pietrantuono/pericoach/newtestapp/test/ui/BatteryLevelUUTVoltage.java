package com.pietrantuono.pericoach.newtestapp.test.ui;

import static org.mockito.Mockito.when;
import java.io.IOException;
import org.mockito.Mockito;
import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.btutility.BTUtility;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.test.barcodetests.mockito.IOIOUtilsMock;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.IOIOMock;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.sequences.GenericSequence;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import server.pojos.Firmware;

public class BatteryLevelUUTVoltage extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mainActivity;
	private IOIOUtilsMock ioioUtilsInterfaceMock;
	private Solo solo;

	public BatteryLevelUUTVoltage() {
		super(MainActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
		
	}

	public void test1() throws InterruptedException, ConnectionLostException, IOException {
		setAllUp();
		solo.clickOnButton("");
		Thread.sleep(5 * 60 * 1000);

	}

	public void setAllUp() throws ConnectionLostException, IOException{
		
		Mockito.mock(IOIOMock.class);
		mainActivity = getActivity();
		
		Mockito.mock(Uart.class);
		solo = new Solo(getInstrumentation(), getActivity());

		ioioUtilsInterfaceMock = Mockito.mock(IOIOUtilsMock.class);

		IOIOUtils.setIOIOUtilsInterface(ioioUtilsInterfaceMock);
		
		NewSequenceInterface newSequence = new GenericSequence();	
		com.pietrantuono.tests.superclass.Test test;
		test= new BatteryLevelUUTVoltageTest(mainActivity,"registerSequenceFragment",1);
		newSequence.addTest(test);
		
		mainActivity.setNewSequence(newSequence);
		
		BTUtility btUtility=Mockito.mock(BTUtility.class);
		when(btUtility.getBatteryLevel()).thenReturn((short)0);
		mainActivity.setBTUtility(btUtility);
		
		Firmware firmware= Mockito.mock(Firmware.class);
		PeriCoachTestApplication.setGetFirmware(firmware);
		
		PeriCoachTestApplication.setFirmwareFileForTest();
	}
}
