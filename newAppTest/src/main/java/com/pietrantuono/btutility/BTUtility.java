package com.pietrantuono.btutility;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import com.pietrantuono.activities.MyOnCancelListener;
import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.R;
import com.pietrantuono.pericoachengineering.util.Utils;
import com.pietrantuono.sensors.NewDevice;
import com.pietrantuono.sensors.NewPFMATDevice;
import com.pietrantuono.tests.implementations.BatteryLevelUUTVoltageTest;
import com.pietrantuono.tests.superclass.Test;
import com.radiusnetworks.bluetooth.BluetoothCrashResolver;

import analytica.pericoach.android.ConnectDeviceItem;
import analytica.pericoach.android.DBManager;
import analytica.pericoach.android.Type;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import hydrix.pfmat.generic.Device;
public class BTUtility {
	private WeakReference<Activity> activityRef;
	private BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	private ArrayList<ConnectDeviceItem> mListItems = null;
	private BTBroadcastReceiver mBTReceiver = new BTBroadcastReceiver();
	private ConnectReceiver mConnectReceiver = new ConnectReceiver();
	private static final String INTENT_CONNECT_FAILED = "INTENT_CONNECT_FAILED";
	private static final String INTENT_CONNECT_SUCCEEDED = "INTENT_CONNECT_SUCCEEDED";
	private ProgressDialog progressdialog;
	private String mFirmwareVer;
	private static final String DISCONNECTED = "disconnected";
	private String mDeviceId;
	private String scancode;
	private Boolean deviceIDOK = false;
	private Boolean scancodeOK = false;
	// private IOIOActivityListener ioioActivityListener;
	private String model = null;
	private BluetoothCrashResolver bluetoothCrashResolver;
	private Boolean isstopped = false;
	private AlertDialog alertDialog;
	private AlertDialog alertDialog2;
	private AlertDialog alertDialog3;
	private Test bluetoothConnectTest;
	private int retries = 0;

