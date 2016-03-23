package hydrix.pfmat.generic;

@SuppressWarnings("unused")
public interface CalibrationObserver
{
	void onCalibratedSensor(byte sensorIndex, boolean calibrationSuccessful, float calibratedOffset);
}
