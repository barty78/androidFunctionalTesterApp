package com.pietrantuono.sensors;

import hydrix.pfmat.generic.Motion;
import hydrix.pfmat.generic.Quaternion;

interface OnSampleCallback {

	void onSample(int requestTimestampMS, short sensor0, short sensor1, short sensor2);
	void onDataSample(int seqNumber, short sensor0, short sensor1, short sensor2, int batteryLevel);
	void onAccelSample(int requestTimestampMS, Motion.Acceleration accel, Motion.Rotation gyro, Quaternion quat);

}
