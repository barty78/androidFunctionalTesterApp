package com.pietrantuono.ioioutils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;


public class Current {
	private static DecimalFormat df = new DecimalFormat("##.##");
	private static Boolean isinterrupted=false;;
	private static int currentsleeptime=0;
	
	public enum Scale {
		
		mA	((double)1E3),
		uA	((double)1E6),
		nA	((double)1E9);
		
		public Double value;
		
		public Double getValue() {
			return value;
		}
		
		Scale(double value) {
			this.value = value;
		}
	}
	
	
	
	/**
	 * Gets Current by doing 10 readings and waiting 0 ms between readings
	 * 
	 * This method does NOT sleep between readings
	 * 
	 * @param ioio
	 * @param pinNumber
	 * @param gain
	 * @param Rshunt
	 * @param scale
	 * @throws Exception
	 */
	private static float getCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, Scale scale) throws Exception {
		return getCurrent(ioio, pinNumber, gain, Rshunt, scale, 10, 0);
	}
		
	
	/**
	 * Gets current doing numberofreadings readings and waiting sleeptime ms between readings
	 * 
	 * This method does DOES SLEEP between readings
	 * 
	 * @param ioio
	 * @param pinNumber
	 * @param gain
	 * @param Rshunt
	 * @param scale
	 * @param sleeptime
	 * @param numberofreadings
	 * @return average reading
	 * @throws Exception
	 */
	private static float getCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, Scale scale, int numberofreadings, int sleeptime) throws Exception {
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
		float average = (float) (((total / numsamples) / (gain * Rshunt)) * scale.getValue());
		Log.d("GAIN", String.valueOf(gain));
		Log.d("SHUNT", String.valueOf(Rshunt));
		Log.d("SCALE", String.valueOf(scale.getValue()));
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
	 * @param ioio
	 * @param pinNumber		- IOIO Pin Number
	 * @param gain
	 * @param Rshunt
	 * @param scale			- mA, uA, nA
	 * @param isUpperLower - True (Upper/Lower), False (Nominal/Precision)
	 * @param limitParam1 - Upper / Nominal
	 * @param limitParam2 - Lower / Precision
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("ucd")
	public static Result checkCurrent(IOIO ioio, int pinNumber, int gain, int Rshunt, Scale scale, Boolean isUpperLower, float limitParam1, float limitParam2) throws Exception{
		float average=getCurrent(ioio, pinNumber, gain, Rshunt, scale);
		if (isUpperLower) {
			Boolean success=((average > limitParam2) && (average < limitParam1))?true:false;									// Use Param1 as Upper, Param2 as Lower
			return new Result(success, average, scale);
		} else {
			Boolean success=((average > limitParam1 - limitParam2) && (average < limitParam1 + limitParam2))?true:false;		// Use Param1 as limit, Param2 as Precision
			return new Result(success, average, scale);
		}
		
	};
	
	public static class Result{
		
		private String readingString;
		private Boolean success;
		private Result(Boolean success, float reading, Scale scale) {
			df.setRoundingMode(RoundingMode.DOWN);
			this.success = success;
			switch (scale) {
			case mA:
				if (reading==0) readingString="0mA";
				else readingString=df.format(reading) + "mA";
				break;
			case uA:
				if (reading==0) readingString="0uA";
				else readingString=df.format(reading) + "uA";
				break;
			case nA:
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
