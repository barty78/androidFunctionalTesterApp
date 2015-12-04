	package com.pietrantuono.ioioutils;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.activities.NewIOIOActivityListener;

public class IOIOUtils implements IOIOUtilsInterface  {
	private  Uart uart1;
	private  Uart uart2;
	
	private static InputStream RX1;
	private static BufferedReader RD1;
	private static DigitalInput barcodeOK;
	private static DigitalOutput barcodeTRGR;
	private static final String TAG = "IOIOUtils";
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	private static IOIO myioio;
	private static int counter = 0;
	private static DigitalOutput boot0;
	private static DigitalOutput boot1;
	private static DigitalOutput POWER;
	private static DigitalOutput trigger;
	private static DigitalOutput reset;
	private static DigitalOutput Irange;
	private static DigitalOutput _5V_DC;
	private static DigitalOutput Sensor_Low;
	private static DigitalOutput Sensor_High;
	private static DigitalOutput HallInt;
	private static DigitalOutput EMag;

	private static DigitalInput CHGPin;

	private static TwiMaster master = null;
	private static Boolean isinterrupted=false;
	private static Boolean stopthread=false;
	private static Boolean triggervalue=true;
	
	private static InputStream iS;
	private static OutputStream oS;
	private static InputStream inputStream;
	private static OutputStream outputStream;
	private static BufferedReader r;
	private static StringBuilder sb;
	private static Uart2Thread thread;
	private static IOIOUtilsInterface instance;
	
	public static IOIOUtilsInterface getUtils(){
		if(instance==null){
			instance= new IOIOUtils();
		}
		return instance;
	}
	
	public static void setIOIOUtilsInterface(IOIOUtilsInterface mockobject){
		instance=mockobject;
	} 
		
