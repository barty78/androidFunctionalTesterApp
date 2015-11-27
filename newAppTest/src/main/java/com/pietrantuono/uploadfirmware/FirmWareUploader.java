package com.pietrantuono.uploadfirmware;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parser.BinaryParser;
import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.pericoach.newtestapp.R;

@SuppressWarnings("ucd")
public class FirmWareUploader {
	private static final String TAG = "FirmWareUploader";
	private OutputStream TX;
	private InputStream RX;
	private static final byte STM32_CMD_INIT = 0x7F;
	private static final byte STM32_CMD_GET = 0x00;
	private static final byte XOR_BYTE = (byte) 0xFF;
	private static final byte STM32_ACK = 0x79;
	private static final byte STM32_NACK = 0x1F;

	private Activity c;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private HashMap<String, Integer> _CMDList = new HashMap<String, Integer>();
	private int pid;
	private int[][] devices = {
			{ 0x412, 0x20000200, 0x20002800, 0x08000000, 0x08008000, 4, 1024,
					0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 },
			{ 0x410, 0x20000200, 0x20005000, 0x08000000, 0x08020000, 4, 1024, // F1
					0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 },
			{ 0x414, 0x20000200, 0x20010000, 0x08000000, 0x08080000, 2, 2048,
					0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 },
			{ 0x418, 0x20001000, 0x20010000, 0x08000000, 0x08040000, 2, 2048,
					0x1FFFF800, 0x1FFFF80F, 0x1FFFB000, 0x1FFFF800 },
			{ 0x420, 0x20000200, 0x20002000, 0x08000000, 0x08020000, 4, 1024,
					0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800 },
			{ 0x430, 0x20000800, 0x20018000, 0x08000000, 0x08100000, 2, 2048,
					0x1FFFF800, 0x1FFFF80F, 0x1FFFE000, 0x1FFFF800 },
			{ 0x427, 0x20000000, 0x2000BFFF, 0x08000000, 0x0803FFFF, 16, 256, // L1
					0x1FF80000, 0x1FF8001F, 0x1FFF0000, 0x1FF01FFF }, { 0x0 } };
	private int fl_start;
	private int fl_end;
	@SuppressWarnings("ucd")
	public int mem_end;
	private ProgressBar progress;
	private TextView percent;
	private Boolean isstopped = false;
	private WriteTask task = null;
	private UploaderListener listener;

	private int stm32_gen_cs(int v) {
		return ((v & 0xFF000000) >> 24) ^ ((v & 0x00FF0000) >> 16)
				^ ((v & 0x0000FF00) >> 8) ^ ((v & 0x000000FF) >> 0);
	}

	public FirmWareUploader(OutputStream TX, InputStream RX, Activity c,
			ProgressBar progress, TextView percent,
			NewIOIOActivityListener listner) {
		this.TX = TX;
		this.c = c;
		this.RX = RX;
		this.progress = progress;
		this.percent = percent;
		isstopped = false;
	}