	private static Activity activity;
	private class ConnectReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(INTENT_CONNECT_FAILED)) {
				onConnectFailed();
			} else if (intent.getAction().equals(INTENT_CONNECT_SUCCEEDED)) {
				onConnectSucceeded();
			}
		}
	}
	private class BTBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
					if (bluetoothCrashResolver != null)
						bluetoothCrashResolver
								.notifyScannedDevice(device, null);
					onDiscoverDevice(device);
				}
			}
			// If we aren't connected to a device and discovery finished, kick
			// off a new discovery.
			else if (mListItems.size() == 0) {
				if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent
						.getAction())) {
					if (progressdialog != null)
						progressdialog.dismiss();
					startDiscovery();
				}
			}
		}
	}
	public BTUtility(Activity activity1, String scancode,
			NewIOIOActivityListener IOIOActivityListener) {
		if (isstopped)
			return;
		this.activityRef = new WeakReference<Activity>(activity1);
		this.scancode = scancode;
		final Activity activity = activityRef.get();
		if (activity == null)
			return;
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isstopped)
					return;
				progressdialog = new ProgressDialog(activity);
				progressdialog.setTitle("Connecting PCB via Bluetooth");
				progressdialog.setMessage("Looking for PCBs, please wait");
			}
		});
	}
	private void onConnectFailed() {
		retries++;
		if (retries < 3) {
			startDiscovery();
			return;
		}
		((NewIOIOActivityListener) activityRef.get()).addFailOrPass(
				false, false, bluetoothConnectTest.getDescription());
	}
	public void connectProbeViaBT(Test bluetoothConnectTest) {
		if (isstopped)
			return;
		this.bluetoothConnectTest=bluetoothConnectTest;
		mListItems = new ArrayList<ConnectDeviceItem>();
		// Register for BT device discovery broadcast events
		IntentFilter eventFilter = new IntentFilter();
		eventFilter.addAction(BluetoothDevice.ACTION_FOUND);
		eventFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		eventFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		Activity activity = activityRef.get();
		if (activity == null)
			return;
		activity.registerReceiver(mBTReceiver, eventFilter);
		// Register for connection results
		IntentFilter connectFilter = new IntentFilter();
		connectFilter.addAction(INTENT_CONNECT_FAILED);
		connectFilter.addAction(INTENT_CONNECT_SUCCEEDED);
		// connectFilter.addAction(INTENT_REFRESH);
		activity.registerReceiver(mConnectReceiver, connectFilter);
		// Populate the ConnectDeviceItemList so that we can check/unpair
		// any existing devices - shouldn't be any
		Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
		if (pairedDevices.size() > 0)
			for (BluetoothDevice device : pairedDevices)
				if (device.getName() != null
						&& (device.getName().contains("PeriCoach"))) {
					mListItems.add(new ConnectDeviceItem(Type.DEVICE, device
							.getName(), device, R.drawable.device));
				}
		removeDevicesFromList(true, true);
		startDiscovery();
	}
	private void removeDevicesFromList(boolean removeBondedDevices,
			boolean unpair) {
		// Clear devices from the adapter... we always leave item0, which is the
		// Refresh option
		for (int i = mListItems.size() - 1; i == 0; i--) {
			if (unpair) {
				Log.d("BT:", "Unpair - " + mListItems.get(i).getName());
				Utils.unpairDevice(mListItems.get(i).getDevice());
			}
			if (removeBondedDevices
					|| mListItems.get(i).getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
				Log.d("BT:", "List Remove - " + mListItems.get(i).getName());
				mListItems.remove(i);
			}
		}
	}
	private final void startDiscovery() {
		if (isstopped)
			return;
		Activity activity = activityRef.get();
		if (activity == null || activity.isFinishing())
			return;
		NewPFMATDevice.disconnect();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				progressdialog.show();
			}
		});
		if (activityRef.get() != null) {
			bluetoothCrashResolver = new BluetoothCrashResolver(
					activityRef.get());
			bluetoothCrashResolver.start();
		}
		mBTAdapter.startDiscovery();
	}
	private final void onDiscoverDevice(BluetoothDevice device) {
		if (isstopped)
			return;
		if (device.getName() != null) {
			Log.d("BT NAME:", device.getName());
			if (device.getName() != ""
					&& (device.getName().contains("PeriCoach-" + scancode))) {
				mListItems.add(new ConnectDeviceItem(Type.DEVICE, device
						.getName(), device, R.drawable.device));
				// Attempt to connect to specified device. This happens
				// asynchronously, and we'll end up with onConnectSucceeded or
				// onConnectFailed called when it's complete
				Activity activity = activityRef.get();
				if (activity == null)
					return;
				NewPFMATDevice.specifyDevice(device, activity);
				NewPFMATDevice.connect(activity, INTENT_CONNECT_SUCCEEDED,
						INTENT_CONNECT_FAILED);
				if (progressdialog != null) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							progressdialog.dismiss();
						}
					});
				}
			}
		}
	}
	private void onConnectSucceeded() {
		if (isstopped)
			return;
		if (progressdialog != null) {
			Activity activity = activityRef.get();
			if (activity == null)
				return;
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressdialog.dismiss();
				}
			});
		}
		NewDevice device = NewPFMATDevice.getDevice();
		NewDevice.Information info = (device == null || !device.isConnected()) ? null
				: NewPFMATDevice.getDevice().getInformation();
		// Firmware version
		if (info == null
				|| info.mFirmwareVersion == Device.FIRMWARE_VERSION_UNKNOWN)
			mFirmwareVer = DISCONNECTED;
		else {
			Object[] args = new Object[4];
			args[0] = new Integer(info.mFirmwareVersion >> 24);
			args[1] = new Integer((info.mFirmwareVersion >> 16) & 0xFF);
			args[2] = new Integer((info.mFirmwareVersion >> 8) & 0xFF);
			args[3] = new Integer(info.mFirmwareVersion & 0xFF);
			mFirmwareVer = String.format("%d.%d.%d.%d", args);
		}
		mDeviceId = (info == null || info.mSerialNumber == null || info.mSerialNumber
				.length() == 0) ? DISCONNECTED : info.mSerialNumber;
		model = (info == null || info.mModel == null || info.mModel.length() == 0) ? null
				: info.mModel;
