package hydrix.pfmat.generic;

public interface CalibrationObserver
{
	public void onCalibratedSensor(byte sensorIndex, boolean calibrationSuccessful, float calibratedOffset);
}
