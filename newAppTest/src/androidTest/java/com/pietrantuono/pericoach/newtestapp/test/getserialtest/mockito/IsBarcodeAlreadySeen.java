package com.pietrantuono.pericoach.newtestapp.test.getserialtest.mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.constants.LimitsProvider;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.sensors.ClosedTest;
import com.pietrantuono.sensors.NewPFMATDevice;
import com.pietrantuono.sensors.SensorTest;
import com.pietrantuono.sensors.SensorsTestHelper;
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import com.robotium.solo.Solo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import hydrix.pfmat.generic.Force;
import hydrix.pfmat.generic.SessionSamples;
import hydrix.pfmat.generic.TestLimits;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Job;

public class IsBarcodeAlreadySeen extends ActivityInstrumentationTestCase2<MainActivity> {
	private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private MainActivity mainActivityMock;
	
	public IsBarcodeAlreadySeen() {
		super(MainActivity.class);
		
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty(
			    "dexmaker.dexcache",
			    getInstrumentation().getTargetContext().getCacheDir().getPath());
		ioioMock = Mockito.mock(IOIO.class);
		uartMock=Mockito.mock(Uart.class);
		digitalOutputMock=Mockito.mock(DigitalOutput.class);
		mainActivityMock=Mockito.mock(MainActivity.class);
		when(ioioMock.openUart(anyInt(), anyInt(), anyInt(), any(Uart.Parity.class), any(Uart.StopBits.class)))
		.thenReturn(uartMock );
		when(ioioMock.openDigitalOutput(anyInt(), any(DigitalOutput.Spec.Mode.class), anyBoolean())).thenReturn(digitalOutputMock);
		digitalInputMock=Mockito.mock(DigitalInput.class);
		when(ioioMock.openDigitalInput(anyInt(),any(DigitalInput.Spec.Mode.class))).thenReturn(digitalInputMock);

	}

	
	@Test
	public void testFoo() {
		job = new Job();
		String barcoderead="foo";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		assertTrue(barcodeTest.isSuccess());
		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());
		
	}
	@Test
	public void test123456() {
		job = new Job();
		String barcoderead="123456";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		assertTrue(!barcodeTest.isSuccess());
		verify(mainActivityMock).onCurrentSequenceEnd();

		
	}
	

}
