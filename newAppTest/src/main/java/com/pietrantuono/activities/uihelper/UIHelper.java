package com.pietrantuono.activities.uihelper;

import java.util.ArrayList;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.fragments.PagerAdapter;
import com.pietrantuono.activities.fragments.SequenceFragment;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.constants.NewMResult;
import com.pietrantuono.constants.NewMSensorResult;
import com.pietrantuono.constants.NewSequenceInterface;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.uploadfirmware.ProgressAndTextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UIHelper {
	
	private Activity activity;
	private View v = null;
	private NewSequenceInterface sequence;
	private static final String TAG = "UIHelper";
	private static SequenceFragment sequenceFragment;

	public UIHelper(Activity activity, NewSequenceInterface sequence) {
		this.activity = activity;
		this.sequence = sequence;
		setUpRetryButton();
		setUpExitButton();
		setOverallFailOrPass(false);
		setupViewpager(activity);
		if(sequenceFragment!=null)
			sequenceFragment.setSequence(sequence);
	}

	private void setupViewpager(Activity activity) {
		ViewPager viewPager= (ViewPager) activity.findViewById(R.id.pager);
		AppCompatActivity appcompat = (AppCompatActivity) activity;
		viewPager.setAdapter(new PagerAdapter(appcompat.getSupportFragmentManager()));
		viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
		viewPager.setCurrentItem(1);
	}

	public void setupChronometer(Activity activity){
		Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
		cronometer.setOnChronometerTickListener(new UIHelper.MyOnChronometerTickListener());
		
	}
	public void startChronometer(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
				cronometer.setBase(SystemClock.elapsedRealtime());
				cronometer.start();
			}
		});
		Log.d(TAG, "Chronometer started");
	}
	
	public void stopChronometer(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
				cronometer.stop();
			}
		});
		Log.d(TAG, "Chronometer stopped");
	}

	public void setResult(boolean success) {
		ActivityUIHelperCallback callback = (ActivityUIHelperCallback) activity;
		try {
				callback.getResults().get(callback.getIterationNumber()).get(sequence.getCurrentTestNumber())
						.setTestsuccessful(success);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void registerSequenceFragment(SequenceFragment sequenceFragment) {
		this.sequenceFragment=sequenceFragment;
	}

	public void unregisterSequenceFragment() {
		this.sequenceFragment=null;
	}


	public interface ActivityUIHelperCallback {
		ArrayList<ArrayList<NewMResult>> getResults();

		int getIterationNumber();

		void closeActivity();

		void goAndExecuteNextTest();

		void manuallyRedoCurrentTest();

		void restartSequence();

		void clearSerialConsole();
	}

	private static class MyOnChronometerTickListener implements OnChronometerTickListener {
		@Override
		public void onChronometerTick(Chronometer cArg) {
			long time = SystemClock.elapsedRealtime() - cArg.getBase();
			int h = (int) (time / 3600000);
			int m = (int) (time - h * 3600000) / 60000;
			int s = (int) (time - h * 3600000 - m * 60000) / 1000;
			String mm = m < 10 ? "0" + m : m + "";
			String ss = s < 10 ? "0" + s : s + "";
			cArg.setText(mm + ":" + ss);
		}
	}

	public void setJobId(final String jobnumber, final Boolean success) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView job_number = (TextView) activity.findViewById(R.id.job_number);
				if (PeriCoachTestApplication.getIsRetestAllowed()) {
					job_number.setText(jobnumber + " (Retests)");
				} else {
					job_number.setText(jobnumber + " (No Retests)");
				}
				if (success)
					job_number.setTextColor(Color.GREEN);
			}
		});
	}

	public void setConnected(final boolean conn) {
		final TextView connected = (TextView) activity.findViewById(R.id.connected);
		final ImageView connectedicon = (ImageView) activity.findViewById(R.id.image);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (conn) {
					connected.setText("FIXTURE CONNECTED");
					connected.setTextColor(Color.GREEN);
					connectedicon.setImageResource(R.drawable.ic_connect);
				} else {
					connected.setText("CONNECTING TO FIXTURE");
					connected.setTextColor(Color.RED);
					connectedicon.setImageResource(R.drawable.ic_disconnect);
				}
			}
		});
	}

	public synchronized void addView(final String label, final String text, boolean goAndExecuteNextTest) {
		addView(label, text, 0, goAndExecuteNextTest);
	}

	public synchronized void addView(final String label, final String text, final int color, final boolean goAndExecuteNextTest) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.barcode_adn_serial);
				LayoutInflater inflater = activity.getLayoutInflater();
				View view = inflater.inflate(R.layout.add_view, null);
				TextView labeltv = (TextView) view.findViewById(R.id.label);
				TextView texttv = (TextView) view.findViewById(R.id.text);
				if (label != null)
					labeltv.setText(label);
				if (text != null)
					texttv.setText(text);
				if (color == 0)
					texttv.setTextColor(Color.GREEN);
				else
					texttv.setTextColor(color);
				final ViewTreeObserver observer = layout.getViewTreeObserver();
				observer.addOnPreDrawListener(new OnPreDrawListener() {				
					@Override
					public boolean onPreDraw() {
						observer.removeOnPreDrawListener(this);
						if(goAndExecuteNextTest)((ActivityCallback) activity).goAndExecuteNextTest();
						return true;
					}
				});
				layout.addView(view);
			}
		});
	}

	public synchronized void setStatusMSG(final String message, final Boolean success) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView tv = (TextView) activity.findViewById(R.id.teststatusmsg);
				tv.setText(message);
				if (success == null) {
					tv.setTextColor(activity.getResources().getColor(R.color.list_highlight_color));
				} else {
					if (success) {
						tv.setTextColor(Color.GREEN);
					} else {
						tv.setTextColor(Color.RED);
					}
				}
			}
		});

	}

	public synchronized ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading,
			String otherreading, String description) {
		if(sequenceFragment!=null){
			return  sequenceFragment.addFailOrPass(istest,success,reading,otherreading,description);
		}
		else return null;
	}

	public synchronized ProgressAndTextView addFailOrPass(final Boolean istest, final Boolean success, String reading,
			String otherreading) {
		return addFailOrPass(istest, success, reading, otherreading, null);

	}

	public void setOverallFailOrPass(final Boolean show) {
		final ActivityUIHelperCallback callback = (ActivityUIHelperCallback) activity;
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				RelativeLayout resultlayout = (RelativeLayout) activity.findViewById(R.id.progresscontainer);
				TextView resulttxt = (TextView) activity.findViewById(R.id.testResultIndText);
				ProgressBar progress = (ProgressBar) activity.findViewById(R.id.testResultInd);

				Resources res = activity.getResources();
				Drawable background = null;

				if (show) {
					Boolean temp = true;
					if (callback.getIterationNumber() >= 0)
						for (int i = 0; i <= sequence.getNumberOfSteps() - 1; i++)
							if (callback.getResults().get(callback.getIterationNumber()).get(i) == null
									|| callback.getResults().get(callback.getIterationNumber()).get(i)
											.isTestsuccessful() == null
									|| (callback.getResults().get(callback.getIterationNumber()).get(i).isTest()
											&& !callback.getResults().get(callback.getIterationNumber()).get(i)
													.isTestsuccessful())){
								Log.d("RESULT", "TEST # " + i + " FAILED");
								temp = false;}
					final Boolean success = temp;
					resultlayout.setVisibility(View.VISIBLE);
					if (success) {
						resulttxt.setText("PASS");
						background = res.getDrawable(R.drawable.greenprogress);
						progress.setProgressDrawable(background);

					} else {
						resulttxt.setText("FAIL");
						background = res.getDrawable(R.drawable.redprogress);
						progress.setProgressDrawable(background);
					}
				} else {
					resultlayout.setVisibility(View.GONE);
				}

			}
		});
	}

	public void setCurrentAndNextTaskinUI() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView currenttask = (TextView) activity.findViewById(R.id.currenttask);
				TextView nexttask = (TextView) activity.findViewById(R.id.nexttask);
				String currentstepdesc = null;
				String currentstenumber = null;
				try {
					currentstepdesc = sequence.getCurrentTestDescription();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					currentstenumber = "" + (sequence.getCurrentTestNumber() + 1);

				} catch (Exception e) {
					e.printStackTrace();

				}
				if (currentstepdesc == null)
					currenttask.setText("");
				else {
					if (currentstenumber == null)
						currenttask.setText(currentstepdesc);
					else
						currenttask.setText(currentstenumber + " " + currentstepdesc);
				}
				String nexttaskdescription = null;
				String nexttasknumber = null;
				try {
					nexttaskdescription = sequence.getNextTestDescription();

				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					nexttasknumber = "" + (sequence.getCurrentTestNumber() + 2);

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (nexttaskdescription == null)
					nexttask.setText("");
				else {
					if (nexttasknumber == null)
						nexttask.setText(nexttaskdescription);
					else
						nexttask.setText(nexttasknumber + " " + nexttaskdescription);
				}
			}
		});
	}

	private void setUpRetryButton() {
		if (activity == null || activity.isFinishing())
			return;
		final ActivityUIHelperCallback activityUIHelperCallback = (ActivityUIHelperCallback) activity;
		Button retry = (Button) activity.findViewById(R.id.retry);
		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Restart Sequence").setMessage("Are sure you want to restart the sequence?");
				builder.setPositiveButton("Yes, let's try again", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						activityUIHelperCallback.manuallyRedoCurrentTest();
						activityUIHelperCallback.restartSequence();
					}
				});
				builder.setNegativeButton("NO", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
			}
		});
	}

	private void setUpExitButton() {
		if (activity == null || activity.isFinishing())
			return;
		final ActivityUIHelperCallback activityUIHelperCallback = (ActivityUIHelperCallback) activity;
		Button exit = (Button) activity.findViewById(R.id.exit);
		exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Exit").setMessage("Are sure you want to exit?");
				builder.setPositiveButton("Yes, let's try exit", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						activityUIHelperCallback.closeActivity();
					}
				});
				builder.setNegativeButton("NO", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.create().show();
			}
		});
	}

	public void addSensorTestCompletedRow(NewMSensorResult mSensorResult) {
		sequenceFragment.addSensorTestCompletedRow(mSensorResult);
	}

	public void setSequence(NewSequenceInterface sequence) {
		this.sequence = sequence;
		if(sequenceFragment!=null)sequenceFragment.setSequence(sequence);
	}

	public synchronized ProgressAndTextView createUploadProgress(final Boolean istest, final Boolean success,String description) {
		if(sequenceFragment!=null) return sequenceFragment.createUploadProgress(istest,success,description);
		else return null;
	}
	
	public void cleanUI(final Activity activity) {
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				setStatusMSG("", true);
				setOverallFailOrPass(false);
				Chronometer cronometer = (Chronometer) activity.findViewById(R.id.chronometer);
				cronometer.setBase(SystemClock.elapsedRealtime());
				cronometer.setText("00:00");
				LinearLayout layout = (LinearLayout)activity.findViewById(R.id.barcode_adn_serial);
				layout.removeAllViews();
				if(sequenceFragment!=null)sequenceFragment.cleanUI();
				TextView currenttask = (TextView) activity.findViewById(R.id.currenttask);
				currenttask.setText("");
				TextView nexttask = (TextView) activity.findViewById(R.id.nexttask);
				nexttask.setText("");
				final ActivityUIHelperCallback activityUIHelperCallback = (ActivityUIHelperCallback) activity;
				activityUIHelperCallback.clearSerialConsole();
			}
		});

	}

	public void playSound(Activity activity){
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(activity.getApplicationContext(), notification);
		r.play();
	}
}
