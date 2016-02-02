package hydrix.pfmat.generic;

import android.util.FloatMath;

public class Motion
{
	// Members
	private Acceleration mAccel;	// Acceleration
	private Rotation mGyro;			// Gyroscope
	private Quaternion mQuat;		// Quaternion
	private float[] mYPR;			// Yaw/Pitch/Roll
	
	static public class VectorFloat
	{
		public float mx;
		public float my;
		public float mz;
		
		public VectorFloat()
		{
			mx = 0;
			my = 0;
			mz = 0;
		}
		
		public VectorFloat(float x, float y, float z)
		{
			mx = x;
			my = y;
			mz = z;
		}
		
		public float getMagnitude()
		{
			return FloatMath.sqrt(mx * mx + my * my + mz * mz);
		}
		
		void normalize()
		{
			float m = getMagnitude();
			mx /= m;
			my /= m;
			mz /= m;	
		}
		
		VectorFloat getNormalized()
		{
			VectorFloat r = new VectorFloat(mx, my, mz);
			r.normalize();
			return r;
		}
		
		 void rotate(Quaternion q) {
	            Quaternion p = new Quaternion(0, mx, my, mz);

	            // quaternion multiplication: q * p, stored back in p
	            p = q.getProduct(p);

	            // quaternion multiplication: p * conj(q), stored back in p
	            p = p.getProduct(q.getConjugate());

	            // p quaternion is now [0, x', y', z']
	            mx = p.x;
	            my = p.y;
	            mz = p.z;
	        }

	        VectorFloat getRotated(Quaternion q) {
	            VectorFloat r = new VectorFloat(mx, my, mz);
	            r.rotate(q);
	            return r;
	        }
	}
	
	static public class VectorInt16
	{
		public int mx;
		public int my;
		public int mz;
	
		public VectorInt16()
		{
			mx = 0;
			my = 0;
			mz = 0;
		}
		
		public VectorInt16(int x, int y, int z)
		{
			mx = x;
			my = y;
			mz = z;
		}
		
		public float getMagnitude()
		{
			return (float) Math.sqrt(mx*mx + my*my + mz*mz);
		}
		
		public void normalise()
		{
			float m = getMagnitude();
			mx /= m;
			my /= m;
			mz /= m;
		}
		
		public VectorInt16 getNormalised()
		{
			VectorInt16 r = new VectorInt16(mx, my, mz);
			r.normalise();
			return r;
		}
		
		void rotate(Quaternion q)
		{
			// http://www.cprogramming.com/tutorial/3d/quaternions.html
            // http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm
            // http://content.gpwiki.org/index.php/OpenGL:Tutorials:Using_Quaternions_to_represent_rotation
            // ^ or: http://webcache.googleusercontent.com/search?q=cache:xgJAp3bDNhQJ:content.gpwiki.org/index.php/OpenGL:Tutorials:Using_Quaternions_to_represent_rotation&hl=en&gl=us&strip=1
        
            // P_out = q * P_in * conj(q)
            // - P_out is the output vector
            // - q is the orientation quaternion
            // - P_in is the input vector (a*aReal)
            // - conj(q) is the conjugate of the orientation quaternion (q=[w,x,y,z], q*=[w,-x,-y,-z])
			Quaternion p = new Quaternion(0, mx, my, mz);
			
            // quaternion multiplication: q * p, stored back in p
			p = q.getProduct(p);
			
			// quaternion multiplication: p * conj(q), stored back in p
            p = p.getProduct(q.getConjugate());

            // p quaternion is now [0, x', y', z']
            mx = (int) p.x;
            my = (int) p.y;
            mz = (int) p.z;
		}
		
		VectorInt16 getRotated(Quaternion q) {
            VectorInt16 r = new VectorInt16(mx, my, mz);
            r.rotate(q);
            return r;
        }
	}
	
	
	static public class Acceleration
	{
		public final int mAccelX;
		public final int mAccelY;
		public final int mAccelZ;
		public Acceleration(int accelx, int accely, int accelz)
		{
			mAccelX = accelx;
			mAccelY = accely;
			mAccelZ = accelz;
		}
		public final int getAccelX() {return mAccelX;}
		public final int getAccelY() {return mAccelY;}
		public final int getAccelZ() {return mAccelZ;}

	}
	
	static public class Rotation
	{
		public final int mGyroX;
		public final int mGyroY;
		public final int mGyroZ;
		public Rotation(int gyrox, int gyroy, int gyroz)
		{
			mGyroX = gyrox;
			mGyroY = gyroy;
			mGyroZ = gyroz;
		}
		public final int getGyroX() {return mGyroX;}
		public final int getGyroY() {return mGyroY;}
		public final int getGyroZ() {return mGyroZ;}
	}
	
	// Construction
	public Motion(Acceleration accel, Rotation gyro, Quaternion quat, float[] ypr)
	{
		mAccel = accel;
		mGyro = gyro;
		mQuat = quat;
		mYPR = ypr;
	}
	
	public final Acceleration getAccel()
	{
		return mAccel;
	}
	
	public final Rotation getGyro()
	{
		return mGyro;
	}
	
	public final Quaternion getQuat()
	{
		return mQuat;
	}
	
	public final float[] getYPR()
	{
		return mYPR;
	}
	
	public final int getAccelforAxis(char axis)
	{
		switch(axis) {
		case 'X': 
			return mAccel.getAccelX();		
		case 'Y':
			return mAccel.getAccelY();
		case 'Z':
			return mAccel.getAccelZ();
		}
		return 0;
	
	}
	
	public final int getGyroforAxis(char axis)
	{
		switch(axis) {
		case 'X': 
			return mGyro.getGyroX();		
		case 'Y':
			return mGyro.getGyroY();
		case 'Z':
			return mGyro.getGyroZ();
		}
		return 0;
	}
	
	public final int getGravity( )
	{
		return 0;
	}
	
	
}