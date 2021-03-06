package com.pietrantuono.uploadfirmware;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parser.BinaryParser;
import com.pietrantuono.activities.IOIOActivityListener;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.tests.implementations.upload.UploadDialog;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import ioio.lib.api.IOIO;

@SuppressWarnings("ucd")
public class DummyFirmWareUploader {
    private static final String TAG = "DummyFirmWareUploader";
    private final OutputStream TX;
    private static final byte XOR_BYTE = (byte) 0xFF;
    private static final byte STM32_ACK = 0x79;
    private final UploadDialog uploadDialog;
    private final Activity c;
    private final HashMap<String, Integer> _CMDList = new HashMap<String, Integer>();
    private int pid;
    private final int[][] devices = {
            {0x412, 0x20000200, 0x20002800, 0x08000000, 0x08008000, 4, 1024,
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800},
            {0x410, 0x20000200, 0x20005000, 0x08000000, 0x08020000, 4, 1024, // F1
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800},
            {0x414, 0x20000200, 0x20010000, 0x08000000, 0x08080000, 2, 2048,
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800},
            {0x418, 0x20001000, 0x20010000, 0x08000000, 0x08040000, 2, 2048,
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFB000, 0x1FFFF800},
            {0x420, 0x20000200, 0x20002000, 0x08000000, 0x08020000, 4, 1024,
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFF000, 0x1FFFF800},
            {0x430, 0x20000800, 0x20018000, 0x08000000, 0x08100000, 2, 2048,
                    0x1FFFF800, 0x1FFFF80F, 0x1FFFE000, 0x1FFFF800},
            {0x427, 0x20000000, 0x2000BFFF, 0x08000000, 0x0803FFFF, 16, 256, // L1
                    0x1FF80000, 0x1FF8001F, 0x1FFF0000, 0x1FF01FFF}, {0x0}};
    private int fl_start;
    private int fl_end;
    private Boolean isstopped = false;
    private WriteTask task = null;
    private UploaderListener listener;
    private final IOIO ioio_;
    private final Boolean loopback;

    private static ReadThread thread;


    private int stm32_gen_cs(int v) {
        return ((v & 0xFF000000) >> 24) ^ ((v & 0x00FF0000) >> 16)
                ^ ((v & 0x0000FF00) >> 8) ^ ((v & 0x000000FF) >> 0);
    }

    public DummyFirmWareUploader(OutputStream TX, InputStream RX, Activity c,
                                 IOIOActivityListener listner, IOIO ioio_, Boolean loopback, UploadDialog uploadDialog) {
        this.TX = TX;
        this.c = c;
        this.uploadDialog = uploadDialog;
        this.ioio_ = ioio_;
        this.loopback = loopback;
        isstopped = false;

        if (thread == null) {
            thread = new ReadThread(RX);
            Log.d(TAG, "Starting Reading Thread");
            thread.start();
        }
    }

