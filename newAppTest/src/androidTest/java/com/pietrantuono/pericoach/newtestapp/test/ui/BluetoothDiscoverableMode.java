package com.pietrantuono.pericoach.newtestapp.test.ui;

import java.io.IOException;
import org.mockito.Mockito;
import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.IOIOMock;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.sequences.GenericSequence;
import com.pietrantuono.tests.implementations.BluetoothDiscoverableModeTest;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ioio.lib.api.exception.ConnectionLostException;
import server.pojos.Firmware;

public class BluetoothDiscoverableMode extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mainActivity;
	private Solo solo;

	public BluetoothDiscoverableMode() {
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
		
		solo = new Solo(getInstrumentation(), getActivity());

		
		NewSequenceInterface newSequence = new GenericSequence();	
		com.pietrantuono.tests.superclass.Test test;
		test= new BluetoothDiscoverableModeTest(mainActivity);
		newSequence.addTest(test);
		
		mainActivity.setNewSequence(newSequence);
		
		
		Firmware firmware= Mockito.mock(Firmware.class);
		PeriCoachTestApplication.setGetFirmware(firmware);
		
		PeriCoachTestApplication.setFirmwareFileForTest();
	}
}
