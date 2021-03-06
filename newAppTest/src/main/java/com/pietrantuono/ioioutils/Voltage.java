package com.pietrantuono.ioioutils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.os.Handler;
import android.os.Looper;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;


public class Voltage {
	private static final DecimalFormat df = new DecimalFormat("##.##");
	private static Boolean isinterrupted=false;
	private static int currentsleeptime=0;

	public enum Units {

		V	((double)1),
		mV	((double)1E3);

		public final Double value;

		public Double getValue() {
			return value;
		}

		Units(double value) {
			this.value = value;
		}
	}


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
		for (int i = 0; i < numberofreadings; i++) {
			if(isinterrupted)break;
			System.out.printf("Total = %.2f\n", total);
			total = total + analogInput.getVoltage();
			if (sleeptime != 0) {
				Thread.sleep(sleeptime);
			}
		}
		analogInput.close();
		float average = total / numberofreadings;
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

	/**
	 * Performs a Voltage Measurement by doing numberofreadings readings and waiting 0 ms between readings,
	 * measurement is then checked against limitParam1 & limitParam2
	 *
	 * This method does NOT sleep between readings
	 *
	 * @param ioio			- IOIO Instance
	 * @param pinNumber		- IOIO Pin Number
	 * @param scaling		- Scaling factor for input conditioning circuit
	 * @param isNominal 	- False (Upper/Lower), True (Nominal/Precision)
	 * @param limitParam1 	- Upper / Nominal
	 * @param limitParam2 	- Lower / Precision
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("ucd")
	public static Result checkVoltage(IOIO ioio, int pinNumber, float scaling, Boolean isNominal, float limitParam1, float limitParam2) throws Exception{
		float average=getVoltage(ioio, pinNumber);
		average = average * scaling;
		Boolean success;
		if (isNominal) {
			if (limitParam1 != 0) {
				if (limitParam1 < 0) {
					success = ((average > (limitParam1 + (limitParam1 * limitParam2))) && (average < (limitParam1 - (limitParam1 * limitParam2)))) ? true : false;
				} else {
					success = ((average > (limitParam1 - (limitParam1 * limitParam2))) && (average < (limitParam1 + (limitParam1 * limitParam2)))) ? true : false;
				}
			} else {
				success = (average == 0) ? true : false;
			}
		} else {
			success = ((average > limitParam2) && (average < limitParam1)) ? true : false;
		}
		return new Result(success, average);
		
	}

	public static class Result{
		private final float reading;
		private final String readingString;
		
		private Result(Boolean success, float reading) {
			this.reading=reading;
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
		public float getReadingValue() {
			return reading;
		}
		private final Boolean success;
		
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
