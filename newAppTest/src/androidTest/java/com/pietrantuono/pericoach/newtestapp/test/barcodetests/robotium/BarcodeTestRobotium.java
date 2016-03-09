package com.pietrantuono.pericoach.newtestapp.test.barcodetests.robotium;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.NewNewSequence;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Job;

public class BarcodeTestRobotium extends ActivityInstrumentationTestCase2<MainActivity> {
	MainActivity activity;
	private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private Solo solo;

	public BarcodeTestRobotium() {
		super(MainActivity.class);
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(
			    "dexmaker.dexcache",
			    getInstrumentation().getTargetContext().getCacheDir().getPath());
		activity=getActivity();
		ioioMock = Mockito.mock(IOIO.class);
		uartMock=Mockito.mock(Uart.class);
		digitalOutputMock=Mockito.mock(DigitalOutput.class);
		when(ioioMock.openUart(anyInt(), anyInt(), anyInt(), any(Uart.Parity.class), any(Uart.StopBits.class)))
		.thenReturn(uartMock );
		when(ioioMock.openDigitalOutput(anyInt(), any(DigitalOutput.Spec.Mode.class), anyBoolean())).thenReturn(digitalOutputMock);
		digitalInputMock=Mockito.mock(DigitalInput.class);
		when(ioioMock.openDigitalInput(anyInt(),any(DigitalInput.Spec.Mode.class))).thenReturn(digitalInputMock);
		solo = new Solo(getInstrumentation(), getActivity());

	}

	@Test
	public void testEmpty() {
		job = new Job();
		String barcoderead="";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(activity, ioioMock, job);
		barcodeTest.execute();
		//verify(activity).runOnUiThread(any(Runnable.class));;
		//assertTrue(true);
	    assertTrue("Could not find the dialog!", solo.searchText("Unable to read barcode"));

	}
//	@TEST
//	public void testNull() {
//		job = new Job();
//		String barcoderead="fff";
//		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
//		when(uartMock.getInputStream()).thenReturn(stubInputStream);
//		GetBarcodeTest barcodeTest = new GetBarcodeTest(activity, ioioMock, job);
//		barcodeTest.execute();
//		verify(activity).runOnUiThread(any(Runnable.class));;
//		//assertTrue(true);
//	    //assertTrue("Could not find the dialog!", solo.searchText("Unable to read barcode"));
//
//	}

//	@TEST
//	public void testFoo2() throws InterruptedException {
//		job = new Job();
//		job.setBarcodeprefix(1000);
//		job.setQuantity(1000);
//		
//		String barcoderead="isSuccess";
//		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
//		when(uartMock.getInputStream()).thenReturn(stubInputStream);
//		//activity.setSequence( new NewNewSequence(null,null ));
//		GetBarcodeTest barcodeTest = new GetBarcodeTest(activity, ioioMock, job);
//		barcodeTest.execute();
//		//Thread.sleep(10*1000);
//	    assertTrue("Could not find the toast!", solo.searchText("Invalid barcode! Aborting test"));
//		
//	}
	

}
