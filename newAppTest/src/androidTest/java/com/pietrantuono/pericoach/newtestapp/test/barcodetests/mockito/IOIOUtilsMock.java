package com.pietrantuono.pericoach.newtestapp.test.barcodetests.mockito;

import java.io.InputStream;
import java.io.OutputStream;

import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.ioioutils.IOIOUtilsInterface;

import android.app.Activity;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;

public class IOIOUtilsMock implements IOIOUtilsInterface {

	@Override
	public String getBarcode(NewIOIOActivityListener listner, IOIO ioio_, Activity activity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeall(NewIOIOActivityListener listener, Activity ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(NewIOIOActivityListener listner, IOIO ioio_, Activity ac) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DigitalOutput getSensor_Low() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput getSensor_High() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput get_5V_DC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TwiMaster getMaster() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput getPOWER() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput getReset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput getHallInt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetDevice(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggle5VDC(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleTrigger(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleHall(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	public void modeBootloader(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modeApplication(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DigitalOutput getIrange() {
		// TODO Auto-generated method stub
		return null;
	}

	public DigitalOutput getBoot0() {
		// TODO Auto-generated method stub
		return null;
	}

	public DigitalOutput getBoot1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uart getIOIOUart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder getUartLog() {
		// TODO Auto-generated method stub
		return null;
	}

	public void flushUartLog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getUartInStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getUartOutStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopUartThread() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toggleEMag(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIrange(Activity activity, Boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DigitalOutput getEmag() {
		// TODO Auto-generated method stub
		return null;
	}

}
