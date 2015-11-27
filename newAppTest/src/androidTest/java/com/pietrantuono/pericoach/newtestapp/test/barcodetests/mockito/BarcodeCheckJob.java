package com.pietrantuono.pericoach.newtestapp.test.barcodetests.mockito;

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
import com.pietrantuono.tests.implementations.GetBarcodeTest;
import android.test.ActivityInstrumentationTestCase2;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import server.pojos.Job;

public class BarcodeCheckJob extends ActivityInstrumentationTestCase2<MainActivity> {
	private Job job;
	private IOIO ioioMock;
	private Uart uartMock;
	private DigitalOutput digitalOutputMock;
	private DigitalInput digitalInputMock;
	private MainActivity mainActivityMock;
	
	public BarcodeCheckJob() {
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
		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());
		
	}
	@Test
	public void testJobNull() {
		job = null;
		String barcoderead="foo";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());
		
	}
	@Test(expected=NullPointerException.class)
	public void testBarcodeNull() throws InterruptedException {
		
		job = new Job();
		job.setBarcodeprefix("1509");
		job.setQuantity(1000);
		String barcoderead="";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		assertTrue(barcodeTest.counter==0);
		try {
		barcodeTest.execute();
		} catch(NullPointerException e){
			assertTrue(!barcodeTest.isSuccess());
			assertTrue(barcodeTest.counter>0);	
			return;
			}

		fail();
	}
	
	@Test
	public void testQuantityAndPrefixOK1() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("1509");
		job.setQuantity(1000);
		String barcoderead="15090100";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();
		assertTrue(barcodeTest.isSuccess());

		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());

	}
	
	@Test
	public void testQuantityAndPrefixOK2() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("1509");
		job.setQuantity(100);
		String barcoderead="15090100";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();
		assertTrue(barcodeTest.isSuccess());

		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());

	}
	

	@Test
	public void testQuantityAndPrefixOK3() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("1509");
		job.setQuantity(1000);
		String barcoderead="15090100";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();
		assertTrue(barcodeTest.isSuccess());

		verify(mainActivityMock).addFailOrPass(true, true, barcodeTest.getDescription());

	}

	@Test
	public void testQuantityAndPrefixNotOK1() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("1509");
		job.setQuantity(1000);
		String barcoderead="15100100";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();
		assertTrue(!barcodeTest.isSuccess());

		verify(mainActivityMock, Mockito.never()).addFailOrPass(true, false, barcodeTest.getDescription());

	}
	
	@Test
	public void testQuantityAndPrefixNotOK2() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("0101");
		job.setQuantity(100);
		String barcoderead="01010200";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();
		assertTrue(!barcodeTest.isSuccess());

		
		verify(mainActivityMock,Mockito.never()).addFailOrPass(true, false, barcodeTest.getDescription());

	}
	

	@Test
	public void testQuantityAndPrefixNotOK3() throws InterruptedException {
		job = new Job();
		job.setBarcodeprefix("0101");
		job.setQuantity(1000);
		String barcoderead="01020100";
		InputStream stubInputStream =  IOUtils.toInputStream(barcoderead);
		when(uartMock.getInputStream()).thenReturn(stubInputStream);
		GetBarcodeTest barcodeTest = new GetBarcodeTest(mainActivityMock, ioioMock, job);
		barcodeTest.execute();
		//barcodeTest.
		//verify(mainActivityMock).onCurrentSequenceEnd();

		assertTrue(!barcodeTest.isSuccess());

		
		verify(mainActivityMock, Mockito.never()).addFailOrPass(true, false, barcodeTest.getDescription());

	}

	
	
	

	

}
