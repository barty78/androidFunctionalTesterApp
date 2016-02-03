package hydrix.pfmat.generic;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceInfo implements Parcelable {
	
	public class Info
	{
		public final String mSerialNumber;
		public final String mDeviceID;
		public final String mFirmwareVer;
		public final Integer mJobID;
		
		public Info(String SerialNumber, String DeviceID, String FirmwareVer, Integer JobID)
		{
			mSerialNumber = SerialNumber;
			mDeviceID = DeviceID;
			mFirmwareVer = FirmwareVer;
			mJobID = JobID;
		}
	}

	private ArrayList<Info> mInfos;
	
	// Construction
		public DeviceInfo()
		{
			// Create an empty list ready for adding samples to
			mInfos = new ArrayList<Info>();
		}
		
		// Always called from UI thread so no need for synchronisation
		public synchronized final void add(String SerialNumber, String DeviceID, String FirmwareVer, Integer JobID)
		{
			mInfos.add(new Info(SerialNumber, DeviceID, FirmwareVer, JobID));
			
		}
		
		// Always called from UI thread so no need for synchronisation
		public synchronized final void add(Info info)
		{
			mInfos.add(info);
			
		}
		
		public synchronized final void clear()
		{
			mInfos.clear();
		}
		
		// This function must not be called while the session is still in progress, i.e. there is no thread synchronization at this point
		public final ArrayList<Info> getInfo() {return mInfos;}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			//dest.writeString(mSerialNumber);
			//dest.writeString(mDeviceID);
			
			
			
		}
}
