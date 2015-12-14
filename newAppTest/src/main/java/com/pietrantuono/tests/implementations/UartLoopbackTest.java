package com.pietrantuono.tests.implementations;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import android.app.Activity;
import android.util.Log;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

public class UartLoopbackTest extends Test {
	private Uart uart;
	private InputStream RX;
	private OutputStream TX;
	private static Boolean stopthread=false;

	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private final String TAG = getClass().getSimpleName();
	private int bytesVerified_;
	
	private static final boolean uartdefinclass = false;

	
	public UartLoopbackTest(Activity activity, IOIO ioio) {
		super(activity, ioio, "Simple UART Loopback Test", false, false, 0, 0, 0);
	}

	@Override
	public void execute() {
		UartThread thread = new UartThread();
		thread.start();

	}
	
	public static void stop() {
		stopthread = true;
	}

	public class UartThread extends Thread {
		
		final int BYTE_COUNT = 100;
		final int SEED = 17;

		
		Random rand = new Random(SEED);
		
		public UartThread() {
			stopthread = false;
		}
		
		@Override
		public void run() {
				
		if (uartdefinclass) {
			try {
				uart = ioio.openUart(13, 14, 115200, Uart.Parity.EVEN, // STM32
																			// Bootloader
																			// requires
																			// EVEN
																			// Parity..
						Uart.StopBits.ONE);
			} catch (ConnectionLostException e) {
				Log.e(TAG, e.toString());
			}
		
			RX = uart.getInputStream();
			TX = uart.getOutputStream();
			
		} else {
			TX = IOIOUtils.getUtils().getUartOutStream();
			RX = IOIOUtils.getUtils().getUartInStream();
		}
			
			
			
						
			while (!stopthread) {
				//Log.d(TAG, "Task is running");
				bytesVerified_ = 0;

				try {
					for (int i = 0; i < BYTE_COUNT; i++) {
						byte value = 100;
						Log.d(TAG, "Writing: " + String.valueOf(value));
						TX.write(value);
					
						try {
							Thread.sleep(1 * 1);
						} catch (InterruptedException e) {
						}
						
						int read = RX.read() & 0xFF;
						Log.d(TAG, "Read: " + String.valueOf(read));
						if (read != value) {
							Log.d(TAG, "Expected: " + (value) + ", Got: " + read);
						} else {
							bytesVerified_++;
						}
					}
				} catch (Exception e) {
					
				}
								
				publishResult();
				
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
				}
				
			}
		}

		private void publishResult() {
			((Activity) activityListener).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (bytesVerified_ != BYTE_COUNT)
						//activityListener.addFailOrPass(true, false, String.valueOf(bytesVerified_));
						Log.d(TAG, String.valueOf(bytesVerified_));
					else {
						//activityListener.addFailOrPass(true, true, String.valueOf(bytesVerified_));
						Log.d(TAG, String.valueOf(bytesVerified_));
					}
				}
			});
		}
	}
	
	@Override
	public void interrupt() {
		super.interrupt();
		
	}

}

