package customclasses;
import java.lang.ref.WeakReference;
import java.util.Random;
import com.pietrantuono.activities.ActivtyWrapper;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.ioioutils.PCBConnectedCallback;
import com.pietrantuono.ioioutils.PCBDetectHelper.PCBDetectHelperInterface;
import com.pietrantuono.ioioutils.Voltage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.util.Log;

import ioio.lib.api.DigitalInput;

public class PCBDetectHelperImpl implements PCBDetectHelperInterface {
	@SuppressWarnings("unused")
	private  DigitalInput _PCB_Detect = null;
	private  ActivtyWrapper callback = null;
	private  PCBDisconnectDetectAsyncTask detectAsyncTask = null;
	private  PCBSleepMonitorAsyncTask sleepMonitorAsyncTask = null;
	private  static final String TAG = "PCBDetectHelper";
	private  PCBConnectedDetectAsyncTask connectedDetectAsyncTask = null;
	private  PCBWaitDisconnectDetectAsyncTask waitDisconnectDetectAsyncTask = null;

	public  PCBDetectHelperImpl() {}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#startCheckingForShutdown(ioio.lib.api.AnalogInput)
	 */
	@Override
	public void startPCBSleepMonitor(PCBConnectedCallback callback) {
		Log.d(TAG, "PCB Sleep Monitor started");
		sleepMonitorAsyncTask = new PCBSleepMonitorAsyncTask(callback);
		sleepMonitorAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void stopPCBSleepMonitor() {

		if (sleepMonitorAsyncTask != null && !sleepMonitorAsyncTask.isCancelled()) {
			Log.d(TAG, "PCB Sleep Monitor stopped");
			sleepMonitorAsyncTask.cancel(true);
		}
		sleepMonitorAsyncTask = null;
	}

	private class PCBSleepMonitorAsyncTask extends
			AsyncTask<Void, Void, Void> {
		private WeakReference<PCBConnectedCallback> callback;

		public PCBSleepMonitorAsyncTask(PCBConnectedCallback callback) {
			this.callback = new WeakReference<PCBConnectedCallback>(callback);
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				int pos = IOIOUtils.getUtils().getUartLog().length();
				while (IOIOUtils.getUtils().getUartLog().substring(pos).indexOf("IWDG") == -1  && !isCancelled()) { // TODO put true
					Thread.sleep(1000);
				}
			} catch (Exception e) {}

			Log.d(TAG,"DEVICE ASLEEP!!!");
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if (callback.get() != null)
				callback.get().onPCBSleep();
			try {callback.clear();callback=null;}
			catch (Exception e){}
		}
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#startCheckingIfConnectionDrops(ioio.lib.api.DigitalInput)
	 */
	@Override
	public void startCheckingIfConnectionDrops(DigitalInput digitalInput) {
		this._PCB_Detect = digitalInput;
		Log.d(TAG, "PCB Fixture Connection Detector started");
		detectAsyncTask = new PCBDisconnectDetectAsyncTask();
		detectAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#setPCBDetectCallback(com.pietrantuono.activities.ActivtyWrapper)
	 */
	@Override
	public void setPCBDetectCallback(ActivtyWrapper callback) {
		this.callback = callback;
	}
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#stopCheckingIfConnectionDrops()
	 */
	@Override
	public void stopCheckingIfConnectionDrops() {
		//_PCB_Detect = null;
		//callback.clear();
		if (detectAsyncTask != null && !detectAsyncTask.isCancelled()) {
			Log.d(TAG, "PCB Fixture Connection Detector stopped");
			detectAsyncTask.cancel(true);
		}
		detectAsyncTask = null;
	}

	@Override
	public void stopWaitingForPCBDisconnected() {
		if (waitDisconnectDetectAsyncTask !=null && !waitDisconnectDetectAsyncTask.isCancelled()) {
			waitDisconnectDetectAsyncTask.cancel(true);
		}
		waitDisconnectDetectAsyncTask = null;
	}

	private class PCBDisconnectDetectAsyncTask extends
			AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				while (!_PCB_Detect.read()  && !isCancelled()) { // TODO put true
					//!getRandomBoolean(0.97f)
					// !_PCB_Detect.read()
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				return false;
			}
			Log.d(TAG,"CONNECTION LOST!!!");
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			synchronized (this) {
				if (!isCancelled()) {
					Voltage.interrupt();
				}
				if (!isCancelled() && callback != null) {
					if(callback.isActivityFinishing())return;
					callback.onPCBConnectionLostRestartSequence();
				}
			}

		}
	}
	private  static Random random = new Random();
	private static  boolean getRandomBoolean(float p) {
		return random.nextFloat() > p;
	}
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#waitForPCBDetect(com.pietrantuono.ioioutils.PCBConnectedCallback, ioio.lib.api.DigitalInput)
	 */
	@Override
	public  void waitForPCBDetect(PCBConnectedCallback callback,
								  DigitalInput digitalInput) {
		connectedDetectAsyncTask = new PCBConnectedDetectAsyncTask(callback,
				digitalInput);
		connectedDetectAsyncTask.execute();
	}
	/* (non-Javadoc)
	 * @see com.pietrantuono.ioioutils.PCBDetectHelper#waitForPCBDisconneted(com.pietrantuono.ioioutils.PCBConnectedCallback, ioio.lib.api.DigitalInput)
	 */
	@Override
	public  void waitForPCBDisconneted(PCBConnectedCallback callback,
									   DigitalInput digitalInput) {
		waitDisconnectDetectAsyncTask = new PCBWaitDisconnectDetectAsyncTask(
				callback, digitalInput);
		waitDisconnectDetectAsyncTask.execute();
	}
	private class PCBConnectedDetectAsyncTask extends
			AsyncTask<Void, Void, Boolean> {
		private WeakReference<PCBConnectedCallback> callback;
		@SuppressWarnings("unused")
		private DigitalInput digitalInput;
		public PCBConnectedDetectAsyncTask(PCBConnectedCallback callback,
										   DigitalInput digitalInput) {
			this.callback = new WeakReference<PCBConnectedCallback>(callback);
			this.digitalInput = digitalInput;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// while ( !isConnectedTest(0.5f) && !isCancelled()) { // TODO
				// put _PCB_Detect.read() using digitalInput
				while (digitalInput.read() && !isCancelled()) { // TODO put


					// using
					// digitalInput
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Log.d(TAG,"CONNECTION DETECTED!!!");
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result && callback.get() != null)
				callback.get().onPCBConnectedStartNewSequence();
			try {callback.clear();
				callback=null;}
			catch(Exception e){}
		}
	}
	private static class PCBWaitDisconnectDetectAsyncTask extends
			AsyncTask<Void, Void, Boolean> {
		private WeakReference<PCBConnectedCallback> callback;
		@SuppressWarnings("unused")
		private DigitalInput digitalInput;
		private PCBWaitDisconnectDetectAsyncTask(PCBConnectedCallback callback,
												 DigitalInput digitalInput) {
			this.callback = new WeakReference<PCBConnectedCallback>(callback);
			this.digitalInput = digitalInput;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				// while ( isConnectedTest(0.5f) && !isCancelled()) { // TODO
				// put !_PCB_Detect.read() using digitalInput
				while (  !digitalInput.read() && !isCancelled()) { // TODO put

					// !digitalInput.read()
					// using
					// digitalInput
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			Log.d(TAG,"DISCONNECT DETECTED!!!");
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result && callback.get() != null)
				callback.get().onPCBDisconnected();
			try {callback.clear();callback=null;}
			catch (Exception e){}
		}
	}
}