package com.pietrantuono.uploadfirmware;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.parser.BinaryParser;
import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.fragments.sequence.holders.UploadItemHolder;
import com.pietrantuono.application.PeriCoachTestApplication;
import com.pietrantuono.ioioutils.IOIOUtils;
import com.pietrantuono.pericoach.newtestapp.BuildConfig;
import com.pietrantuono.tests.ErrorCodes;

import ioio.lib.api.IOIO;

@SuppressWarnings({"ucd", "unused"})
public class FirmWareUploader {
    private static final String TAG = "FirmWareUploader";
    private static  int ERRORCODE = ErrorCodes.NO_ERROR ;
    private final UploadItemHolder holder;
    private String error = "";
    private OutputStream TX;
    private InputStream RX;
    private static final byte STM32_CMD_INIT = 0x7F;
    private static final byte STM32_CMD_GET = 0x00;
    private static final byte XOR_BYTE = (byte) 0xFF;
    private static final byte STM32_ACK = 0x79;
    private static final byte STM32_NACK = 0x1F;
    private byte[] optionBytes = {(byte) 0xAA, 0x00, 0x55, (byte) 0xFF, (byte) 0xF8, 0x00, 0x07, (byte) 0xFF,
                                    0x00, 0x00, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF,
                                    0x00, 0x00, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF,
                                    0x00, 0x00, (byte) 0xFF, (byte) 0xFF, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF};

