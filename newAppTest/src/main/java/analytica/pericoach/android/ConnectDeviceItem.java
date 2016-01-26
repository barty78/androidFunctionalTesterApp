package analytica.pericoach.android;

import android.bluetooth.BluetoothDevice;

	public class ConnectDeviceItem {
		private String mName;
		BluetoothDevice mDevice;


		// Construction for device rows
		public ConnectDeviceItem(Type type, String name,
				BluetoothDevice device, int iconResourceId) {
			mName = name;
			mDevice = device;
		}

		@Override
		public String toString() {
			return mName;
		}

		public final String getName() {
			return mName;
		}

		public final BluetoothDevice getDevice() {
			return mDevice;
		}

	}