//		checkDeviceID(mDeviceId);
		start();
	}
	private void checkDeviceID(String device) {
		if (isstopped)
			return;
		if (scancode == null || scancode.isEmpty())
			scancode = "";
		ArrayList<String> devices = null;
		final Activity activity = activityRef.get();
		if (activity == null)
			return;
		DBManager db = (new DBManager(activity));
		if (!deviceIDOK) {
			ArrayList<String> temp = db.getAllDevices();
			if (temp != null && temp.size() > 0) {
				devices = new ArrayList<String>();
				devices.addAll(temp);
				Boolean duplicate = false;
				for (String item : devices) {
					if (item.equalsIgnoreCase(device))
						duplicate = true;
				}
				if (!duplicate) {
					deviceIDOK = true;
					checkScancode();
					return;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							activity);
					builder.setMessage("This device has already been tested")
							.setTitle("Device already used")
							.setPositiveButton("Continue anyway",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (isstopped)
												return;
											deviceIDOK = true;
											checkScancode();
											return;
										}
									})
							.setNegativeButton("Stop test",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											activity.onBackPressed();
											return;
										}
									}).setCancelable(true);
					// Create the AlertDialog object and return it
					builder.setOnCancelListener(new MyOnCancelListener(activity));
					alertDialog2 = builder.create();
					alertDialog2.show();
				}
			} else
				checkScancode();
		} else
			checkScancode();
	}
	private void checkScancode() {
		if (isstopped)
			return;
		if (scancode == null || scancode.isEmpty())
			scancode = "";
		ArrayList<String> scancodes = null;
		final Activity activity = activityRef.get();
		if (activity == null)
			return;
		DBManager db = (new DBManager(activity));
		if (!scancodeOK) {
			ArrayList<String> temp = db.getAllScancodes();
			if (temp != null && temp.size() > 0) {
				scancodes = new ArrayList<String>();
				scancodes.addAll(temp);
				Boolean duplicate = false;
				for (String item : scancodes) {
					if (item.equalsIgnoreCase(scancode))
						duplicate = true;
				}
				if (!duplicate) {
					scancodeOK = true;
					start();
					return;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							activity);
					builder.setMessage("This device has already been tested")
							.setTitle("Barcode already used")
							.setPositiveButton("Continue anyway",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (isstopped)
												return;
											scancodeOK = true;
											alertDialog.dismiss();
											start();
											return;
										}
									})
							.setNegativeButton("Stop test",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											activity.onBackPressed();
											return;
										}
									})
							.setNeutralButton("Edit scancode",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (isstopped)
												return;
											AlertDialog.Builder alert = new AlertDialog.Builder(
													activity);
											alert.setTitle("Edit scancode");
											alert.setMessage("You can change the scancode");
											// Set an EditText view to get user
											// input
											final EditText input = new EditText(
													activity);
											input.setInputType(InputType.TYPE_CLASS_NUMBER);
											input.setText(scancode);
											alert.setView(input);
											alert.setPositiveButton(
													"Ok",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int whichButton) {
															if (isstopped)
																return;
															String value = input
																	.getText()
																	.toString();
															scancode = value;
															checkScancode();
														}
													});
											alert.setNegativeButton(
													"Cancel",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int whichButton) {
															// Canceled.
															if (isstopped)
																return;
															checkScancode();
														}
													});
											alertDialog3 = alert.create();
											alertDialog3.show();
										}
									}).setCancelable(true);
					// Create the AlertDialog object and return it
					builder.setOnCancelListener(new MyOnCancelListener(activity));
					alertDialog = builder.create();
					alertDialog.show();
				}
			} else
				start();
		} else
			start();
	}
	private void start() {
		if (isstopped)
			return;
		if(bluetoothConnectTest!=null)bluetoothConnectTest.setSuccess(true);
		if ((NewIOIOActivityListener) activityRef.get() == null)
			return;
		Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				insertDeviceAndScancode(mDeviceId, scancode);
				((NewIOIOActivityListener) activityRef.get()).addFailOrPass(
						true, true, "Connected");
				//((NewIOIOActivityListener) activityRef.get()).goAndExecuteNextTest();
			}
		});
	}
	private Boolean insertDeviceAndScancode(String devid, String scancode) {
		if (isstopped)
			return false;
		Boolean inserted = false;
		if (scancode == null || scancode.isEmpty())
			scancode = "";
		final Activity activity = activityRef.get();
		if (activity == null)
			return false;
		DBManager db = (new DBManager(activity));
		db.insertDeviceID(devid);
		db.insertScancode(scancode);
		return inserted;
	}
	public String getSerial() {
		String serial = null;
		NewDevice device = NewPFMATDevice.getDevice();
		NewDevice.Information info = (device == null || !device.isConnected()) ? null
				: NewPFMATDevice.getDevice().getInformation();
		if (info != null)
			serial = info.mSerialNumber;
		return serial;
	}
	public String getModelNumber() {
		return model;
	}
	public String getFirmWareVersion() {
		return mFirmwareVer;
	}

	public short getBatteryLevel() {
		if (isstopped)
			return 0;
		NewDevice device = NewPFMATDevice.getDevice();
		NewDevice.Information info = (device == null || !device.isConnected()) ? null
				: NewPFMATDevice.getDevice().getInformation();
		device.sendGetBatteryStatus(null);
		if (info != null) {
			Log.d("BATTERY LEVEL", String.valueOf(info.mBatteryPercent));
			device = null;
			return info.mBatteryPercent;
		}
		return -1;
	}
	public short requestBatteryLevelAndWait() {
		if (isstopped)
			return 0;
		NewDevice device = NewPFMATDevice.getDevice();
		device.sendGetBatteryStatus(null);
		try {
			Thread.sleep(1*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		NewDevice.Information info = (device == null || !device.isConnected()) ? null
				: NewPFMATDevice.getDevice().getInformation();
		if (info != null) {
			Log.d("BATTERY LEVEL", String.valueOf(info.mBatteryPercent));
			device = null;
			return info.mBatteryPercent;
		}
		return -1;
	}

	public void getBatteryLevel(BatteryLevelUUTVoltageTest.Callback callback) {
		if (isstopped)
			return ;
		NewDevice device = NewPFMATDevice.getDevice();
		NewDevice.Information info = (device == null || !device.isConnected()) ? null
				: NewPFMATDevice.getDevice().getInformation();
		device.sendGetBatteryStatus(callback);

	}


	private void stopBTDiscovery() {
		if (mBTAdapter != null && mBTAdapter.isDiscovering())
			mBTAdapter.cancelDiscovery();
		Activity activity = activityRef.get();
		if (activity == null)
			return;
		try {
			activity.unregisterReceiver(mBTReceiver);
		} catch (Exception e) {
		}
		try {
			activity.unregisterReceiver(mConnectReceiver);
		} catch (Exception e) {
		}
	}

	public void pollSensor() {
		NewPFMATDevice.getDevice().sendGetSensorData(0);
	}

	public void setZeroVoltage(final Short voltage) {
		Handler handler = new Handler();
		Byte sensor = (byte) (0 & 0xFF);
		NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (1 & 0xFF);
				NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
			}
		}, 20);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (2 & 0xFF);
				NewPFMATDevice.getDevice().sendZeroVoltage(sensor, voltage);
			}
		}, 40);
	}


	public void setVoltage(final Short voltage) {
		Handler handler = new Handler();
		Byte sensor = (byte) (0 & 0xFF);
		NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
		Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (1 & 0xFF);
				Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
			}
		}, 20);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Byte sensor = (byte) (2 & 0xFF);
				Log.d("SENSOR", "Setting sensor " + sensor + " to " + voltage);
				NewPFMATDevice.getDevice().sendRefVoltage(sensor, voltage);
			}
		}, 40);
	}
	public void abort() {
		Log.d("BTUtility", "abort");
		if (alertDialog != null && alertDialog.isShowing())
			alertDialog.dismiss();
		if (alertDialog2 != null && alertDialog2.isShowing())
			alertDialog2.dismiss();
		if (alertDialog3 != null && alertDialog3.isShowing())
			alertDialog3.dismiss();
		isstopped = true;
		stopBTDiscovery();
		if (NewPFMATDevice.getDevice() != null) {
			setVoltage((short) 127);
		}
		if (NewPFMATDevice.getDevice() != null) {
			NewPFMATDevice.getDevice().disconnect();
			NewPFMATDevice.getDevice().stop();
		}
		if (progressdialog != null && progressdialog.isShowing()
				&& activityRef.get() != null) {
			activityRef.get().runOnUiThread(new Runnable() {
				public void run() {
					progressdialog.dismiss();
				}
			});
		}
		try {
			IOIOUtils.getUtils().getSensor_High().write(true);
		} catch (Exception e) {
		}
		removeDevicesFromList(true, true);
		try {
			activityRef.get().unregisterReceiver(mBTReceiver);
		} catch (Exception e) {
		}
		try {
			activityRef.get().unregisterReceiver(mConnectReceiver);
		} catch (Exception e) {
		}
	}
	public void stop() {
		isstopped = true;
		if (NewPFMATDevice.getDevice() != null) {
			setVoltage((short) 127);
		}
		try {
			IOIOUtils.getUtils().getSensor_High().write(true);
		} catch (Exception e) {
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (NewPFMATDevice.getDevice() != null) {
					NewPFMATDevice.getDevice().sendSleep((short) 100);
				}
			}
		}, 200);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (NewPFMATDevice.getDevice() != null) {
					NewPFMATDevice.getDevice().disconnect();
				}
			}
		}, 300);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				removeDevicesFromList(true, true);
			}
		}, 400);
		if (progressdialog != null && progressdialog.isShowing()
				&& activityRef.get() != null) {
			activityRef.get().runOnUiThread(new Runnable() {
				public void run() {
					progressdialog.dismiss();
				}
			});
		}
		stopBTDiscovery();
	}
	@SuppressLint("NewApi")
	public static String listenToGetIOIOAddress(Activity activity){
		BTUtility.activity=activity;
		IntentFilter myfilter = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED);
		activity.registerReceiver(mReceiver, myfilter);
		String add="";
		return add;
	}
	private final static BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
	        	BTUtility.activity.unregisterReceiver(mReceiver);
	        	if(device==null)return;
	        	if(device.getName()==null)return;
	        	if(!device.getName().toLowerCase().contains("ioio"))return;
	        	if(device.getAddress()==null)return;
	        	PeriCoachTestApplication.setIOIOAddress(device.getAddress());
	        }
	    }
	};
	public static void unregisterIOIOAddressREceiver() {
		if(BTUtility.activity==null)return;
		try {activity.unregisterReceiver(mReceiver);}catch (Exception e){}
	}
}
