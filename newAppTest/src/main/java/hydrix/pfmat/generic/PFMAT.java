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

	public static final byte TX_GET_CAL_OFFSET = 0x05;
	public static final byte RX_CAL_OFFSET = 0x50;

	public static final byte TX_SET_REF_VOLTAGE = 0x06;
	public static final byte RX_REF_VOLTAGE = 0x60;
	
	public static final byte TX_SLEEP = 0x07;
	public static final byte RX_SLEEP = 0x70;
	
	public static final byte TX_SET_ZERO_VOLTAGE = 0x09;
	public static final byte RX_ZERO_VOLTAGE = (byte) 0x90;
	
	
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
	
	public static final short SLEEP_FAILED = (short)0x00;

}