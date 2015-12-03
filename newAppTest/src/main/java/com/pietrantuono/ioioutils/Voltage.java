package com.pietrantuono.ioioutils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;


public class Voltage {
	private static DecimalFormat df = new DecimalFormat("##.##");
	private static Boolean isinterrupted=false;;
	private static int currentsleeptime=0;
	/**
	 * Gets voltages doing 10 readings and waiting 0 ms between readings
	 * 
	 * This method does NOT sleep between readings
	 * 
	 * @param ioio
	 * @param pinNumber
	 * @throws Exception
	 */
	private static float getVoltage(IOIO ioio, int pinNumber) throws Exception {
		return getVoltage(ioio, pinNumber, 10, 0);
	}
		
	
	/**
	 * Gets voltages doing numberofreadings readings and waiting sleeptime ms between readings
	 * 
	 * This method does DOES SLEEP between readings
	 * 
	 * @param ioio
	 * @param pinNumber
	 * @param sleeptime
	 * @param numberofreadings
	 * @return average reading
	 * @throws Exception
	 */
	public static float getVoltage(IOIO ioio, int pinNumber,  int numberofreadings, int sleeptime) throws Exception {
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
			System.out.printf("Total = %.2f\n", total);
			total = total + analogInput.getVoltage();
			Thread.sleep(sleeptime);
		}
		analogInput.close();
		float average = total / numsamples;
		System.out.printf("Average = %.2f\n", average);
		return average;
	}
	
	/**
	 * Gets voltages doing numberofreadings readings and waiting 0 ms between readings
	 * 
	 * This method does NOT sleep between readings
	 * 
	 * @param ioio
	 * @param pinNumber
	 * @param numberofreadings
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("ucd")
	public static float getVoltage(IOIO ioio, int pinNumber,  int numberofreadings) throws Exception {
		return getVoltage(ioio, pinNumber, numberofreadings, 0);
	}
	
	@SuppressWarnings("ucd")
	public static Result checkVoltage(IOIO ioio, int pinNumber, Boolean scaled, float scaling, float limit, float precision) throws Exception{
		float average=getVoltage(ioio, pinNumber);
		if (scaled) average = average*scaling;
		Boolean success=((average > limit - precision)&& (average < limit + precision))?true:false;
		return new Result(success, average);
		
	};
	
	public static class Result{
		
		private String readingString;
		
		private Result(Boolean success, float reading) {
			df.setRoundingMode(RoundingMode.DOWN);
			this.success = success;
			if (reading==0) readingString="0V";
			else readingString=df.format(reading) + "V";
		}
		@SuppressWarnings("ucd")
		public Boolean isSuccess() {
			return success;
		}
		public String getReading() {
			return readingString;
		}
		private Boolean success;
		
	}
	
	
	/**
	 * Interrupts ongoing measurements, if any
	 */
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
