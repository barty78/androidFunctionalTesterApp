package com.pietrantuono.ioioutils;

import java.io.InputStream;
import java.io.OutputStream;

import com.pietrantuono.activities.NewIOIOActivityListener;

import android.app.Activity;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;

public interface IOIOUtilsInterface {

	//String getBarcode(NewIOIOActivityListener listner, IOIO ioio_, Activity activity);

	void closeall(NewIOIOActivityListener listener, Activity ac);

	void initialize(NewIOIOActivityListener listner, IOIO ioio_, Activity ac);

	void ioioSync(IOIO ioio_);

	boolean setBattVoltage(IOIO ioio_, float voltage);

	DigitalOutput getDigitalOutput(int pinNumber);

	void resetUart2(IOIO ioio_, Activity ac);

	DigitalOutput getSensor_Low();

	DigitalOutput getSensor_High();

	DigitalOutput get_5V_DC();

	DigitalInput getCHGPinIn();

	DigitalOutput getCHGPinOut();

	void driveChargeLed(IOIO ioio_, Activity ac);

	TwiMaster getMaster();

	@SuppressWarnings("ucd")
	DigitalOutput getPOWER();

	@SuppressWarnings("ucd")
	DigitalOutput getReset();

	@SuppressWarnings("ucd")
	DigitalOutput getHallInt();

	void resetDevice(Activity activity);

	void toggle5VDC(Activity activity);

	int readPulseWithTimeout(DigitalInput input);

	@SuppressWarnings("ucd")
	void toggleTrigger(Activity activity);
	
	@SuppressWarnings("ucd")
	void toggleHall(Activity activity);
	
	void toggleEMag(Activity activity);
	
	void modeApplication(Activity activity);

	IOIOUtils.Mode getUutMode(Activity activity);

//	IOIOUtils.Mode getUutMode(Activity activity);

	DigitalOutput getIrange();
	
	@SuppressWarnings("ucd")
	void setIrange(Activity activity, Boolean value);

	Uart getIOIOUart();

	String getUartLog();

	void clearUartLog();

	void closeUart(Activity activity);

	String readBarcode(Activity activity);

	@SuppressWarnings("ucd")
	InputStream getUartInStream();

	@SuppressWarnings("ucd")
	OutputStream getUartOutStream();

	void stopUartThread();

	DigitalOutput getEmag();

}