    private void write(int c) {
        if (isstopped)
            return;
        try {
            TX.write(c);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private class WriteTask extends AsyncTask<Void, Void, Void> {
        private FileOutputStream fileOutputStream;
        private int prog = 0;

        protected Void doInBackground(Void... v) {
            showToast("doInBackground");

            if (isCancelled())
                return null;
            try {

                BinaryParser aParser = new BinaryParser();
                showToast(aParser.toString());

                int offset = 0;
                int size = aParser.getData().length;

                System.out.printf("Filesize : %x.\n", size);

                if (size > fl_end - fl_start) {
                    showToast("fl_end - fl_start");

                    System.err
                            .println("File provided larger then available flash space.\n");
                    return null;
                }

                //int npages = 0xFF;
//				if (!erase((byte) 0xFF))			// Erase all memory
//					return null;


                int addr = fl_start;
                int len;
                byte[] buffer = new byte[256];

                ByteArrayInputStream aInput = new ByteArrayInputStream(
                        aParser.getData());

                System.out.println();

                while (addr < fl_end && offset < size && !isCancelled()) {

//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}

                    int left = fl_end - addr;
                    len = buffer.length > left ? left : buffer.length;
                    len = len > (size - offset) ? size - offset : len;

                    aInput.read(buffer, 0, len);
                    if (isCancelled())
                        return null;
                    if (!writeMemory(addr, buffer, len)) {
                        showToast("Failed to write memory at address " + addr);
                        System.err.printf(
                                "Failed to write memory at address 0x%08x\n",
                                addr);
//						break;
                        return null;
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
            Log.d(TAG, "Upload progress update");

            uploadDialog.setProgress(prog);
        }

        protected void onPreExecute() {
            if (isCancelled())
                return;
            Log.d(TAG, "Upload pre execute");
            try {
                fileOutputStream = new FileOutputStream(PeriCoachTestApplication.getFirmwareCheckFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            uploadDialog.reset();
            /*
			 * Log.e(TAG, "onPreExecute"); init.setEnabled(false);
			 * get.setEnabled(false); write.setEnabled(false);
			 * erase.setEnabled(false); state =
			 * STATES.PCB_CONNECTED_POWER_ON_YES_INIT_WRITING; updateUI();
			 */
        }

        protected void onPostExecute(Void v) {

            if (isCancelled()) return;
            showToast("onPostExecute");
            c.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (prog >= 100) {
                        if (listener != null) listener.onUploadCompleted(true);
                        Toast.makeText(c, "WRITE COMPLETED",
                                Toast.LENGTH_LONG).show();

                        uploadDialog.setPass();

//						Handler h = new Handler();
//						h.postDelayed(new Runnable() {
//
//							@Override
//							public void run() {
//								if (progress != null) {
//									progress.setProgress(100);
//									Resources res = c.getResources();
//									Drawable background = res
//											.getDrawable(R.drawable.greenprogress);
//									progress.setProgressDrawable(background);
//								}
//								if (percent != null)
//									percent.setText("PASS");
//
//							}
//						}, 2000);
                    } else {
                        showToast("onPostExecute, prog < 100");

                        if (listener != null) listener.onUploadCompleted(false);


//						Toast.makeText(c, "WRITE FAILED!",
//								Toast.LENGTH_LONG).show();
                        uploadDialog.setFail("");


//						Handler h = new Handler();
//						h.postDelayed(new Runnable() {
//
//							@Override
//							public void run() {
//								if (progress != null)
//									progress.setProgress(0);
//								if (percent != null)
//									percent.setText("");
//
//							}
//						}, 2000);
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
        thread.emptyInputStream();
        _CMDList.put("wm", 0x31);
//		if (!sendCommand(STM32_CMD_GET)) {
//			System.out
//					.println("Failed to send command to the device: reset your device.");
//			return false;
//		}
//		if (isstopped)
//			return false;
//		int len = readWithTimeout(1000) + 1;
//		System.out.printf("Have to read %d bytes.\n", len);
//		if (isstopped)
//			return false;
//		readWithTimeout(1000);
//		--len;
//		// updateUiText("Bootloader Version - " + bl_version + ".\n");//TODO
//		_CMDList.put("get", readWithTimeout(1000));
//		--len;
//		_CMDList.put("gvr", readWithTimeout(1000));
//		--len;
//		_CMDList.put("gid", readWithTimeout(1000));
//		--len;
//		_CMDList.put("rm", readWithTimeout(1000));
//		--len;
//		_CMDList.put("go", readWithTimeout(1000));
//		--len;
//		_CMDList.put("wm", readWithTimeout(1000));
//		--len;
//		_CMDList.put("er", readWithTimeout(1000));
//		--len;
//		_CMDList.put("wp", readWithTimeout(1000));
//		--len;
//		_CMDList.put("uw", readWithTimeout(1000));
//		--len;
//		_CMDList.put("rp", readWithTimeout(1000));
//		--len;
//		_CMDList.put("ur", readWithTimeout(1000));
//		--len;
//		if (len > 0) {
//			System.out
//					.println("Seems this bootloader returns more then we understand in the GET command, we will skip the unknown bytes\n");
//			System.out.println("Please reset your device. Stopping.");
//			return false;
//		}
//		if (readWithTimeout(1000) != STM32_ACK) {
//			System.out.println("No ACK received from the device");
//			System.out.printf("Next data: %d\n", readWithTimeout(1000));
//			return false;
//		}
//
//		/* get the version and read protection status */
//		if (!sendCommand(_CMDList.get("gvr").byteValue())) {
//			System.out.println("No ACK received from the device");
//			return false;
//		}
//		if (isstopped)
//			return false;
//		readWithTimeout(1000);
//		if (isstopped)
//			return false;
//		readWithTimeout(1000);
//		if (isstopped)
//			return false;
//		readWithTimeout(1000);
//		if (isstopped)
//			return false;
//		if (readWithTimeout(1000) != STM32_ACK) {
//			System.out.println("No ACK received from the device");
//			return false;
//		}
//		if (isstopped)
//			return false;
//		/* get the device ID */
//		if (!sendCommand(_CMDList.get("gid").byteValue())) {
//			System.out.println("No ACK received from the device");
//			return false;
//		}
//		if (isstopped)
//			return false;
//		len = readWithTimeout(1000) + 1;
//		if (len != 2) {
//			System.err
//					.println("More then two bytes sent in the PID, unknown/unsupported device\n");
//			return false;
//		}
//		if (isstopped)
//			return false;
//		pid = (readWithTimeout(1000) << 8) | readWithTimeout(1000);
//		if (readWithTimeout(1000) != STM32_ACK) {
//			System.err
//					.println("More then two bytes sent in the PID, unknown/unsupported device\n");
//			return false;
//		}
        // updateUiText("Product ID - " + pid + ".\n");//TODO
        pid = 0x427;

        int[] aData = getDeviceSetup();
        fl_start = aData[3];
        // updateUiText("Flash Start Address - " + fl_start + ".\n");//TODO

        System.out.printf("Flash Start Address - %x.\n", fl_start);
        fl_end = aData[4];
        // updateUiText("Flash End Address - " + fl_end + ".\n");//TODO
        System.out.printf("Flash End Address - %x.\n", fl_end);

        return true;

    }

//	private void emptyInputStream() {
//		try {
//			while (RX.available() > 0) {
//				RX.read();
//			}
//		} catch (Exception e) {
//			Log.e(TAG, e.toString());
//			e.printStackTrace();
//		}
//	}

    private boolean sendCommand(byte iCmd) {
        if (isstopped)
            return false;
        System.out.printf("Sending command (%2x,%2x)\n", iCmd,
                (byte) (iCmd ^ XOR_BYTE));
        write(iCmd);
        write((byte) (iCmd ^ XOR_BYTE));
        byte c = (byte) thread.readByteWithTimeout(1000);
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


    private int[] getDeviceSetup() {

        for (int[] device : devices) {

            if (device[0] == pid) {
                return device;
            }

        }
        return null;

    }

    private void showToast(final String s) {
        c.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(c, s,
                        Toast.LENGTH_SHORT).show();
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean writeMemory(int address, byte[] data, int len) {

        byte cs;
        int i;
        int c, extra;
        Log.e(TAG, "len > 0 && len < 257 " + (len > 0 && len < 257));
        if (!(len > 0 && len < 257)) {
            showToast("Data length invalid");
            throw new IllegalArgumentException("Data length invalid");
        }

//		showToast("flushing");

//		flush();

		/* must be 32bit aligned */
        Log.e(TAG, "address% 4 == 0 " + (address % 4 == 0));
        if (!(address % 4 == 0)) {
            showToast("Address not 32bit aligned");
            throw new IllegalArgumentException("Address not 32bit aligned");
        }

        cs = (byte) stm32_gen_cs(address);

		/* send the address and checksum */
        if (!sendCommand(_CMDList.get("wm").byteValue())) {
            showToast("Unable to send write command");
            System.err.println("Unable to send write command \n");
            return false;
        }

        ByteBuffer bb = ByteBuffer.allocate(5);
        bb.put(addressToByteArray(address));
        bb.put(cs);                            // Put the address into buffer
        byte[] addrbytes = bb.array();

        write(addrbytes, 5);

        if (thread.readByteWithTimeout(1000) != STM32_ACK) {
            showToast("Unable to write addressing");
            System.err.println("Unable to write adressing \n");
            return false;
        }
        // System.out.println("Address has been written \n");

		/* setup the cs and send the length */
        extra = len % 4;
        cs = (byte) (len - 1 + extra);

        bb = ByteBuffer.allocate(len + 2 + extra);
        bb.put(cs);                            // Put the length into buffer
		
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
//		showToast("before, write(bytes, bytes.length);");

        write(bytes, bytes.length);
//		showToast("Write Thread is " + String.valueOf(Thread.currentThread().getId()));

//		showToast(String.valueOf(bytes.length) + " | " + String.valueOf(bytes[0]));

        IOIOUtils.getUtils().ioioSync(ioio_);
//		showToast("after, ioioSync");

        byte aRes;
        if (loopback) {
            // Read to ensure receive expected number of bytes
            aRes = (byte) thread.readBytesWithTimeout(1000);
        } else {
            aRes = (byte) thread.readByteWithTimeout(1000);
        }

        System.out.printf("Checksum : %2x\n", ((int) cs) & 0xFF);
        //byte aRes = (byte) readWithTimeout(2 * 1000);
//		byte aRes = (byte) readLoopWithTimeout(2 * 1000);

        System.out.printf("Result write : %2x\n", ((int) aRes) & 0xFF);
        return aRes == STM32_ACK;
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

    private byte[] addressToByteArray(int c) {
        byte[] aData = new byte[4];
        aData[0] = (byte) ((c >> 24) & 0x000000FF);
        aData[1] = (byte) ((c >> 16) & 0x000000FF);
        aData[2] = (byte) ((c >> 8) & 0x000000FF);
        aData[3] = (byte) ((c >> 0) & 0x000000FF);
        return aData;
    }

    public void upload(UploaderListener listener) {
        task = new WriteTask();

        this.listener = listener;
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stop() {
        isstopped = true;
        if (task != null && !task.isCancelled())
            task.cancel(true);
    }


    public interface UploaderListener {

        void onUploadCompleted(boolean b);
    }

    private class ReadThread extends Thread {

        @SuppressWarnings("unused")
        private final InputStream RX;


        public ReadThread(InputStream RX) {
            this.RX = RX;
        }

        @Override
        public void run() {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public int readByteWithTimeout(int timeout) {

            long now = System.currentTimeMillis();
            int avail = 1;
            while (System.currentTimeMillis() < (now + timeout)) {

//				try {
//					avail = this.RX.available();
//				} catch (IOException e) {
//					showToast(e.toString());
//					e.printStackTrace();
//				}
                if (avail >= 1) {
                    Integer tmp = -1;
                    return STM32_ACK;

//					try {
////						showToast("Read Thread is " + String.valueOf(this.getId()));
//						return this.RX.read();
//					} catch (IOException e) {
//						showToast(e.toString());
//						e.printStackTrace();
//					}
//					System.out.printf("Call returned : %2x\n", tmp);
                }

            }
            showToast("Read Timeout!");
            return -1;

        }

        public int readBytesWithTimeout(int timeout) {

            long now = System.currentTimeMillis();
            int avail = 1;
            int numbytes = 0;
            int res = -1;
            byte[] bytes = new byte[256];
            while (System.currentTimeMillis() < (now + timeout)) {

                try {
                    avail = this.RX.available();
                } catch (IOException e) {
                    showToast(e.toString());
                    e.printStackTrace();
                }
                if (avail >= 1) {
                    try {
                        numbytes = RX.read();
                        Log.d(TAG, "Number of bytes expected is " + numbytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        res = RX.read(bytes, 0, numbytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Number of bytes actual is " + res);

                    if (res == numbytes) {
                        return STM32_ACK;
                    }
//					Integer tmp = -1;
                    return STM32_ACK;
                }

            }
            showToast("Read Timeout!");
            return -1;

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
    }
}