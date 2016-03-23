package com.pietrantuono.sensors;

interface OnSampleCallback {

	void onSample(int requestTimestampMS, short sensor0, short sensor1, short sensor2);
}
