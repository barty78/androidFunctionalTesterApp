package com.pietrantuono.pericoach.newtestapp.test.getserialtest.mockito;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import com.pietrantuono.ioioutils.IOIOUtils;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({IOIOUtils.class,GetDeviceSerialTest.class}) // Static.class contains static methods
public class Foo {
	private IOIO ioioMock;
//	private Uart uartMock;
//	private DigitalOutput digitalOutputMock;
//	private DigitalInput digitalInputMock;
	@Before
	public void setUp() throws Exception {
//		ioioMock = Mockito.mock(IOIO.class);
//		uartMock=Mockito.mock(Uart.class);
//		digitalOutputMock=Mockito.mock(DigitalOutput.class);
//		when(ioioMock.openUart(anyInt(), anyInt(), anyInt(), any(Uart.Parity.class), any(Uart.StopBits.class)))
//		.thenReturn(uartMock );
//		when(ioioMock.openDigitalOutput(anyInt(), any(DigitalOutput.Spec.Mode.class), anyBoolean())).thenReturn(digitalOutputMock);
//		digitalInputMock=Mockito.mock(DigitalInput.class);
//		when(ioioMock.openDigitalInput(anyInt(),any(DigitalInput.Spec.Mode.class))).thenReturn(digitalInputMock);
//		
		PowerMock.mockStatic(IOIOUtils.class);
//		when(IOIOUtils.getIrange()).thenReturn(digitalOutputMock);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Fooooo!!!!!!!!!!!!");
		
	}

}
