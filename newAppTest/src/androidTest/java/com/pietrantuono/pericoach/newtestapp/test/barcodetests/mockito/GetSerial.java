package com.pietrantuono.pericoach.newtestapp.test.barcodetests.mockito;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.GetDeviceSerialTest;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;

public class GetSerial extends ActivityInstrumentationTestCase2<MainActivity> {
	// private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private MainActivity mainActivityMock;
	private MainActivity mainActivity;
	private IOIOUtilsMock ioioUtilsInterfaceMock;
	private Solo solo;

	public GetSerial() {
		super(MainActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
		mainActivity = getActivity();
		ioioMock = Mockito.mock(IOIO.class);
		uartMock = Mockito.mock(Uart.class);
		digitalOutputMock = Mockito.mock(DigitalOutput.class);
		mainActivityMock=Mockito.mock(MainActivity.class);
		when(ioioMock.openUart(anyInt(), anyInt(), anyInt(), any(Uart.Parity.class), any(Uart.StopBits.class)))
				.thenReturn(uartMock);
		when(ioioMock.openDigitalOutput(anyInt(), any(DigitalOutput.Spec.Mode.class), anyBoolean()))
				.thenReturn(digitalOutputMock);
		digitalInputMock = Mockito.mock(DigitalInput.class);
		when(ioioMock.openDigitalInput(anyInt(), any(DigitalInput.Spec.Mode.class))).thenReturn(digitalInputMock);
		ioioUtilsInterfaceMock = Mockito.mock(IOIOUtilsMock.class);
		IOIOUtils.setIOIOUtilsInterface(ioioUtilsInterfaceMock);
		when(ioioUtilsInterfaceMock.getIrange()).thenReturn(digitalOutputMock);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Test(expected=NullPointerException.class)
	public void testEmptySerial() throws InterruptedException {
		StringBuilder stringBuilder = new StringBuilder("");
		when(ioioUtilsInterfaceMock.getUartLog()).thenReturn(stringBuilder);
		InputStream stubInputStream = IOUtils.toInputStream("");
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetDeviceSerialTest serialTest = new GetDeviceSerialTest(mainActivity, ioioMock);
		try {
			serialTest.execute();
			} catch(NullPointerException e){
				assertTrue(!serialTest.isSuccess());
				assertTrue(serialTest.counter2>0);	
				return;
				}
		
		Thread.sleep(3 * 1000);
		solo.clickOnButton("OK");
		Thread.sleep(3 * 1000);
		solo.clickOnButton("OK");
		Thread.sleep(3 * 1000);
		solo.clickOnButton("OK");
		Thread.sleep(2*1000);
		//assertTrue(!serialTest.isSuccess());
	}

	@Test
	public void testNotOK() {
		String barcoderead = "itoa16:";
		InputStream stubInputStream = IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetDeviceSerialTest serialTest = new GetDeviceSerialTest(mainActivity, ioioMock);
		serialTest.execute();
		// verify(mainActivityMock).addFailOrPass(true, true,
		// barcodeTest.getDescription());

	}

}
