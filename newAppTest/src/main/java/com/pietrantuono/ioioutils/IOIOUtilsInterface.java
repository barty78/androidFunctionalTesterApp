package com.pietrantuono.ioioutils;

import java.io.InputStream;
import java.io.OutputStream;

import com.pietrantuono.activities.NewIOIOActivityListener;

import android.app.Activity;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;

public interface IOIOUtilsInterface {

	//String getBarcode(NewIOIOActivityListener listner, IOIO ioio_, Activity activity);

	void closeall(NewIOIOActivityListener listener, Activity ac);

	void initialize(NewIOIOActivityListener listner, IOIO ioio_, Activity ac);

	DigitalOutput getSensor_Low();

	DigitalOutput getSensor_High();

	DigitalOutput get_5V_DC();

	TwiMaster getMaster();

	@SuppressWarnings("ucd")
	DigitalOutput getPOWER();

	@SuppressWarnings("ucd")
	DigitalOutput getReset();

	@SuppressWarnings("ucd")
	DigitalOutput getHallInt();

	void resetDevice(Activity activity);

	void toggle5VDC(Activity activity);

	@SuppressWarnings("ucd")
	void toggleTrigger(Activity activity);
	
	@SuppressWarnings("ucd")
	void toggleHall(Activity activity);
	
	void toggleEMag(Activity activity);
	
	void modeApplication(Activity activity);

	DigitalOutput getIrange();
	
	@SuppressWarnings("ucd")
	void setIrange(Activity activity, Boolean value);

	Uart getIOIOUart();

	StringBuilder getUartLog();

	String readBarcode(Activity activity);

	@SuppressWarnings("ucd")
	void stop();

	@SuppressWarnings("ucd")
	InputStream getUartInStream();

	@SuppressWarnings("ucd")
	OutputStream getUartOutStream();

	void stopUartThread();

	DigitalOutput getEmag();

}