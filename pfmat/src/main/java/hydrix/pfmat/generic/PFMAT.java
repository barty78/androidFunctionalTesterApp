package hydrix.pfmat.generic;

public class PFMAT
{
	public static final String LOGTAG = "PFMAT";
	
	public static final byte TX_GET_DEVICE_DETAILS = 0x01;
	public static final byte RX_DEVICE_DETAILS = 0x10;

	public static final byte TX_GET_SENSOR_DATA = 0x02;
	public static final byte RX_SENSOR_DATA = 0x20;
	
	public static final byte TX_GET_BATTERY_STATUS = 0x03;
	public static final byte RX_BATTERY_STATUS = 0x30;

	public static final byte TX_GET_ACCEL_DATA = 0x04;
	public static final byte RX_ACCEL_DATA = 0x40;

	public static final byte TX_SET_CONFIG = 0x05;
	public static final byte RX_CONFIG_DATA = 0x50;

	public static final byte TX_SET_REF_VOLTAGE = 0x06;
	public static final byte RX_REF_VOLTAGE = 0x60;
	
	public static final byte TX_SLEEP = 0x07;
	public static final byte RX_SLEEP = 0x70;
	
	public static final byte TX_SET_ZERO_VOLTAGE = 0x09;
	public static final byte RX_ZERO_VOLTAGE = (byte) 0x90;

	public static final byte TX_SET_ALL_VOLTAGE = 0x0A;
	public static final byte RX_ALL_VOLTAGE = (byte) 0xA0;

	public static final byte RX_DATA = (byte) 0xB0;

	public static final byte TX_SET_ACCEL_CONFIG = 0x0C;
	public static final byte RX_ACCEL_CONFIG = (byte) 0xC0;

	public static final short MIN_SENSOR_VALUE = 0;
	//public static final short MAX_SENSOR_VALUE = 0x3FF; // 10-bit range, 0 - 1023
	public static final short MAX_SENSOR_VALUE = 0xFFF; // 12-bit range, 0 - 4095
	
	public static final byte MIN_SENSOR_INDEX = 0;
	public static final byte MAX_SENSOR_INDEX = 2;
	
	public static final short MIN_VOLTAGE_VALUE = 0x01;
	public static final short MAX_VOLTAGE_VALUE = 0xFF;

	public static final short MIN_SLEEP_VALUE = 100;
	public static final short MAX_SLEEP_VALUE = 32000;

	public static final short CALIBRATION_FAILED = (short)0x8000;
	
	public static final short VOLTAGE_FAILED = (short)0x00;
	
	public static final short ZERO_FAILED = 0x00;

	public static final byte ACK = 0x79;
	public static final byte NACK = 0x1F;

	public static final short SLEEP_FAILED = (short)0x00;

	public static final byte SENSORS_ONLY = 0x00;
	public static final byte ACCEL_ONLY = 0x01;
	public static final byte SENSORS_AND_ACCEL = 0x02;

	public static final int BATTERY = 1 << 0;
	public static final int RSSI = 1 << 1;
	public static final int SENSORS = 1 << 2;
	public static final int ACCEL = 1 << 3;
	public static final int GYRO = 1 << 4;
	public static final int QUAT = 1 << 5;

	public static final String V2 = "0188";
	public static final String V3 = "0198";
}