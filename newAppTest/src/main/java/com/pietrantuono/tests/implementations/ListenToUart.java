package com.pietrantuono.tests.implementations;

import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.superclass.Test;

import android.app.Activity;
import android.util.Log;
import ioio.lib.api.IOIO;

public class ListenToUart extends Test {
	private final String TAG = getClass().getSimpleName();

	public ListenToUart(Activity activity, IOIO ioio) {
		super(activity, ioio, "Listen to uart", false, false);
	}

	@Override
	public void execute() {
		UartThread thread = new UartThread();
		thread.start();

	}

	public class UartThread extends Thread {
		private String line;
		
		@Override
		public void run() {
						
			
			IOIOUtils.getUtils().modeApplication((Activity)activityListener);			// Switch boot mode to Application			

			IOIOUtils.getUtils().resetDevice((Activity)activityListener);				// Reset the device
			
			
			while (!isinterrupted) {
				//Log.d(TAG, "Task is running");
				line = null;
				
				//line = IOIOUtils.getUtils().getUartLog().toString();
				//if (IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") != -1)
				if (IOIOUtils.getUtils().getUartLog() != null)
				{
					line = IOIOUtils.getUtils().getUartLog().toString();
					//line = IOIOUtils.getUtils().getUartLog().substring(IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 8, IOIOUtils.getUtils().getUartLog().indexOf("itoa16: ") + 32).toString();
				}
				//Log.d(TAG, String.valueOf(total.length()));
				//Log.d(TAG, line);
					
				publishResult();
				//IOIOUtils.getUtils().flushUartLog();
				IOIOUtils.getUtils().resetDevice((Activity)activityListener);
				
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
					if (line == null || line.matches("")) Log.d(TAG, "Nothing to read");
						//activityListener.addFailOrPass(false, false, "Nothing to read");
					else {
						//activityListener.addFailOrPass(false, true, line);
						Log.d(TAG, line);
					}

				}
			});

		}


	}
	
	

}
