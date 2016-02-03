package server.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.query.Select;

import server.pojos.Device;
import server.pojos.DevicesList;

public class ServiceDBHelper {
	
	public static void addDevices(DevicesList arg0) {
		if (arg0.getNew() != null && arg0.getNew().size() > 0) addNewDevices(arg0.getNew());
		if (arg0.getUpdated() != null && arg0.getUpdated().size() > 0)updateDevices(arg0.getUpdated());
	}

	private static void updateDevices(List<Device> updatedDevices) {
		HashSet<Device> devicesToBeSaved= new HashSet<Device>();
		for (Device device:updatedDevices) {
			Device existingdevice = weHaveItAlready(device.getBarcode());
			if(existingdevice!=null) {
				existingdevice.setBarcode(device.getBarcode() != null ? device.getBarcode() : "");
				existingdevice.setBt_addr(device.getBt_addr() != null ? device.getBt_addr() : "");
				existingdevice.setDeviceId(device.getDeviceId());
				existingdevice.setExec_Tests(device.getExec_Tests());
				existingdevice.setFwver(device.getFwver() != null ? device.getFwver() : "");
				existingdevice.setJobId(device.getJobId());
				existingdevice.setModel(device.getModel() != null ? device.getModel() : "");
				existingdevice.setSerial(device.getSerial() != null ? device.getSerial() : "");
				existingdevice.setStatus(device.getStatus());
				devicesToBeSaved.add(existingdevice);
			}
			else devicesToBeSaved.add(device);
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

	private static void addNewDevices(List<Device> newDevices) {
		HashSet<Device> devicesToBeSaved= new HashSet<Device>();
		devicesToBeSaved.addAll(newDevices);
		Iterator<Device> iterator = devicesToBeSaved.iterator();
		while (iterator.hasNext()){
			if(weHaveItAlready(iterator.next().getBarcode())!=null)iterator.remove();
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


	@SuppressWarnings("ucd")
	public static boolean isBarcodeAlreadySeen(String barcode){
		return new Select().from(Device.class).where("Barcode = ?",barcode).execute().size()>0;

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

	public static long getMaxDeviceID(){
		Device max = new Select().from(Device.class).orderBy("deviceId DESC").executeSingle();
		return max.getDeviceId();
	}
	public static List<Model> foo(){
		return new Select().from(Device.class).orderBy("deviceId DESC").execute();

	}
}
