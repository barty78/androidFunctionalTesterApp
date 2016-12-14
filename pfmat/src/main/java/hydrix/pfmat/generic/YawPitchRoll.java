package hydrix.pfmat.generic;


public class YawPitchRoll {

	public static float[] calculateYawPitchRoll(PacketRx_Data data) {
		Quaternion quat=data.getQuat();
		Motion.VectorFloat v = getGravity(quat);
		float[] ypr = getYawPitchRoll(quat, v);
//		Motion.Acceleration accel = data.getAccel();
		//VectorInt16 linearAccel = getLinearAccel(new VectorInt16(accel.mAccelX, accel.mAccelY, accel.mAccelZ), v);
		//VectorInt16 worldAccel = getLinearAccelInWorld(linearAccel, quat);
		//Log.d("LINEAR ACCEL:", "X:" + linearAccel.mx + " , " + "Y:" + linearAccel.my + " , " + "Z:" + linearAccel.mz + " ");
		//Log.d("LINEAR ACCEL WORLD:", "X:" + worldAccel.mx + " , " + "Y:" + worldAccel.my + " , " + "Z:" + worldAccel.mz + " ");
		//mSamples.add(timeOffsetMS, new Motion(accel, gyro, quat, yprData));
		return ypr;
	}

	public static float[] calculateYawPitchRoll(PacketRx_AccelData data){
		Quaternion quat=data.getQuat();
		Motion.VectorFloat v = getGravity(quat);
		float[] ypr = getYawPitchRoll(quat, v);
//		Motion.Acceleration accel = data.getAccel();
		//VectorInt16 linearAccel = getLinearAccel(new VectorInt16(accel.mAccelX, accel.mAccelY, accel.mAccelZ), v);
		//VectorInt16 worldAccel = getLinearAccelInWorld(linearAccel, quat);
		//Log.d("LINEAR ACCEL:", "X:" + linearAccel.mx + " , " + "Y:" + linearAccel.my + " , " + "Z:" + linearAccel.mz + " ");
		//Log.d("LINEAR ACCEL WORLD:", "X:" + worldAccel.mx + " , " + "Y:" + worldAccel.my + " , " + "Z:" + worldAccel.mz + " ");
		//mSamples.add(timeOffsetMS, new Motion(accel, gyro, quat, yprData));
		return ypr;
	}

	private final static Motion.VectorFloat getGravity(Quaternion q)
	{
		Motion.VectorFloat v = new Motion.VectorFloat();
		v.mx = 2 * (q.x * q.z - q.w * q.y);
		v.my = 2 * (q.w * q.x - q.y * q.z);
		v.mz = q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z; 
		return v;
	}
	private final static float[] getYawPitchRoll(Quaternion q, Motion.VectorFloat grav)
	{
		float[] ypr = new float[]{(float)0, (float)0, (float)0};
		
		ypr[0] = (float) Math.atan2(2 * q.x * q.y - 2 * q.w * q.z, 2 * q.w * q.w + 2 * q.x * q.x - 1);
		ypr[1] = (float) Math.atan(grav.mx / Math.sqrt(grav.my * grav.my + grav.mz * grav.mz));
	    // roll: (tilt left/right, about X axis)
	    ypr[2] = (float) Math.atan(grav.my / Math.sqrt(grav.mx * grav.mx + grav.mz * grav.mz));
		return ypr;
	}

	
}
