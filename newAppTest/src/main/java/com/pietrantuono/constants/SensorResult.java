package com.pietrantuono.constants;

import com.pietrantuono.tests.superclass.Test;

public class SensorResult extends com.pietrantuono.constants.Result {
	private Boolean singleSensorTest = false;

	public static int min = 0;
	public static int max = 1;
	public static int avg = 2;

	public static int avgresult = 0;
	public static int varresult = 1;

	private Boolean[][] Result = {{false, false}, {false, false}, {false, false}};

	private short[][] sensor = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

	public boolean isSingleSensorTest() { return singleSensorTest.booleanValue(); }

	public void singleSensorTest() {
		this.singleSensorTest = true;
	}

	public Boolean getSensor0stabilitypass() { return Result[0][varresult].booleanValue();
	}

	public void setSensor0stabilitypass(boolean sensor0stabilitypass) {
		this.Result[0][varresult] = sensor0stabilitypass;
	}

	public Boolean getSensor1stabilitypass() {
		return Result[1][varresult].booleanValue();
	}

	public void setSensor1stabilitypass(boolean sensor1stabilitypass) {
		this.Result[1][varresult] = sensor1stabilitypass;
	}

	public Boolean getSensor2stabilitypass() {
		return Result[2][varresult].booleanValue();
	}

	public void setSensor2stabilitypass(boolean sensor2stabilitypass) {
		this.Result[2][varresult] = sensor2stabilitypass;
	}

	public boolean getSensorAvgPass(int sensor) { return Result[sensor][avgresult]; }

	public void setSensorAvgPass(int sensor, boolean sensoravgpass) {
		this.Result[sensor][avgresult] = sensoravgpass;
	}

	public boolean getSensorStabilityPass(int sensor) { return Result[sensor][varresult]; }

	public void setSensorStabilityPass(int sensor, boolean sensorstabilitypass) {
		this.Result[sensor][varresult] = sensorstabilitypass;
	}

	public void setSensor(int sensor, short min, short max, short avg) {
		this.sensor[sensor] = new short[]{min, max, avg};
	}

	public short[] getSensor(int sensor) {
		return this.sensor[sensor];
	}

	public short getSensorAvg(int sensor) { return this.sensor[sensor][avg]; }
	public short getSensorVar(int sensor) { return (short) (this.sensor[sensor][max] - this.sensor[sensor][min]); }
	public short getSensorMax(int sensor) { return this.sensor[sensor][max]; }
	public short getSensorMin(int sensor) { return this.sensor[sensor][min]; }

	public void setSensor0(short min, short max, short avg) {
		this.sensor[0] = new short[]{min, max, avg};
	}

	public void setSensor1(short min, short max, short avg) {
		this.sensor[1] = new short[]{min, max, avg};
	}

	public void setSensor2(short min, short max, short avg) {
		this.sensor[2] = new short[]{min, max, avg};
	}

	public short getSensor0min() { return sensor[0][min]; }

	public void setSensor0min(short sensor0min) {
		this.sensor[0][min] = sensor0min;
	}

	public short getSensor1min() { return sensor[1][min]; }

	public void setSensor1min(short sensor1min) {
		this.sensor[1][min] = sensor1min;
	}

	public short getSensor2min() { return sensor[2][min]; }

	public void setSensor2min(short sensor2min) {
		this.sensor[2][min] = sensor2min;
	}

	public short getSensor0max() { return sensor[0][max]; }

	public void setSensor0max(short sensor0max) {
		this.sensor[0][max] = sensor0max;
	}

	public short getSensor1max() { return sensor[1][max]; }

	public void setSensor1max(short sensor1max) {
		this.sensor[1][max] = sensor1max;
	}

	public short getSensor2max() { return sensor[2][max]; }

	public void setSensor2max(short sensor2max) {
		this.sensor[2][max] = sensor2max;
	}

	public short getSensor0avg() { return sensor[0][avg]; }

	public void setSensor0avg(short sensor0avg) {
		this.sensor[0][avg] = sensor0avg;
	}

	public short getSensor1avg() { return sensor[1][avg]; }

