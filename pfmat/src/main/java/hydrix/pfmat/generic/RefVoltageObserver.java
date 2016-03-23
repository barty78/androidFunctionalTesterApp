package hydrix.pfmat.generic;

public interface RefVoltageObserver
{
	void onRefVoltage(byte sensorIndex, short refVoltage);
}
