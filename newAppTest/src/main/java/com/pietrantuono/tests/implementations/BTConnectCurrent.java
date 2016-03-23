package com.pietrantuono.tests.implementations;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import ioio.lib.api.IOIO;
import android.app.Activity;
import android.os.AsyncTask;

import com.pietrantuono.ioioutils.Voltage;
import com.pietrantuono.tests.superclass.Test;
public class BTConnectCurrent extends Test {
	public BTConnectCurrent(Activity activity, IOIO ioio) {
		super(activity, ioio, "Current Measurement - Bluetooth Connected", false, false, 0, 0, 0);
	}
	@Override
	public void execute() {
		new BTConnectCurrentAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	@Override
	public void interrupt() {
		super.interrupt();
		try{Voltage.interrupt();}catch(Exception e){}
	}

	private class BTConnectCurrentAsyncTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			if(isinterrupted)return null;
			DecimalFormat df = new DecimalFormat("##.##");
			df.setRoundingMode(RoundingMode.DOWN);
			float average = 0;
			try {
				average = ((Voltage.getVoltage(ioio, 42, 20,100) / (50 * 2)) * (float) 1e3);
			} catch (Exception e) {
				report(e);
				activityListener.addFailOrPass(true, false, e.toString());
				return null;
			}
			setValue(average);
			float precisionfactor = 0.1f;
			if (30 * (1 - precisionfactor) < average
					&& average < 30 * (1 + precisionfactor)) {
				setSuccess(true);
				activityListener.addFailOrPass(true, true, df.format(average) + "mA",description);
			} else {
				setSuccess(false);
				activityListener.addFailOrPass(true, false, df.format(average) + "mA",description);
			}

			return null;
		}
	}
}