	public void write(int c) {
		if (isstopped)
			return;
		try {
			TX.write(c);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	public boolean deviceInit() {// IMPOSSIBLE TO CHECK BECAUSE NOT ALWAYS
									// DEVICE
									// ACKNOWLEDGES
		if (isstopped)
			return false;
		System.out.printf("Init : %x.\n", STM32_CMD_INIT);

		write(STM32_CMD_INIT);

		int tmp = (readWithTimeout(2000) & 0xFF);
		if (tmp == STM32_ACK) {
			return true;
		}
		return false;
	}

	private class WriteTask extends AsyncTask<Void, Void, Void> {
		private FileOutputStream fileOutputStream;
		private int prog = 0;

		protected Void doInBackground(Void... v) {
			if (isCancelled())
				return null;
			try {

				BinaryParser aParser = new BinaryParser(c);

				int offset = 0;
				int size = aParser.getData().length;

				System.out.printf("Filesize : %x.\n", size);

				if (size > fl_end - fl_start) {
					System.err
							.println("File provided larger then available flash space.\n");
					return null;
				}

				//int npages = 0xFF;
				if (!erase((byte) 0xFF))			// Erase all memory
					return null;
				

				int addr = fl_start;
				int len = 0;
				byte[] buffer = new byte[256];

				ByteArrayInputStream aInput = new ByteArrayInputStream(
						aParser.getData());

				System.out.println();

				while (addr < fl_end && offset < size && !isCancelled()) {

					int left = fl_end - addr;
					len = buffer.length > left ? left : buffer.length;
					len = len > (size - offset) ? size - offset : len;

					aInput.read(buffer, 0, len);
					if (isCancelled())
						return null;
					if (!writeMemory(addr, buffer, len)) {
						System.err.printf(
								"Failed to write memory at address 0x%08x\n",
								addr);
						break;
					} else {
						fileOutputStream.write(buffer, 0, len);
					}
					addr += len;
					offset += len;

					System.out.printf("\rWrote %saddress 0x%08x (%.2f%%)",
							false ? "and verified " : "", addr, (100.0f / size)
									* offset);
					prog = (int) ((100.0f / size) * offset);
					// updateUiProgress(prog);
					publishProgress();
				}

				System.out.printf("\nDone.\n");
				fileOutputStream.close();

			} catch (IOException e) {
				System.err.println("Unable to load the file.");
			}

			return null;

		}

		protected void onProgressUpdate(Void... v) {
			if (isCancelled())
				return;
			c.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (progress != null)
						progress.setProgress(prog);
					else
						Log.e(TAG, "progress si null");
					if (percent != null)
						percent.setText("" + prog + "%");
					else
						Log.e(TAG, "percent si null");

				}
			});

		}

