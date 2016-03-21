package hydrix.pfmat.generic;

@SuppressWarnings("unused")
public interface CalibrationObserver
{
	public void onCalibratedSensor(byte sensorIndex, boolean calibrationSuccessful, float calibratedOffset);
}
