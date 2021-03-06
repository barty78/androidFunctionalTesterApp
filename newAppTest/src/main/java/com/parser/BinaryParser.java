package com.parser;


import java.io.ByteArrayOutputStream;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;






import com.pietrantuono.application.PeriCoachTestApplication;

import android.util.Log;

@SuppressWarnings("ucd")
public class BinaryParser  {
	private static final String TAG="BinaryParser";
	//String _filename;
	private byte[] _data;

	public BinaryParser() throws IOException{
		//_filename = iFileName;
		readData();

	}

	private void readData() throws IOException{

		ByteArrayOutputStream ous = null;
		//InputStream ios = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			try {
				ios=new FileInputStream (PeriCoachTestApplication.getFirmware());
			    } catch (IOException e) {
			        Log.e(TAG,e.toString());
			        e.printStackTrace();
			    }
			//InputStream caInput = new BufferedInputStream(is);
			//ios = new FileInputStream(new File(_filename));
			int read;
			while ((read = ios.read(buffer)) != -1)
				ous.write(buffer, 0, read);
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
		}
		
		_data = ous.toByteArray();

	}

	public byte[] getData() {
		return _data;
	}


}