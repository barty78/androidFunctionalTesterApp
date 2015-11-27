package com.pietrantuono.constants;

import com.pietrantuono.tests.superclass.Test;

public class NewMSensorResult extends NewMResult {
	private short sensor0min=0;
	private short sensor0max=0;
	private short sensor0avg=0;
	private Boolean sensor0avgpass=false;
	private Boolean sensor0stabilitypass=false;
	private short sensor1min=0;
	private short sensor1max=0;
	private short sensor1avg=0;
	private Boolean sensor1avgpass=false;
	private Boolean sensor1stabilitypass=false;
	private short sensor2min=0;
	private short sensor2max=0;
	private short sensor2avg=0;
	private Boolean sensor2avgpass=false;
	private Boolean sensor2stabilitypass=false;
	
	public Boolean getSensor0stabilitypass() {
		return sensor0stabilitypass;
	}

	public void setSensor0stabilitypass(Boolean sensor0stabilitypass) {
		this.sensor0stabilitypass = sensor0stabilitypass;
	}

	public Boolean getSensor1stabilitypass() {
		return sensor1stabilitypass;
	}

	public void setSensor1stabilitypass(Boolean sensor1stabilitypass) {
		this.sensor1stabilitypass = sensor1stabilitypass;
	}

	public Boolean getSensor2stabilitypass() {
		return sensor2stabilitypass;
	}

	public void setSensor2stabilitypass(Boolean sensor2stabilitypass) {
		this.sensor2stabilitypass = sensor2stabilitypass;
	}

	public short getSensor0min() {
		return sensor0min;
	}

	public void setSensor0min(short sensor0min) {
		this.sensor0min = sensor0min;
	}

	public short getSensor0max() {
		return sensor0max;
	}

	public void setSensor0max(short sensor0max) {
		this.sensor0max = sensor0max;
	}

	public short getSensor0avg() {
		return sensor0avg;
	}

	public void setSensor0avg(short sensor0avg) {
		this.sensor0avg = sensor0avg;
	}

	public Boolean getSensor0AvgPass() {
		return sensor0avgpass;
	}

	public void setSensor0AvgPass(Boolean sensor0pass) {
		this.sensor0avgpass = sensor0pass;
	}

	public short getSensor1min() {
		return sensor1min;
	}

	public void setSensor1min(short sensor1min) {
		this.sensor1min = sensor1min;
	}

	public short getSensor1max() {
		return sensor1max;
	}

	public void setSensor1max(short sensor1max) {
		this.sensor1max = sensor1max;
	}

	public short getSensor1avg() {
		return sensor1avg;
	}

	public void setSensor1avg(short sensor1avg) {
		this.sensor1avg = sensor1avg;
	}

	public Boolean getSensor1AvgPass() {
		return sensor1avgpass;
	}

	public void setSensor1AvgPass(Boolean sensor1pass) {
		this.sensor1avgpass = sensor1pass;
	}

	public short getSensor2min() {
		return sensor2min;
	}

	public void setSensor2min(short sensor2min) {
		this.sensor2min = sensor2min;
	}

	public short getSensor2max() {
		return sensor2max;
	}

	public void setSensor2max(short sensor2max) {
		this.sensor2max = sensor2max;
	}

	public short getSensor2avg() {
		return sensor2avg;
	}

	public void setSensor2avg(short sensor2avg) {
		this.sensor2avg = sensor2avg;
	}

	public Boolean getSensor2AvgPass() {
		return sensor2avgpass;
	}

	public void setSensor2AvgPass(Boolean sensor2pass) {
		this.sensor2avgpass = sensor2pass;
	}

	
	
	public NewMSensorResult(Test test) {
		super(test);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sensor0avg;
		result = prime * result + ((sensor0avgpass == null) ? 0 : sensor0avgpass.hashCode());
		result = prime * result + sensor0max;
		result = prime * result + sensor0min;
		result = prime * result + ((sensor0stabilitypass == null) ? 0 : sensor0stabilitypass.hashCode());
		result = prime * result + sensor1avg;
		result = prime * result + ((sensor1avgpass == null) ? 0 : sensor1avgpass.hashCode());
		result = prime * result + sensor1max;
		result = prime * result + sensor1min;
		result = prime * result + ((sensor1stabilitypass == null) ? 0 : sensor1stabilitypass.hashCode());
		result = prime * result + sensor2avg;
		result = prime * result + ((sensor2avgpass == null) ? 0 : sensor2avgpass.hashCode());
		result = prime * result + sensor2max;
		result = prime * result + sensor2min;
		result = prime * result + ((sensor2stabilitypass == null) ? 0 : sensor2stabilitypass.hashCode());
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
		NewMSensorResult other = (NewMSensorResult) obj;
		if (sensor0avg != other.sensor0avg)
			return false;
		if (sensor0avgpass == null) {
			if (other.sensor0avgpass != null)
				return false;
		} else if (!sensor0avgpass.equals(other.sensor0avgpass))
			return false;
		if (sensor0max != other.sensor0max)
			return false;
		if (sensor0min != other.sensor0min)
			return false;
		if (sensor0stabilitypass == null) {
			if (other.sensor0stabilitypass != null)
				return false;
		} else if (!sensor0stabilitypass.equals(other.sensor0stabilitypass))
			return false;
		if (sensor1avg != other.sensor1avg)
			return false;
		if (sensor1avgpass == null) {
			if (other.sensor1avgpass != null)
				return false;
		} else if (!sensor1avgpass.equals(other.sensor1avgpass))
			return false;
		if (sensor1max != other.sensor1max)
			return false;
		if (sensor1min != other.sensor1min)
			return false;
		if (sensor1stabilitypass == null) {
			if (other.sensor1stabilitypass != null)
				return false;
		} else if (!sensor1stabilitypass.equals(other.sensor1stabilitypass))
			return false;
		if (sensor2avg != other.sensor2avg)
			return false;
		if (sensor2avgpass == null) {
			if (other.sensor2avgpass != null)
				return false;
		} else if (!sensor2avgpass.equals(other.sensor2avgpass))
			return false;
		if (sensor2max != other.sensor2max)
			return false;
		if (sensor2min != other.sensor2min)
			return false;
		if (sensor2stabilitypass == null) {
			if (other.sensor2stabilitypass != null)
				return false;
		} else if (!sensor2stabilitypass.equals(other.sensor2stabilitypass))
			return false;
		return true;
	}
	



}
