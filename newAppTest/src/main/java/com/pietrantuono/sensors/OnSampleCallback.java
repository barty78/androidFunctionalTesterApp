package com.pietrantuono.sensors;

interface OnSampleCallback {

	public void onSample(int requestTimestampMS, short sensor0,short sensor1,short sensor2);
}