	public void setSensor1avg(short sensor1avg) {
		this.sensor[1][avg] = sensor1avg;
	}

	public short getSensor2avg() { return sensor[2][avg]; }

	public void setSensor2avg(short sensor2avg) {
		this.sensor[2][avg] = sensor2avg;
	}

	public Boolean getSensor0AvgPass() {
		return Result[0][avgresult].booleanValue();
	}

	public void setSensor0AvgPass(Boolean sensor0pass) {
		this.Result[0][avgresult] = sensor0pass;
	}

	public Boolean getSensor1AvgPass() {
		return Result[1][avgresult].booleanValue();
	}

	public void setSensor1AvgPass(Boolean sensor1pass) {
		this.Result[1][avgresult] = sensor1pass;
	}

	public Boolean getSensor2AvgPass() {
		return Result[2][avgresult].booleanValue();
	}

	public void setSensor2AvgPass(Boolean sensor2pass) {
		this.Result[2][avgresult] = sensor2pass;
	}

	
	public SensorResult(Test test) {
		super(test);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getSensor0avg();
		result = prime * result + ((getSensor0AvgPass() == null) ? 0 : getSensor0AvgPass().hashCode());
		result = prime * result + getSensor0max();
		result = prime * result + getSensor0min();
		result = prime * result + ((getSensor0stabilitypass() == null) ? 0 : getSensor0stabilitypass().hashCode());
		result = prime * result + getSensor1avg();
		result = prime * result + ((getSensor1AvgPass() == null) ? 0 : getSensor1AvgPass().hashCode());
		result = prime * result + getSensor1max();
		result = prime * result + getSensor1min();
		result = prime * result + ((getSensor1stabilitypass() == null) ? 0 : getSensor1stabilitypass().hashCode());
		result = prime * result + getSensor2avg();
		result = prime * result + ((getSensor2AvgPass() == null) ? 0 : getSensor2AvgPass().hashCode());
		result = prime * result + getSensor2max();
		result = prime * result + getSensor2min();
		result = prime * result + ((getSensor2stabilitypass() == null) ? 0 : getSensor2stabilitypass().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorResult other = (SensorResult) obj;
		if (getSensor0avg() != other.getSensor0avg())
			return false;
		if (getSensor0AvgPass() == null) {
			if (other.getSensor0AvgPass() != null)
				return false;
		} else if (!getSensor0AvgPass().equals(other.getSensor0AvgPass()))
			return false;
		if (getSensor0max() != other.getSensor0max())
			return false;
		if (getSensor0min() != other.getSensor0min())
			return false;
		if (getSensor0stabilitypass() == null) {
			if (other.getSensor0stabilitypass() != null)
				return false;
		} else if (!getSensor0stabilitypass().equals(other.getSensor0stabilitypass()))
			return false;
		if (getSensor1avg() != other.getSensor1avg())
			return false;
		if (getSensor1AvgPass() == null) {
			if (other.getSensor1AvgPass() != null)
				return false;
		} else if (!getSensor1AvgPass().equals(other.getSensor1AvgPass()))
			return false;
		if (getSensor1max() != other.getSensor1max())
			return false;
		if (getSensor1min() != other.getSensor1min())
			return false;
		if (getSensor1stabilitypass() == null) {
			if (other.getSensor1stabilitypass() != null)
				return false;
		} else if (!getSensor1stabilitypass().equals(other.getSensor1stabilitypass()))
			return false;
		if (getSensor2avg() != other.getSensor2avg())
			return false;
		if (getSensor2AvgPass() == null) {
			if (other.getSensor2AvgPass() != null)
				return false;
		} else if (!getSensor2AvgPass().equals(other.getSensor2AvgPass()))
			return false;
		if (getSensor2max() != other.getSensor2max())
			return false;
		if (getSensor2min() != other.getSensor2min())
			return false;
		if (getSensor2stabilitypass() == null) {
			if (other.getSensor2stabilitypass() != null)
				return false;
		} else if (!getSensor2stabilitypass().equals(other.getSensor2stabilitypass()))
			return false;
		return true;
	}
}