    private Activity activity;
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private HashMap<String, Integer> _CMDList = new HashMap<String, Integer>();
    private int pid;
    private int[][] devices = {
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
    private int op_start;
    private int op_end;

    @SuppressWarnings("ucd")
    public int mem_end;
    private Boolean isstopped = false;
    private WriteTask task = null;
    private UploaderListener listener;
    private IOIO ioio_;

    private int stm32_gen_cs(int v) {
        return ((v & 0xFF000000) >> 24) ^ ((v & 0x00FF0000) >> 16)
                ^ ((v & 0x0000FF00) >> 8) ^ ((v & 0x000000FF) >> 0);
    }

    private byte stm32_gen_data_cs(byte[] data, int len) {
        byte cs = 0;
        for (int i = 0; i < len; ++i)
            cs ^= data[i];
        return cs;
    }

    public FirmWareUploader(OutputStream TX, InputStream RX, Activity activity,
                            UploadItemHolder holder,
                            NewIOIOActivityListener listner, IOIO ioio_) {
        this.TX = TX;
        this.activity = activity;
        this.RX = RX;
        this.ioio_ = ioio_;
        this.holder = holder;
        isstopped = false;

    }

    public void write(int i) {
        if (isstopped)
            return;
        try {
            TX.write(i);
        } catch (Exception e) {
            showToast(String.valueOf(e));
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

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (readWithTimerTimeout(2000) == STM32_ACK) {
            return true;
        }
        ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_INIT_FAILED;
        return false;
    }

    private class WriteTask extends AsyncTask<Void, Void, Integer> {
        private FileOutputStream fileOutputStream;
        private int prog = 0;
        @Override
        protected Integer doInBackground(Void... v) {

            if (isCancelled())
                return null;
            try {

                BinaryParser aParser = new BinaryParser(activity);

                int offset = 0;
                int size = aParser.getData().length;

                System.out.printf("Filesize : %x.\n", size);

                if (size > fl_end - fl_start) {

                    System.err
                            .println("File provided larger then available flash space.\n");
//					error="File provided larger then available flash space.";
                    error = "File too big to flash: (" + String.valueOf(size) + " > " + String.valueOf(fl_end - fl_start) + ")";
                    return ErrorCodes.FIRMWAREUPLOAD_FILESIZE_ERROR;
                }

                int addr = fl_start;
                int len = 0;
                byte[] buffer = new byte[252];

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
                        error = "Failed to write memory at address " + String.format("0x%08x", addr);
                        return ErrorCodes.FIRMWAREUPLOAD_WRITE_MEMORY_ERROR;
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
        @Override
        protected void onProgressUpdate(Void... v) {
            if (isCancelled())
                return;
            Log.d(TAG, "Upload progress update");
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    holder.setProgress(prog);
                }
            });

        }
        @Override
        protected void onPreExecute() {
            if (isCancelled())
                return;
            Log.d(TAG, "Upload pre execute");
            try {
                fileOutputStream = new FileOutputStream(PeriCoachTestApplication.getFirmwareCheckFile());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   holder.setProgress(0);
                }
            });
        }
        @Override
        protected void onPostExecute(final Integer errorcode) {

            if (isCancelled()) return;
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (prog >= 100) {
                        if (listener != null) listener.onUploadSuccess();
                        holder.setPass();

                    } else {

                        if (listener != null) listener.onUploadFailure(error,errorcode!=null?errorcode:ErrorCodes.FIRMWAREUPLOAD_GENERIC_FAILURE);
                        holder.setFail("");
                    }
                }
            });

            if (isCancelled())
                return;
        }
    }

    public boolean getInfo() {
        String string;
        ERRORCODE=ErrorCodes.NO_ERROR;
        if (isstopped)
            return false;
        emptyInputStream();

        if (!sendCommand(STM32_CMD_GET)) {
            System.out
                    .println("Failed to send command to the device: reset your device.");
            error = "Failed to send command to the device: reset your device.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_SEND_ERROR;

            return false;
        }
        if (isstopped)
            return false;
        int len = readWithTimerTimeout(1000) + 1;

        System.out.printf("Have to read %d bytes.\n", len);
//        byte[] bytes = new byte [len];

        if (isstopped)
            return false;
        readWithTimerTimeout(1000);
        --len;
        // updateUiText("Bootloader Version - " + bl_version + ".\n");//TODO
        _CMDList.put("get", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("gvr", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("gid", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("rm", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("go", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("wm", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("er", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("wp", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("uw", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("rp", readWithTimerTimeout(1000));
        --len;
        _CMDList.put("ur", readWithTimerTimeout(1000));
        --len;
        if (len > 0) {
            System.out
                    .println("Seems this bootloader returns more then we understand in the GET command, we will skip the unknown bytes\n");
            System.out.println("Please reset your device. Stopping.");
            error = "get cmd: More bytes than known.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_DEVICE_INFO_ERROR;
            return false;
        }
        if (readWithTimerTimeout(2000) != STM32_ACK) {
            System.out.println("No ACK received from the device");
            System.out.printf("Next data: %d\n", readWithTimerTimeout(1000));
            error = "get cmd: No ACK received.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_NO_ACK_ERROR;
            return false;
        }

        if (!sendCommand(_CMDList.get("gvr").byteValue())) {
            System.out.println("No ACK received from the device");
            error = "gvr cmd: No ACK received.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_GVR_SEND_ERROR;
            return false;
        }
        if (isstopped)
            return false;
        readWithTimerTimeout(1000);
        if (isstopped)
            return false;
        readWithTimerTimeout(1000);
        if (isstopped)
            return false;
        readWithTimerTimeout(1000);
        if (isstopped)
            return false;
        if (readWithTimerTimeout(1000) != STM32_ACK) {
            System.out.println("No ACK received from the device");
            error = "gvr cmd: No ACK received.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_NO_ACK_ERROR;
           return false;
        }
        if (isstopped)
            return false;
		/* get the device ID */

        if (!sendCommand(_CMDList.get("gid").byteValue())) {
            System.out.println("No ACK received from the device");
            error = "gid cmd: No ACK received.";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_GID_SEND_ERROR;
            return false;
        }
        if (isstopped)
            return false;
        len = readWithTimerTimeout(1000) + 1;
        if (len != 2) {
            System.err
                    .println("More then two bytes sent in the PID, unknown/unsupported device\n");
            error = "gid cmd: Unsupported device (> 2 bytes).";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_ADDR_ALIGN_ERROR;
            return false;
        }
        if (isstopped)
            return false;
        pid = (readWithTimerTimeout(1000) << 8) | readWithTimerTimeout(1000);
        if (readWithTimerTimeout(1000) != STM32_ACK) {
            System.err
                    .println("More then two bytes sent in the PID, unknown/unsupported device\n");
            error = "PID: Unsupported device (> 2 bytes).";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_ADDR_ALIGN_ERROR;
            return false;
        }

        int[] aData = getDeviceSetup();
        if (aData == null) {
            error = "ERROR: Device info error";
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_GET_INFO_FAILED;
            return false;
        }
        fl_start = aData[3];

        System.out.printf("Flash Start Address - %x.\n", fl_start);
        fl_end = aData[4];
        System.out.printf("Flash End Address - %x.\n", fl_end);
        mem_end = aData[8];
        op_start = aData[7];
        op_end = aData[8];
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

        write(iCmd);
        write((byte) (iCmd ^ XOR_BYTE));
        byte b = (byte) readWithTimerTimeout(1000);

        if ((b & 0xFF) == STM32_ACK) {
            if (BuildConfig.DEBUG) {
                System.err.println("OK sending command to the device.");
                System.err.printf("Recv: %2x\n", b);
            }
            return true;
        } else {
            if(BuildConfig.DEBUG) {
                System.err.println("Error sending command to the device.");
                System.err.printf("Recv: %2x\n", b);
            }
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_SEND_ERROR;
            return false;
        }
    }


    private int readWithTimerTimeout(int timeout) {
        if (isstopped)
            return -1;

        final Thread readThread = Thread.currentThread();
        Timer t = new Timer();
        TimerTask readTask = new TimerTask() {
            @Override
            public void run() {
                System.out.printf("Timer expired, interrupt\n");
                readThread.interrupt();

            }
        };
//		Log.d(TAG, "Schedule readTask timer for " + String.valueOf(timeout) + " ms");
        t.schedule(readTask, timeout);

        Integer readByte = -1;
        try {
            readByte = RX.read();
            if (readByte >= 0) {
//				System.out.printf("Read Call Returned : %2x\n", readByte);
                t.cancel();
                t.purge();
                t = null;
//				System.out.printf("Timer Cancelled\n");
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            return readByte;
        }
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

    private boolean readOptionBytes() {

        return true;
    }

    public boolean writeOptionBytes() {

        System.out.printf("Writing Option Bytes");

            if (!writeMemory(op_start, optionBytes, optionBytes.length)) {
                if(BuildConfig.DEBUG) {
                    System.err.printf(
                            "Failed to write memory at address 0x%08x\n",
                            op_start);
                }
                error = "Failed to write memory at address " + String.format("0x%08x", op_start);
                ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_OPTIONBYTES_WRITE_ERROR;
                return false;
            }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!deviceInit()) {
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_INIT_FAILED;
            return false;
        }
        return true;
    }

    public boolean massErase() {
        //TO MASS ERASE
        //Send readout protect, wait for 2 acks, need to re-init afterward due to system reset
        //Send readout unprotect, wait for 2 acks
        //SendCommand includes one ACK
        // Must reset and re-init device afterwards

        if (sendCommand(_CMDList.get("rp").byteValue())){
            if(readWithTimerTimeout(1000)==STM32_NACK){
                // Already in protect mode, no device reset so do nothing
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Device just entered protect mode and system resetted, need to re-init
                if (!deviceInit()) return false;
            }
        } else {
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_RP_SEND_ERROR;
        }

        if (!sendCommand(_CMDList.get("ur").byteValue())) {
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_CMD_UR_SEND_ERROR;
            return false;
        }
        if(readWithTimerTimeout(1000)==STM32_NACK){
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_NO_ACK_ERROR;
            return false;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!deviceInit()) {
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_INIT_FAILED;
            return false;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean erase(byte pages) {
        // Erase - 0x43, Extended Erase - 0x44
        System.out.printf("Erasing flash");

        if (!sendCommand(_CMDList.get("er").byteValue()))
            return false;
        if ((pages & 0xFF) == 0xFF) {
            ByteBuffer bb = ByteBuffer.allocate(3);
            bb.put((byte) 0xFF);
            bb.put((byte) 0xFF);
            bb.put((byte) 0x00);

            Log.d("ERASE: ", "ALL");

            if (_CMDList.get("er").byteValue() == 0x44) { // Handle the Extended
                // Erase Memory
                // Command (for
                // STM32L1)
                byte[] eraseall = bb.array();
                for (int i = 0; i < eraseall.length; i++) {
                    System.out.printf("%2x\n", eraseall[i]);
                }
                write(eraseall, 3);
                return (readWithTimerTimeout(10000) & 0xFF) == STM32_ACK;
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
            return (readWithTimerTimeout(10000) & 0xFF) == STM32_ACK;
        }
        return false;
    }

    private void showToast(final String s) {
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(activity, s,
                        Toast.LENGTH_SHORT).show();
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean writeMemory(int address, byte[] data, int len) {

        byte cs;
        int i;
        int j, extra;
        Log.e(TAG, "len > 0 && len < 257 " + (len > 0 && len < 257));
        if (!(len > 0 && len < 257)) {
            showToast("Data length invalid");
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_WRITE_MEMORY_ERROR;
            return false;
            }

        flush();

		/* must be 32bit aligned */
        Log.e(TAG, "address% 4 == 0 " + (address % 4 == 0));
        if (!(address % 4 == 0)) {
            showToast("Address not 32bit aligned");
            ERRORCODE = ErrorCodes.FIRMWAREUPLOAD_ADDR_ALIGN_ERROR;
            return false;
        }


        cs = (byte) stm32_gen_cs(address);

		/* send the address and checksum */
        if (!sendCommand(_CMDList.get("wm").byteValue())) {
            showToast("Unable to send write command");
            System.err.println("Unable to send write command \n");
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_WRITE_MEMORY_ERROR;
            return false;
        }

        ByteBuffer bb = ByteBuffer.allocate(5);
        write(address, 4);
//		bb.put(addressToByteArray(address));
        write(cs);
//		bb.put(cs);							// Put the address into buffer
//		byte[] addrbytes = bb.array();

//		write(addrbytes, 5);

        if (readWithTimerTimeout(1000) != STM32_ACK) {
            showToast("Unable to write addressing");
            System.err.println("Unable to write adressing \n");
            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_WRITE_MEMORY_ERROR;
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
        for (j = 0; j < extra; ++j) {
            bb.put((byte) 0xFF);
            cs ^= 0xFF;
        }

		/* send the checksum */
        bb.put(cs);

        byte[] bytes = bb.array();
        write(bytes, bytes.length);

//        IOIOUtils.getUtils().ioioSync(ioio_);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Checksum : %2x\n", ((int) cs) & 0xFF);
        //byte aRes = (byte) readWithTimeout(2 * 1000);
        byte aRes = (byte) readWithTimerTimeout(2000);

        System.out.printf("Result write : %2x\n", ((int) aRes) & 0xFF);
        return aRes == STM32_ACK;
    }

//    public boolean writeMemoryBasic(int address, byte[] data, int len) {
//
//        byte cs;
//        int i;
//        int j, extra;
//
//        cs = (byte) stm32_gen_cs(address);
//
//		/* send the address and checksum */
//        if (!sendCommand(_CMDList.get("wm").byteValue())) {
//            showToast("Unable to send write command");
//            System.err.println("Unable to send write command \n");
//            ERRORCODE=ErrorCodes.FIRMWAREUPLOAD_WRITE_MEMORY_ERROR;
//            return false;
//        }
//
//        write(address, 4);
//        write(cs);
//
//        if (readWithTimerTimeout(1000) != STM32_ACK)
//            return false;
//
//        // System.out.println("Address has been written \n");
//
//		/* setup the cs and send the length */
//        extra = len % 4;
//        write((byte) (len - 1 + extra));        // Write length byte
//        write(data, data.length);                //	Write data
//
//		/* write the data and build the checksum */
//        for (i = 0; i < len; ++i)
//            cs ^= data[i];
////		Log.d("DATA: ", String.valueOf(cs));
//
//		/* write the alignment padding */
//        for (j = 0; j < extra; ++j) {
//            write(0xFF);
//            cs ^= 0xFF;
//        }
//
//		/* send the checksum */
//        write(cs);    // Write cs
//        System.out.printf("Checksum : %2x\n", ((int) cs) & 0xFF);
//
//        byte aRes = (byte) readWithTimerTimeout(1000);
//
//        System.out.printf("Result write : %2x\n", ((int) aRes) & 0xFF);
//        return aRes == STM32_ACK;
//    }

    public void flush() {
        try {
            TX.flush();
        } catch (IOException e) {
            System.err.println("Unable to flush the output stream");
        }
    }

    private void write(byte[] iData, int length) {
//		for (int i = 0; i < length; i++) {
//			System.out.printf("Sending Byte: %2x\n", iData[i]);
//		}

        try {
            TX.write(iData, 0, length);
        } catch (IOException e) {
            showToast(String.valueOf(e));
            System.err.println("Unable to send char buffer to the device.");
        }
    }

    private void write(int i, int length) {

        if (length == 4) {

            byte[] aData = new byte[4];
            aData[0] = (byte) ((i >> 24) & 0x000000FF);
            aData[1] = (byte) ((i >> 16) & 0x000000FF);
            aData[2] = (byte) ((i >> 8) & 0x000000FF);
            aData[3] = (byte) ((i >> 0) & 0x000000FF);

            write(aData, 4);
        }
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

        public void onUploadSuccess();

        public void onUploadFailure(String error, int errorcode);
    }


    public static int getERRORCODE() {
        return ERRORCODE;
    }
}