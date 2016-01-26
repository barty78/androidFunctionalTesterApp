package com.pietrantuono.pericoach.newtestapp.test.getserialtest.mockito;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;

import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import server.pojos.Job;
import org.powermock.modules.junit4.*;

//@SuppressStaticInitializationFor("package com.pietrantuono.ioioutils.IOIOUtils")
@RunWith(PowerMockRunner.class)
@PrepareForTest({IOIOUtils.class,GetDeviceSerialTest.class}) // Static.class contains static methods
public class GetSerial  extends ActivityInstrumentationTestCase2<MainActivity>{
	private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private MainActivity mainActivityMock;
	
	public GetSerial() {
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
        //Fabric.with(mainActivityMock, new Crashlytics());
		
		PowerMock.mockStatic(IOIOUtils.class);
		//when(IOIOUtils.getIrange()).thenReturn(digitalOutputMock);
		Mockito.doReturn(digitalOutputMock).when(IOIOUtils.getIrange());
	}

	
	@Test 
	public void testFoo() throws ConnectionLostException {
		
		GetDeviceSerialTest getDeviceSerialTest = new GetDeviceSerialTest(mainActivityMock, ioioMock);
		
		//getDeviceSerialTest.execute();
		
		//catch (IllegalStateException e){return;}
		//verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());
	}
	

	

}