	private  void makeToast(final Activity ac, final String s) {
		ac.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(ac, s, Toast.LENGTH_LONG).show();

			}
		});
	}

	private  void report(Exception e, Activity ac) {
		try {
		e.printStackTrace();
		Log.e(TAG, e.toString());
		Crashlytics.logException(e);
		makeToast(ac, e.toString());
		} catch(Exception e1){}
	}


	@Override
	public  String readBarcode(Activity activity) {
		if(isinterrupted)return "";
		String tmp = "";

		try {
			barcodeTRGR.write(false);

		} catch (Exception e1) {
			e1.printStackTrace();
			Log.e(TAG, e1.toString());
			Crashlytics.logException(e1);
		}

		Log.d("BARCODE: ", "Read Triggered...");

		try {
			barcodeTRGR.write(true);
		} catch (Exception e1) {
			e1.printStackTrace();
			Log.e(TAG, e1.toString());
			Crashlytics.logException(e1);
		}

		if (readPulseWithTimeout() == 1) {

			Log.d("BARCODE: ", "Valid Read");

			try {
				tmp = RD1.readLine();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, e.toString());
				Crashlytics.logException(e);

			}

			Log.d("Barcode: ", tmp);
			// updateUiText("Barcode - " + tmp + "\n");
			// gotBarcode = true;

			return tmp;
		}
		return tmp;
	}

	private  int readPulseWithTimeout() {
		if(isinterrupted)return -1;
		// Read barcodeOK pulse with timeout

		int readByte = -1;

		Callable<Integer> readPulse = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				barcodeOK.waitForValue(false);
				return 1;
			}
		};
		Future<Integer> future = executor.submit(readPulse);
		try {
			readByte = future.get(1000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
			Crashlytics.logException(e);
			Log.e(TAG, e.toString());
			Log.d("READ: ", "Timed Out...");
			return -1;

		}

		return readByte;

	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#closeall(com.pietrantuono.activities.NewIOIOActivityListener, android.app.Activity)
	 */
	@Override
	public  void closeall(final NewIOIOActivityListener listener,
			final Activity ac) {
			
		stopUartThread();
		
		try {
			POWER.close();
			}
			catch (Exception e){e.printStackTrace();}
		try {
			reset.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			boot0.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			boot1.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			_5V_DC.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			Irange.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			Sensor_Low.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			Sensor_High.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			master.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			uart2.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			HallInt.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			EMag.close();
			}
			catch (Exception e){e.printStackTrace();}

		try {
			CHGPin.close();
		}
		catch (Exception e){e.printStackTrace();}

		try {
			trigger.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			uart1.close();
			}
			catch (Exception e){e.printStackTrace();}
		
		try {
			uart2.close();
			}
			catch (Exception e){e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#initialize(com.pietrantuono.activities.NewIOIOActivityListener, ioio.lib.api.IOIO, android.app.Activity)
	 */
	@Override
	public  void initialize(final NewIOIOActivityListener listner,
			final IOIO ioio_, final Activity ac) {
		isinterrupted=false;
		try {
			POWER = ioio_.openDigitalOutput(19,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, false);
		} catch (Exception e) {
			report(e, ac);
			return;
		}

		try {
			reset = ioio_.openDigitalOutput(20, DigitalOutput.Spec.Mode.OPEN_DRAIN,
					true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		try {
			boot0 = ioio_.openDigitalOutput(24, DigitalOutput.Spec.Mode.OPEN_DRAIN,
					true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		try {
			boot1 = ioio_.openDigitalOutput(23, DigitalOutput.Spec.Mode.OPEN_DRAIN,
					false);
		} catch (Exception e) {
			report(e, ac);
			return;
		}

		try {
			_5V_DC = ioio_.openDigitalOutput(18,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		
		//Irange (uA Range) = true, (mA Range) = false.
		try {
			Irange = ioio_.openDigitalOutput(9,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);

		} catch (Exception e) {
			report(e, ac);
			return;
		}
		try {
			Sensor_Low = ioio_.openDigitalOutput(2,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		try {
			Sensor_High = ioio_.openDigitalOutput(1,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		try {
			HallInt = ioio_.openDigitalOutput(21,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		
		try {
			EMag = ioio_.openDigitalOutput(3,
					DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		
		try {
			master = ioio_.openTwiMaster(2, TwiMaster.Rate.RATE_100KHz, false);
		} catch (ConnectionLostException e1) {
			report(e1, ac);
			return;
		}
		
		try {
			trigger = ioio_.openDigitalOutput(45,
					DigitalOutput.Spec.Mode.NORMAL, true);
		} catch (Exception e) {
			report(e, ac);
			return;
		}

		try {
			CHGPin = ioio_.openDigitalInput(27, DigitalInput.Spec.Mode.FLOATING);
		} catch (Exception e) {
			report(e, ac);
			return;
		}
		
		try {
			uart1 = ioio_.openUart(6, 7, 115200, Uart.Parity.NONE, Uart.StopBits.ONE);
		} catch (ConnectionLostException e) {
			Log.e(TAG, e.toString());
		}
		
		iS = uart1.getInputStream();
		//oS = uart1.getOutputStream();
		
		InputStreamReader in1 = new InputStreamReader(iS);

		RD1 = new BufferedReader(in1);
		
		try {
			barcodeOK = ioio_.openDigitalInput(15,
					DigitalInput.Spec.Mode.PULL_UP);
		} catch (Exception e) {

		}

		try {
			barcodeTRGR = ioio_.openDigitalOutput(17,
					DigitalOutput.Spec.Mode.NORMAL,

					true);
		} catch (Exception e) {
			
		}
		
		try {
			uart2 = ioio_.openUart(13, 14, 115200, Uart.Parity.EVEN, // STM32
																	// Bootloader
																	// requires
																	// EVEN
																	// Parity..
					Uart.StopBits.ONE);
		} catch (ConnectionLostException e) {
			Log.e(TAG, e.toString());
		}

		inputStream = uart2.getInputStream();
		outputStream = uart2.getOutputStream();
		
		sb = new StringBuilder();

		toggle5VDC(ac);

	}
	
	public DigitalOutput getBarcodeTrgr() {
		return barcodeTRGR;
	}
	
	public DigitalInput getBarcodeOK() {
		return barcodeOK;
	}

	/* (non-Javadoc)
    * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getCHGPin()
    */
	@Override
	public DigitalInput getCHGPin() {return CHGPin; }

	public  DigitalOutput getTrigger() {return trigger;	}

	public  DigitalOutput getSensor_Low() {
		return Sensor_Low;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getSensor_High()
	 */
	@Override
	public  DigitalOutput getSensor_High() {
		return Sensor_High;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#get_5V_DC()
	 */
	@Override
	public  DigitalOutput get_5V_DC() {
		return _5V_DC;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getMaster()
	 */
	@Override
	public  TwiMaster getMaster() {
		return master;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getPOWER()
	 */
	@Override
	public  DigitalOutput getPOWER() {
		return POWER;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getReset()
	 */
	@Override
	public  DigitalOutput getReset() {
		return reset;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getHallInt()
	 */
	@Override
	public  DigitalOutput getHallInt() {
		return HallInt;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getEMag()
	 */
	@Override
	public  DigitalOutput getEmag() {
		return EMag;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#resetDevice(android.app.Activity)
	 */
	@Override
	public  void resetDevice(Activity activity) {
		if(isinterrupted)return;
		Log.d(TAG, "Resetting UUT");

		try {
			reset.write(false);
		} catch (Exception e) {
			report(e, activity);
			return;
		}
		try {
			Thread.sleep(1000);
		} catch (Exception e1) {
			report(e1, activity);
			return;
		}
		try {
			reset.write(true);
		} catch (Exception e1) {
			report(e1, activity);
			return;
		}

	}
	
	@SuppressWarnings("ucd")
	public  void toggleHall(Activity activity) {
		if(isinterrupted)return;
		try {
			HallInt.write(false);
		} catch (Exception e) {
			report(e, activity);
			return;
		}
		try {
			Thread.sleep(2000);
		} catch (Exception e1) {
			report(e1, activity);
			return;
		}
		try {
			HallInt.write(true);
		} catch (Exception e1) {
			report(e1, activity);
			return;
		}

	}
	
	public  void toggle5VDC(Activity activity) {
		if(isinterrupted)return;
		Log.d(TAG, "Toggling 5V DC Charging Power");
		try {
			get_5V_DC().write(false);
		} catch (ConnectionLostException e) {
			report(e, activity);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			report(e, activity);
		}
		try {
			get_5V_DC().write(true);
		} catch (ConnectionLostException e) {
			report(e, activity);
		}
	}
	@Override
	public  void toggleEMag(Activity activity) {
		if(isinterrupted)return;
		Log.d(TAG, "Toggling Electromagnet");
		try {
			getEmag().write(false);
		} catch (ConnectionLostException e) {
			report(e, activity);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			report(e, activity);
		}
		try {
			getEmag().write(true);
		} catch (ConnectionLostException e) {
			report(e, activity);
		}
	}
	

	@SuppressWarnings("ucd")
	public  void toggleTrigger(Activity activity) {
		if(isinterrupted)return;

		if (triggervalue) {
			triggervalue = false;
		} else {
			triggervalue = true;
		}
		
		try {
			trigger.write(triggervalue);
		} catch (Exception e) {
			report(e, activity);
			return;
		}
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#modeApplication(android.app.Activity)
	 */
	@Override
	public  void modeApplication(Activity activity) {
		thread= new Uart2Thread();
		Log.d(TAG, "Starting UART Thread");
		thread.start();
		if(isinterrupted)return;
		Log.d(TAG, "Switching to Application Boot Mode");
		try {
			boot0.write(false);
		} catch (Exception e) {
			report(e, activity);
			return;
		}
		try {
			boot1.write(true);
		} catch (Exception e) {
			report(e, activity);
			return;
		}
				
		toggle5VDC(activity);
		
		// When entering Application mode, start the Uart Thread so that console is captured.  We will search console log for signs of life, etc...
		thread= new Uart2Thread();
		Log.d(TAG, "Starting UART Thread");
		thread.start();

	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getIrange()
	 */
	@Override
	public  DigitalOutput getIrange() {
		return Irange;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#setIrange()
	 */
	@Override
	@SuppressWarnings("ucd")
	public void setIrange(Activity activity, Boolean value) {
		try {
			Irange.write(value);
		} catch (Exception e) {
			report(e, activity);
		}
		
		toggle5VDC(activity);		// When switching current reading range, need to toggle charger on/off to re-power UUT

	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getBoot0()
	 */
	public  DigitalOutput getBoot0() {
		return boot0;
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getBoot1()
	 */
	public  DigitalOutput getBoot1() {
		return boot1;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getIOIOUart()
	 */
	@Override
	public  Uart getIOIOUart() {
		return uart2;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartLog()
	 */
	@Override
	public  StringBuilder getUartLog() {
		return sb;
	}
	
	@Override
	public  void clearUartLog() {
		Log.d(TAG, "Clearing Uart Log StringBuilder instance");
		sb = new StringBuilder();
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#stop()
	 */
	@Override
	public  void stop() {
		stopthread = true;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartInStream()
	 */
	@Override
	public  InputStream getUartInStream() {
		return inputStream;
	}
	
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartOutStream()
	 */
	@Override
	public  OutputStream getUartOutStream() {
		return outputStream;
	}
	
		
	 private class Uart2Thread extends Thread {

		@SuppressWarnings("unused")
		private String line;
		private long counter = 0;
		 
		public Uart2Thread() {
			stopthread=false;
		}

		@Override
		public void run() {
						
			InputStream is = getIOIOUart().getInputStream();
			r = new BufferedReader(new InputStreamReader(is));

			
			while (!stopthread) {
				if(counter % 25 == 0){
					Log.d(TAG, "UART Task is running: ");
				}
				line = null;
				MyCallable myCallable= new MyCallable();
				Future<String> future = executor.submit(myCallable);
				try {
					line = future.get(200, TimeUnit.MILLISECONDS);//TIMEOT 
					//future.cancel(true);
				} catch (Exception e) {
					//Log.e(TAG, e.toString());
					counter++;

				}
				//Log.d(TAG, sb.toString());

			}
		}
		

		private class MyCallable implements Callable {

			@Override
			public String call() throws Exception {
				String tmp = r.readLine();
				sb.append(tmp);
				Log.d(TAG + " - CALL", tmp);
				return tmp;
			}

		}
		
	}
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#stopUartThread()
	 */
	@Override
	public  void stopUartThread(){
		stopthread=true;
	}
	
}
