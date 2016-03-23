package com.pietrantuono.ioioutils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;


public class Current {
	private static final DecimalFormat df = new DecimalFormat("##.##");
	private static Boolean isinterrupted=false;;
	private static int currentsleeptime=0;

	//@Units
	private static double units;

	private static void setUnits(@Units int units){
		Current current = new Current();
		current.units = 1 * Math.pow(10, units);
	}

	private static double getUnits() {
		return units;
	}

//	public enum Units {
//
//		mA	((double)1E3),
//		uA	((double)1E6),
//		nA	((double)1E9);
//
//		public Double value;
//
//		public Double getValue() {
//			return value;
//		}
//
//		Units(double value) {
//			this.value = value;
//		}
//	}
//
	
	
	/**
	 * Gets Current by doing 10 readings and waiting 0 ms between readings
	 * 
	 * This method does NOT sleep between readings
	 * 
	 * @param ioio			- IOIO Instance
	 * @param pinNumber		- IOIO Pin Number
	 * @param gain			- Current sense circuit gain
	 * @param Rshunt		- Current sense shunt resistor value
	 * @param units			- Reading units (mA, uA, nA)
	 * @throws Exception
	 */
	private static float getCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, @Units int units) throws Exception {
		setUnits(units);
		return getCurrent(ioio, pinNumber, gain, Rshunt, getUnits(), 10, 0);
	}
		
	
	/**
	 * Gets current doing numberofreadings readings and waiting sleeptime ms between readings
	 * 
	 * This method does DOES SLEEP between readings
	 *
	 * @param ioio			- IOIO Instance
	 * @param pinNumber		- IOIO Pin Number
	 * @param gain			- Current sense circuit gain
	 * @param Rshunt		- Current sense shunt resistor value
	 * @param units			- Reading units (mA, uA, nA)
	 * @param sleeptime
	 * @param numberofreadings
	 * @return average reading
	 * @throws Exception
	 */
	private static float getCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, double units, int numberofreadings, int sleeptime) throws Exception {
		if(isinterrupted)return 0f;
		currentsleeptime=sleeptime;
		if (ioio == null)
			throw new Exception("IOIO is null!");
		if (pinNumber == 0)
			throw new Exception("Pin number is 0");
		AnalogInput analogInput = null;
		analogInput = ioio.openAnalogInput(pinNumber);
		if (analogInput == null)
			throw new Exception("Unable to open Analog Input");

		float total = 0;
		int numsamples = numberofreadings;
		for (int i = 0; i < numsamples; i++) {
			if(isinterrupted)break;
			total = total + analogInput.getVoltage();
			Thread.sleep(sleeptime);
		}
		analogInput.close();
		float average = (float) (((total / numsamples) / (gain * Rshunt)) * units);
		Log.d("GAIN", String.valueOf(gain));
		Log.d("SHUNT", String.valueOf(Rshunt));
		Log.d("UNITS", String.valueOf(units));
		Log.d("VOLTAGE", String.valueOf(total/numsamples));
		Log.d("CURRENT", String.valueOf(average));
		return average;
	}
	
	
	
	
	/**
	 * Performs a Current Measurement by doing numberofreadings readings and waiting 0 ms between readings, 
	 * measurement is then checked against limitParam1 & limitParam2
	 * 
	 * This method does NOT sleep between readings
	 * 
	 * @param ioio			- IOIO Instance
	 * @param pinNumber		- IOIO Pin Number
	 * @param gain			- Current sense circuit gain
	 * @param Rshunt		- Current sense shunt resistor value
	 * @param units			- Reading units (mA, uA, nA)
	 * @param isNominal 	- False (Upper/Lower), True (Nominal/Precision)
	 * @param limitParam1 	- Upper / Nominal
	 * @param limitParam2 	- Lower / Precision
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("ucd")
	public static Result checkCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, @Units int units, Boolean isNominal, float limitParam1, float limitParam2) throws Exception{
		float average=getCurrent(ioio, pinNumber, gain, Rshunt, units);
		if (!isNominal) {
			Boolean success=((average > limitParam2) && (average < limitParam1))?true:false;									// Use Param1 as Upper, Param2 as Lower
			return new Result(success, average, units);
		} else {
			Boolean success=((average > limitParam1 - (limitParam1 * limitParam2)) && (average < limitParam1 + (limitParam1 * limitParam2)))?true:false;		// Use Param1 as limit, Param2 as Precision
			return new Result(success, average, units);
		}
		
	};
	
	public static class Result{
		private final float reading;
		private String readingString;
		private final Boolean success;
		private Result(Boolean success, float reading, @Units int units) {
			this.reading=reading;
			df.setRoundingMode(RoundingMode.DOWN);
			this.success = success;
			switch (units) {
			case Units.mA:
				if (reading==0) readingString="0mA";
				else readingString=df.format(reading) + "mA";
				break;
			case Units.uA:
				if (reading==0) readingString="0uA";
				else readingString=df.format(reading) + "uA";
				break;
			case Units.nA:
				if (reading==0) readingString="0nA";
				else readingString=df.format(reading) + "nA";
				break;
			default:
				break;
			}
		}
		@SuppressWarnings("ucd")
		public Boolean isSuccess() {
			return success;
		}
		public String getReading() {
			return readingString;
		}
		public float getReadingValue() {
			return reading;
		}
		
	}
	
	
	/**
	 @SuppressWarnings("ucd")
	 * Interrupts ongoing measurements, if any
	 */
	@SuppressWarnings("ucd")
	public static void interrupt(){
		isinterrupted=true;
		Handler h=new Handler(Looper.getMainLooper());
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				isinterrupted=false;
				currentsleeptime=0;
			}
		}, currentsleeptime*2);
	}
}
