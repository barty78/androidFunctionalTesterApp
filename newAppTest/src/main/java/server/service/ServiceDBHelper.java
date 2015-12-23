package server.service;

import java.util.HashSet;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;

import server.pojos.Device;

public class ServiceDBHelper {
	
	public static void addDevices(List<Device> arg0){
		HashSet<Device> devicesReceived= new HashSet<Device>();
		HashSet<Device> devicesToBeSaved= new HashSet<Device>();
		devicesReceived.addAll(arg0);
		for (Device device:devicesReceived) {
			Device existingdevice=null;
			existingdevice=weHaveItAlready(device.getBarcode());
			if(existingdevice==null)devicesToBeSaved.add(device);
			else {
				if(device.getBt_addr()!=null && device.getBt_addr().length()<=0)existingdevice.setBt_addr(device.getBt_addr());
				if(device.getDeviceId()!=0 )existingdevice.setDeviceId(device.getDeviceId());
				if(device.getFwver()!=null && device.getFwver().length()<=0)existingdevice.setFwver(device.getFwver());
				if(device.getJobId()!=0 )existingdevice.setJobId(device.getJobId());
				if(device.getModel()!=null && device.getModel().length()<=0)existingdevice.setModel(device.getModel());
				if(device.getSerial()!=null && device.getSerial().length()<=0)existingdevice.setSerial(device.getSerial());
				devicesToBeSaved.add(existingdevice);
			}
		}
		ActiveAndroid.beginTransaction();
		try {
		        for (Device device:devicesToBeSaved) {
		        	device.save();
		        }
		        ActiveAndroid.setTransactionSuccessful();
		}
		finally {
		        ActiveAndroid.endTransaction();
		}
	}

	private static Device weHaveItAlready(String barcode) {
		Device device=null;
		device= new Select().from(Device.class).where("Barcode = ?",barcode).executeSingle();
		return device;
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
		return new Select().from(Device.class).where("Barcode = ?",barcode).execute().size()>0;

	}
	@SuppressWarnings("ucd")
	public static boolean isSerialAlreadySeen(String serial){
		List sameSerial= new Select().from(Device.class).where("Serial = ?",serial).execute();
		if(sameSerial!=null && sameSerial.size()>0)return true;
		return false;
		}

	public static Long saveBarcode(String barcode){
		if(barcode==null || barcode.length()<=0)return -1l;
		Device existing = null;
		existing=new Select().from(Device.class).where("Barcode = ?", barcode).executeSingle();
		if(existing!=null)return existing.getId();
		Device device= new Device();
		device.setBarcode(barcode);
		return device.save();
	}
	public static Long saveSerial(String barcode, String serial){
		if(barcode==null || barcode.length()<=0)return -1l;
		if(serial==null)return -1l;
		Device device = new Select().from(Device.class).where("Barcode = ?", barcode).executeSingle();
		device.setSerial(serial);
		return device.save();
	}
	public static Long saveMac(String barcode, String mac){
		if(barcode==null || barcode.length()<=0)return -1l;
		if(mac==null)return -1l;
		Device device = new Select().from(Device.class).where("Barcode = ?", barcode).executeSingle();
		device.setBt_addr(mac);
		return device.save();
	}

	public static boolean isSerialAlreadySeen(String barcode, String serial){
		if(barcode==null || barcode.length()<=0)return true;
		if(serial==null)return true;
		String args[]= new String[2];
		args[0]=barcode;
		args[1]=serial;
		return new Select().from(Device.class).where("Barcode = ? AND Serial = ?", args).execute().size()>0;
	}

	public static boolean isMacAlreadySeen(String barcode, String mac){
		if(barcode==null || barcode.length()<=0)return true;
		if(mac==null)return true;
		String args[]= new String[2];
		args[0]=barcode;
		args[1]=mac;
		return new Select().from(Device.class).where("Barcode = ? AND bt_addr = ?", args).execute().size()>0;
	}
}