		protected void onPreExecute() {
			if (isCancelled())
				return;
			try {
				fileOutputStream = new FileOutputStream(PeriCoachTestApplication.getFirmwareCheckFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			c.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (progress != null) {
						progress.setProgress(0);

					}
					if (percent != null)
						percent.setText("");

				}
			});
			/*
			 * Log.e(TAG, "onPreExecute"); init.setEnabled(false);
			 * get.setEnabled(false); write.setEnabled(false);
			 * erase.setEnabled(false); state =
			 * STATES.PCB_CONNECTED_POWER_ON_YES_INIT_WRITING; updateUI();
			 */
		}

		protected void onPostExecute(Void v) {

			if(isCancelled())return;
			
			c.runOnUiThread( new Runnable() {
				
				@Override
				public void run() {
					if (prog >= 100) {
						if(listener!=null)listener.onUploadCompleted(true);
						Toast.makeText(c, "WRITE COMPLETED",
								Toast.LENGTH_LONG).show();

						Handler h = new Handler();
						h.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (progress != null) {
									progress.setProgress(100);
									Resources res = c.getResources();
									Drawable background = res
											.getDrawable(R.drawable.greenprogress);
									progress.setProgressDrawable(background);
								}
								if (percent != null)
									percent.setText("PASS");

							}
						}, 2000);
					} else {

						if(listener!=null)listener.onUploadCompleted(false);

						Toast.makeText(c, "WRITE FAILED!",
								Toast.LENGTH_LONG).show();
						
						Handler h = new Handler();
						h.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (progress != null)
									progress.setProgress(0);
								if (percent != null)
									percent.setText("");

							}
						}, 2000);
					}

				}
			});
			if (isCancelled())
				return;
			try {
				Thread.sleep(2 * 1000 + 500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	public boolean getInfo() {
		if (isstopped)
			return false;
		emptyInputStream();
		if (!sendCommand(STM32_CMD_GET)) {
			System.out
					.println("Failed to send command to the device: reset your device.");
			return false;
		}
		if (isstopped)
			return false;
		int len = readWithTimeout(1000) + 1;
		System.out.printf("Have to read %d bytes.\n", len);
		if (isstopped)
			return false;
		readWithTimeout(1000);
		--len;
		// updateUiText("Bootloader Version - " + bl_version + ".\n");//TODO
		_CMDList.put("get", readWithTimeout(1000));
		--len;
		_CMDList.put("gvr", readWithTimeout(1000));
		--len;
		_CMDList.put("gid", readWithTimeout(1000));
		--len;
		_CMDList.put("rm", readWithTimeout(1000));
		--len;
		_CMDList.put("go", readWithTimeout(1000));
		--len;
		_CMDList.put("wm", readWithTimeout(1000));
		--len;
		_CMDList.put("er", readWithTimeout(1000));
		--len;
		_CMDList.put("wp", readWithTimeout(1000));
		--len;
		_CMDList.put("uw", readWithTimeout(1000));
		--len;
		_CMDList.put("rp", readWithTimeout(1000));
		--len;
		_CMDList.put("ur", readWithTimeout(1000));
		--len;
		if (len > 0) {
			System.out
					.println("Seems this bootloader returns more then we understand in the GET command, we will skip the unknown bytes\n");
			System.out.println("Please reset your device. Stopping.");
			return false;
		}
		if (readWithTimeout(1000) != STM32_ACK) {
			System.out.println("No ACK received from the device");
			System.out.printf("Next data: %d\n", readWithTimeout(1000));
			return false;
		}

		/* get the version and read protection status */
		if (!sendCommand(_CMDList.get("gvr").byteValue())) {
			System.out.println("No ACK received from the device");
			return false;
		}
		if (isstopped)
			return false;
		readWithTimeout(1000);
		if (isstopped)
			return false;
		readWithTimeout(1000);
		if (isstopped)
			return false;
		readWithTimeout(1000);
		if (isstopped)
			return false;
		if (readWithTimeout(1000) != STM32_ACK) {
			System.out.println("No ACK received from the device");
			return false;
		}
		if (isstopped)
			return false;
		/* get the device ID */
		if (!sendCommand(_CMDList.get("gid").byteValue())) {
			System.out.println("No ACK received from the device");
			return false;
		}
		if (isstopped)
			return false;
		len = readWithTimeout(1000) + 1;
		if (len != 2) {
			System.err
					.println("More then two bytes sent in the PID, unknown/unsupported device\n");
			return false;
		}
		if (isstopped)
			return false;
		pid = (readWithTimeout(1000) << 8) | readWithTimeout(1000);
		if (readWithTimeout(1000) != STM32_ACK) {
			System.err
					.println("More then two bytes sent in the PID, unknown/unsupported device\n");
			return false;
		}
		// updateUiText("Product ID - " + pid + ".\n");//TODO

		int[] aData = getDeviceSetup();
		fl_start = aData[3];
		// updateUiText("Flash Start Address - " + fl_start + ".\n");//TODO

		System.out.printf("Flash Start Address - %x.\n", fl_start);
		fl_end = aData[4];
		// updateUiText("Flash End Address - " + fl_end + ".\n");//TODO
		System.out.printf("Flash End Address - %x.\n", fl_end);
		mem_end = aData[8];

		return true;

	}

	private void emptyInputStream() {
		try {
			while (RX.available() > 0) {
				RX.read();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	private boolean sendCommand(byte iCmd) {
		if (isstopped)
			return false;
		System.out.printf("Sending command (%2x,%2x)\n", iCmd,
				(byte) (iCmd ^ XOR_BYTE));
		write(iCmd);
		write((byte) (iCmd ^ XOR_BYTE));
		byte c = (byte) readWithTimeout(1000);
		System.out.printf("Returned :%2x\n", c);

		if ((c & 0xFF) == STM32_ACK) {
			System.err.println("OK sending command to the device.");
			System.err.printf("Received : %2x\n", c);
			return true;
		} else {
			System.err.println("Error sending command to the device.");
			System.err.printf("Received : %2x\n", c);
			return false;
		}
	}

	private int readWithTimeout(int timeout) {
		// Read data with timeout
		if (isstopped)
			return -1;
		int readByte = -1;
		Callable<Integer> readTask = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Integer tmp = RX.read();
				System.out.printf("Call returned : %2x\n", tmp);
				return tmp;
			}
		};
		Future<Integer> future = executor.submit(readTask);
		try {
			readByte = future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return -1;
		}
		if (readByte >= 0)
			System.out.printf("Returned :%2x\n", readByte);

		return readByte;
	}

	public int[] getDeviceSetup() {

		for (int i = 0; i < devices.length; i++) {

			if (devices[i][0] == pid) {
				return devices[i];
			}

		}
		return null;

	}

	private boolean erase(byte pages) {
		if (!sendCommand(_CMDList.get("er").byteValue()))
			return false;
		if ((pages & 0xFF) == 0xFF) {
			ByteBuffer bb = ByteBuffer.allocate(3);
			bb.put((byte) 0xFF);
			bb.put((byte) 0xFE);
			bb.put((byte) 0x01);

			Log.d("ERASE: ", "ALL");

			if (_CMDList.get("er").byteValue() == 0x44) { // Handle the Extended
															// Erase Memory
															// Command (for
															// STM32L1)
				byte[] eraseall = bb.array();
				write(eraseall, 3);
				return (readWithTimeout(5000) & 0xFF) == STM32_ACK;
			}

		} else {
			byte cs = 0;
			byte pg_num;
			write(pages);
			cs ^= pages;
			for (pg_num = 0; pg_num <= pages; pg_num++) {
				write(pg_num);
				cs ^= pg_num;
			}
			write(cs);
			return (readWithTimeout(1000) & 0xFF) == STM32_ACK;
		}
		return false;
	}

	public boolean writeMemory(int address, byte[] data, int len) {

		byte cs;
		int i;
		int c, extra;
		Log.e(TAG, "len > 0 && len < 257 " + (len > 0 && len < 257));
		if (!(len > 0 && len < 257)) {
			throw new IllegalArgumentException("Data length invalid");
		}
		
		flush();

		/* must be 32bit aligned */
		Log.e(TAG, "address% 4 == 0 " + (address % 4 == 0));
		if (!(address % 4 == 0)) {
			throw new IllegalArgumentException("Address not 32bit aligned");
		}

		cs = (byte) stm32_gen_cs(address);

		/* send the address and checksum */
		if (!sendCommand(_CMDList.get("wm").byteValue())) {
			System.err.println("Unable to send write command \n");
			return false;
		}
		
		ByteBuffer bb = ByteBuffer.allocate(5);
		bb.put(addressToByteArray(address));
		bb.put(cs);							// Put the address into buffer
		byte[] addrbytes = bb.array();
		
		write(addrbytes, 5);
		
		if (readWithTimeout(1000) != STM32_ACK) {
			System.err.println("Unable to write adressing \n");
			return false;
		}
		// System.out.println("Address has been written \n");

		/* setup the cs and send the length */
		extra = len % 4;
		cs = (byte) (len - 1 + extra);
		
		bb = ByteBuffer.allocate(len + 2 + extra);
		bb.put(cs);							// Put the length into buffer
		
		/* write the data and build the checksum */
		for (i = 0; i < len; ++i)
			cs ^= data[i];
		Log.d("DATA: ", String.valueOf(cs));

		bb.put(data, 0, len);
		//bb.put(data);
		
		/* write the alignment padding */
		for (c = 0; c < extra; ++c) {
			bb.put((byte) 0xFF);
			cs ^= 0xFF;
		}

		/* send the checksum */
		bb.put(cs);
		
		byte[] bytes = bb.array();
		write(bytes, bytes.length);
		
		System.out.printf("Checksum : %2x\n", ((int) cs) & 0xFF);
		byte aRes = (byte) readWithTimeout(1000);
		System.out.printf("Result write : %2x\n", ((int) aRes) & 0xFF);
		return aRes == STM32_ACK;
	}
	
	public void flush() {
		try {
			TX.flush();
		} catch (IOException e) {
			System.err.println("Unable to flush the output stream");
		}
	}

	private void write(byte[] iData, int length) {
		for (int i = 0; i < length; i++) {
			System.out.printf("Sending Byte: %2x\n", iData[i]);	
		}
		
		try {
			TX.write(iData, 0, length);
		} catch (IOException e) {
			System.err.println("Unable to send char buffer to the device.");
		}
	}
	
	public byte[] addressToByteArray(int c) {
		byte[] aData = new byte[4];
		aData[0] = (byte) ((c >> 24) & 0x000000FF);
		aData[1] = (byte) ((c >> 16) & 0x000000FF);
		aData[2] = (byte) ((c >> 8) & 0x000000FF);
		aData[3] = (byte) ((c >> 0) & 0x000000FF);
		return aData;
	}

	public void upload(UploaderListener listener) {
		task = new WriteTask();

		this.listener=listener;
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void stop() {
		isstopped = true;
		if (task != null && !task.isCancelled())
			task.cancel(true);
	}

	
	public interface UploaderListener{
		
		public void onUploadCompleted(boolean b);
	}
}