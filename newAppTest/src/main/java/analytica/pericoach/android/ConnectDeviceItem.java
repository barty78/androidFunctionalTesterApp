package analytica.pericoach.android;

import android.bluetooth.BluetoothDevice;

	public class ConnectDeviceItem {
		// Members
		private Type mType;
		private String mName;
		BluetoothDevice mDevice;
		private int mIconResourceId;

		// Construction
		public ConnectDeviceItem(Type type, String name, int iconResourceId) {
			mType = type;
			mName = name;
			mDevice = null;
			mIconResourceId = iconResourceId;
		}

		// Construction for device rows
		public ConnectDeviceItem(Type type, String name,
				BluetoothDevice device, int iconResourceId) {
			mType = type;
			mName = name;
			mDevice = device;
			mIconResourceId = iconResourceId;
		}

		@Override
		public String toString() {
			return mName;
		}

		// Accessors
		public final Type getType() {
			return mType;
		}

		public final String getName() {
			return mName;
		}

		public final BluetoothDevice getDevice() {
			return mDevice;
		}

		public final int getIconResourceId() {
			return mIconResourceId;
		}
	}