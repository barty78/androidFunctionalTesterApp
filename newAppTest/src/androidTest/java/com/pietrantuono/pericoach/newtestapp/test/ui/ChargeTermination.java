package com.pietrantuono.pericoach.newtestapp.test.ui;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.mockito.Mockito;
import com.pietrantuono.activities.MainActivity;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.test.barcodetests.mockito.IOIOUtilsMock;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.IOIOMock;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.MyLooperForTest;
import com.pietrantuono.pericoach.newtestapp.test.ui.classes.sequences.GenericSequence;
import com.pietrantuono.tests.implementations.Charge_termination_test;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;
import server.pojos.Firmware;

public class ChargeTermination extends ActivityInstrumentationTestCase2<MainActivity> {
	// private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private MainActivity mainActivity;
	private IOIOUtilsMock ioioUtilsInterfaceMock;
	private Solo solo;

	public ChargeTermination() {
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
		IOIOLooper looper = mainActivity.createIOIOLooper(null, 0);
		looper.setup(null);
		Thread.sleep(5 * 60 * 1000);

	}

	public void setAllUp() throws ConnectionLostException, IOException{
		
		ioioMock = Mockito.mock(IOIOMock.class);
		mainActivity = getActivity();
		
		uartMock = Mockito.mock(Uart.class);
		solo = new Solo(getInstrumentation(), getActivity());

		digitalOutputMock = Mockito.mock(DigitalOutput.class);

		when(ioioMock.openDigitalOutput(anyInt(), any(DigitalOutput.Spec.Mode.class), anyBoolean()))
				.thenReturn(digitalOutputMock);
		digitalInputMock = Mockito.mock(DigitalInput.class);
		when(ioioMock.openDigitalInput(anyInt(), any(DigitalInput.Spec.Mode.class))).thenReturn(digitalInputMock);
		ioioUtilsInterfaceMock = Mockito.mock(IOIOUtilsMock.class);
		IOIOUtils.setIOIOUtilsInterface(ioioUtilsInterfaceMock);
		when(ioioUtilsInterfaceMock.getIrange()).thenReturn(digitalOutputMock);
		when(ioioUtilsInterfaceMock.getIOIOUart()).thenReturn(uartMock);
		when(ioioUtilsInterfaceMock.get_5V_DC()).thenReturn(digitalOutputMock);
		
		InputStream stubInputStream = Mockito.mock(InputStream.class);
		when(stubInputStream.read()).thenReturn(0x79);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		OutputStream stubOutputStream = Mockito.mock(OutputStream.class);
		when(uartMock.getOutputStream()).thenReturn(stubOutputStream);
		when(ioioMock.openUart(anyInt(), anyInt(), anyInt(), any(Uart.Parity.class), any(Uart.StopBits.class)))
				.thenReturn(uartMock);
		AnalogInput analogInput=Mockito.mock(AnalogInput.class);
		when(ioioMock.openAnalogInput(anyInt())).thenReturn(analogInput);
		
		NewSequenceInterface newSequence = new GenericSequence();
		com.pietrantuono.tests.superclass.Test test;
		test= new Charge_termination_test(mainActivity,ioioMock,"bar");
		newSequence.addTest(test);
		
		mainActivity.setNewSequence(newSequence);
		
		
		Firmware firmware= Mockito.mock(Firmware.class);
		PeriCoachTestApplication.setGetFirmware(firmware);
		
		PeriCoachTestApplication.setFirmwareFileForTest();
		
		MyLooperForTest looper = new MyLooperForTest(mainActivity, ioioMock);
		mainActivity.setLooper(looper);
	}
}
