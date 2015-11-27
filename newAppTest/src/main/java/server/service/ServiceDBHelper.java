package server.service;

import java.util.HashSet;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import server.pojos.Device;

public class ServiceDBHelper {
	
	public static void addDevices(List<Device> arg0){
		HashSet<Device> devices= new HashSet<Device>();
		devices.addAll(arg0);
		HashSet<Device> temp= new HashSet<Device>();
		for(Device device:devices){
			if(!isDeviceAlreadySeen(device))temp.add(device);
		}
		
		ActiveAndroid.beginTransaction();
		try {
		        for (Device device:temp) {
		        	device.save();
		        }
		        ActiveAndroid.setTransactionSuccessful();
		}
		finally {
		        ActiveAndroid.endTransaction();
		}
	}
	
	private static boolean isDeviceAlreadySeen(Device device){
		List sameBarcode= new Select().from(Device.class).where("Barcode = ?",device.getBarcode()).execute();
		if(sameBarcode!=null && sameBarcode.size()>0)return true;
		List sameSerial= new Select().from(Device.class).where("Serial = ?",device.getBarcode()).execute();
		if(sameSerial!=null && sameSerial.size()>0)return true;
		return false;
	}
	@SuppressWarnings("ucd")
	public static boolean isBarcodeAlreadySeen(String barcode){
		List sameBarcode= new Select().from(Device.class).where("Barcode = ?",barcode).execute();
		if(sameBarcode!=null && sameBarcode.size()>0)return true;
		return false;
	}
	@SuppressWarnings("ucd")
	public static boolean isSerialAlreadySeen(String serial){
		List sameSerial= new Select().from(Device.class).where("Serial = ?",serial).execute();
		if(sameSerial!=null && sameSerial.size()>0)return true;
		return false;
		}
}
