package com.pietrantuono.ioioutils;

import customclasses.DebugHelper;
import hugo.weaving.DebugLog;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pietrantuono.activities.NewIOIOActivityListener;
import com.pietrantuono.fragments.SerialConsoleFragmentCallback;
import com.pietrantuono.application.PeriCoachTestApplication;

public class IOIOUtils implements IOIOUtilsInterface {
    private Uart uart1;
    private Uart uart2;

    private static BufferedReader RD1;
    private static DigitalInput barcodeOK;
    private static DigitalOutput barcodeTRGR;
    private static final String TAG = "IOIOUtils";
    private static DigitalOutput boot0;
    private static DigitalOutput boot1;
    private static DigitalOutput POWER;
    private static DigitalOutput trigger;
    private static DigitalOutput reset;
    private static DigitalOutput Irange;
    private static DigitalOutput _5V_DC;
    private static DigitalOutput Sensor_Low;
    private static DigitalOutput Sensor_High;
    private static DigitalOutput HallInt;
    private static DigitalOutput EMag;

    private static DigitalInput CHGPinIn;
    private static DigitalOutput CHGPinOut;

    private static TwiMaster master = null;
    private static SpiMaster spiMaster = null;
    private static PwmOutput servo_pwm = null;
    private static PwmOutput airReg_pwm = null;

    private static Boolean isinterrupted = false;
    private static Boolean stopthread = false;
    private static Boolean triggervalue = true;

    private static InputStream iS;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static byte[] log = new byte[0];
    private static Uart2Thread thread;
    private static IOIOUtilsInterface instance;
    private static SerialConsoleFragmentCallback callback;

    private static Mode uutMode;

    public static IOIOUtilsInterface getUtils() {
        if (instance == null) {
            instance = new IOIOUtils();
        }
        return instance;
    }

    @SuppressWarnings("unused")
    public enum Mode {

        bootloader((int) 0),
        application((int) 1);

        public final Integer mode;

        Mode(int mode) {
            this.mode = mode;
        }
    }

    @SuppressWarnings("unused")
    public enum Pin {

        Sensor_High((int) 1),
        Sensor_Low((int) 2),
        twiMaster((int) 2),     // Not pin number, I2C peripheral number (pins25, 26)
        EMag((int) 3),
        spiMaster_miso((int) 4),
        spiMaster_mosi((int) 5),
        uart1_rx((int) 6),
        uart1_tx((int) 7),
        Irange((int) 9),
        spiMaster_clk((int) 10),
        uart2_rx((int) 13),
        uart2_tx((int) 14),
        barcodeOk((int) 15),
        barcodeTrigger((int) 17),
        _5V_DC((int) 18),
        POWER((int) 19),
        reset((int) 20),
        HallInt((int) 21),
        boot1((int) 23),
        boot0((int) 24),
        i2c_scl((int) 25),
        i2c_sda((int) 25),
        ChgPin((int) 27),
        spiMaster_ss((int) 28),
        servo_pwm((int) 30),
        airReg_pwm((int) 31),
        debugTrigger((int) 45);

        public final Integer value;

        public int getValue() {
            return value;
        }

        Pin(int value) {
            this.value = value;
        }
    }


    private void makeToast(final Activity ac, final String s) {
        ac.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ac, s, Toast.LENGTH_LONG).show();

            }
        });
    }

    private void report(Exception e, Activity ac) {
        try {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            Crashlytics.logException(e);
            makeToast(ac, e.toString());
        } catch (Exception e1) {
        }
    }


    @Override
    public String readBarcode(Activity activity) {
        if (isinterrupted) return "";
        String tmp = "";

        try {
            barcodeTRGR.write(false);

        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, e1.toString());
            Crashlytics.logException(e1);
        }

        Log.d("BARCODE: ", "Read Triggered...");

        try {
            barcodeTRGR.write(true);
        } catch (Exception e1) {
            e1.printStackTrace();
            Log.e(TAG, e1.toString());
            Crashlytics.logException(e1);
        }

        if (readPulseWithTimeout(barcodeOK) == 1) {

            Log.d("BARCODE: ", "Valid Read");

            try {
                tmp = RD1.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.toString());
                Crashlytics.logException(e);

            }

            Log.d("Barcode: ", tmp!=null?tmp:"null!");
            // updateUiText("Barcode - " + tmp + "\n");
            // gotBarcode = true;

            return tmp;
        }
        return tmp;
    }

    public int readPulseWithTimeout(final DigitalInput input) {
        if (isinterrupted) return -1;
        // Read barcodeOK pulse with timeout

        int timeout = 1000;

        final Thread readThread = Thread.currentThread();
        Timer t = new Timer();
        TimerTask readTask = new TimerTask() {
            @Override
            public void run() {
                readThread.interrupt();
                System.out.printf("Timer expired, interrupt");
            }
        };
        Log.d(TAG, "Schedule readTask timer for " + String.valueOf(timeout) + " ms");
        t.schedule(readTask, timeout);

        try {
            input.waitForValue(false);
            t.cancel();
            t.purge();
            t=null;
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            return -1;
        }
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#closeall(com.pietrantuono.activities.NewIOIOActivityListener, android.app.Activity)
     */
    @Override
    public void closeall(final NewIOIOActivityListener listener,
                         final Activity ac) {

        stopUartThread();
//        ac.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(ac, "CLOSING ALL IOIO",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        try {
            if (POWER != null) POWER.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (reset != null) reset.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (boot0 != null) boot0.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (boot1 != null) boot1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (_5V_DC != null) _5V_DC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Irange != null) Irange.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Sensor_Low != null) Sensor_Low.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (Sensor_High != null) Sensor_High.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (master != null) master.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (spiMaster != null) spiMaster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (HallInt != null) HallInt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (EMag != null) EMag.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (CHGPinIn != null) CHGPinIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (CHGPinOut != null) CHGPinOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (trigger != null) trigger.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (uart1 != null) uart1.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (uart2 != null) uart2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (servo_pwm != null) {
                servo_pwm.close();
                servo_pwm = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (airReg_pwm != null) airReg_pwm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#initialize(com.pietrantuono.activities.NewIOIOActivityListener, ioio.lib.api.IOIO, android.app.Activity)
     */
    @Override
    public void initialize(final NewIOIOActivityListener listner,
                           final IOIO ioio_, final Activity ac) {
        isinterrupted = false;
        boolean stateBoot0 = true;
        boolean stateBoot1 = false;
        boolean range = false;
        boolean stateEmag = true;
        DigitalOutput.Spec.Mode eMagMode = DigitalOutput.Spec.Mode.OPEN_DRAIN;

        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() != 1) {
            stateBoot0 = false;
            stateBoot1 = true;
            stateEmag = false;
            eMagMode = DigitalOutput.Spec.Mode.NORMAL;
            range = true;
            uutMode = Mode.application;

        } else {
            uutMode = Mode.bootloader;
        }

        // Only open the required ioio pins for PCB fixture test.
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {

            try {
                POWER = ioio_.openDigitalOutput(Pin.POWER.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN, false);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                reset = ioio_.openDigitalOutput(Pin.reset.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN,
                        true);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                boot0 = ioio_.openDigitalOutput(Pin.boot0.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN,
                        stateBoot0);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                boot1 = ioio_.openDigitalOutput(Pin.boot1.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN,
                        stateBoot1);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                Irange = ioio_.openDigitalOutput(Pin.Irange.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN, range);

            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                Sensor_Low = ioio_.openDigitalOutput(Pin.Sensor_Low.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                Sensor_High = ioio_.openDigitalOutput(Pin.Sensor_High.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
            } catch (Exception e) {
                report(e, ac);
                return;
            }

            try {
                HallInt = ioio_.openDigitalOutput(Pin.HallInt.getValue(),
                        DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
            } catch (Exception e) {
                report(e, ac);
                return;
            }
        }

        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() != 1) {

            setServo(ioio_, 500);

            try {
                airReg_pwm = ioio_.openPwmOutput(Pin.airReg_pwm.getValue(), 200);
                airReg_pwm.setDutyCycle((float) 0.5);
            } catch (Exception e) {
                report(e, ac);
                return;
            }
        }

        try {
            _5V_DC = ioio_.openDigitalOutput(Pin._5V_DC.getValue(),
                    DigitalOutput.Spec.Mode.OPEN_DRAIN, true);
        } catch (Exception e) {
            report(e, ac);
            return;
        }

        //Irange (uA Range) = true, (mA Range) = false.



        try {
            EMag = ioio_.openDigitalOutput(Pin.EMag.getValue(),
                    eMagMode, stateEmag);
        } catch (Exception e) {
            report(e, ac);
            return;
        }

        try {
            master = ioio_.openTwiMaster(Pin.twiMaster.getValue(),
                    TwiMaster.Rate.RATE_100KHz, false);
        } catch (ConnectionLostException e1) {
            report(e1, ac);
            return;
        }

        try {
            spiMaster = ioio_.openSpiMaster(Pin.spiMaster_miso.getValue(),
                    Pin.spiMaster_mosi.getValue(),
                    Pin.spiMaster_clk.getValue(),
                    Pin.spiMaster_ss.getValue(),
                    SpiMaster.Rate.RATE_1M);
        } catch (ConnectionLostException e2) {
            report(e2, ac);
            return;
        }

        if(!DebugHelper.isMaurizioDebug() && (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1))setBattVoltage(ioio_, true, 34, 2f, 3.9f);

        try {
            trigger = ioio_.openDigitalOutput(Pin.debugTrigger.getValue(),
                    DigitalOutput.Spec.Mode.NORMAL, true);
        } catch (Exception e) {
            report(e, ac);
            return;
        }

        try {
            CHGPinIn = ioio_.openDigitalInput(Pin.ChgPin.getValue(),
                    DigitalInput.Spec.Mode.FLOATING);
        } catch (Exception e) {
            report(e, ac);
            return;
        }

        try {
            uart1 = ioio_.openUart(Pin.uart1_rx.getValue(),
                    Pin.uart1_tx.getValue(),
                    115200, Uart.Parity.NONE, Uart.StopBits.ONE);
        } catch (ConnectionLostException e) {
            Log.e(TAG, e.toString());
        }

        iS = uart1.getInputStream();

        InputStreamReader in1 = new InputStreamReader(iS);

        RD1 = new BufferedReader(in1);

        try {
            barcodeOK = ioio_.openDigitalInput(Pin.barcodeOk.getValue(),
                    DigitalInput.Spec.Mode.PULL_UP);
        } catch (Exception e) {

        }

        try {
            barcodeTRGR = ioio_.openDigitalOutput(Pin.barcodeTrigger.getValue(),
                    DigitalOutput.Spec.Mode.NORMAL,

                    true);
        } catch (Exception e) {

        }

        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            try {
                uart2 = ioio_.openUart(
                        new DigitalInput.Spec(Pin.uart2_rx.getValue(), DigitalInput.Spec.Mode.PULL_UP),
                        new DigitalOutput.Spec(Pin.uart2_tx.getValue(), DigitalOutput.Spec.Mode.OPEN_DRAIN),
                        115200, Uart.Parity.EVEN, // STM32
                        // Bootloader
                        // requires
                        // EVEN
                        // Parity..
                        Uart.StopBits.ONE);
            } catch (ConnectionLostException e) {
                Log.e(TAG, e.toString());
            }

            inputStream = uart2.getInputStream();
            outputStream = uart2.getOutputStream();
        }

        toggle5VDC(ac);

        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resetDevice(ac);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void openUart2(final IOIO ioio_) {
        try {
            uart2 = ioio_.openUart(
//                    13,
                    new DigitalInput.Spec(Pin.uart2_rx.getValue(), DigitalInput.Spec.Mode.PULL_UP),
//                    14,
                    new DigitalOutput.Spec(Pin.uart2_tx.getValue(), DigitalOutput.Spec.Mode.OPEN_DRAIN),
                    115200, Uart.Parity.EVEN, // STM32
                    // Bootloader
                    // requires
                    // EVEN
                    // Parity..
                    Uart.StopBits.ONE);
        } catch (ConnectionLostException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void openUart2(final IOIO ioio_,
                          int baud, Uart.Parity parity, Uart.StopBits stop) {
        try {
            uart2 = ioio_.openUart(
//                    13,
                    new DigitalInput.Spec(Pin.uart2_rx.getValue(), DigitalInput.Spec.Mode.PULL_UP),
//                    14,
                    new DigitalOutput.Spec(Pin.uart2_tx.getValue(), DigitalOutput.Spec.Mode.OPEN_DRAIN),
                    baud, parity, // STM32
                    // Bootloader
                    // requires
                    // EVEN
                    // Parity..
                    stop);
        } catch (ConnectionLostException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void ioioSync(final IOIO ioio_) {
        try {
            ioio_.sync();
            Log.d(TAG, "IOIO Syncing");
        } catch (ConnectionLostException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setDAC(int DAC){
        byte[] writebyte = new byte[] { 0x00, (byte) DAC };
        byte[] readbyte = new byte[] {};
        try {
            getMaster().writeRead(0x60, false, writebyte, writebyte.length,
                    readbyte, readbyte.length);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private static byte[] int_to_bb_be(int value) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
    }

    @Override
    public void setServo(final IOIO ioio_, int value) {
        if (servo_pwm == null) {
            try {
                servo_pwm = ioio_.openPwmOutput(Pin.servo_pwm.getValue(), 150);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
        }

        try {
            getServo_pwm().setPulseWidth(value);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

//        try {
//            if (servo_pwm != null) {
//                servo_pwm.close();
//                servo_pwm = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void set420(int value) {
        final int multiplier = 1000;
        final float minCurrent = 4 * multiplier;
        final float maxCurrent = 20 * multiplier;
        final float minScaledCurrent = 800;
        final float maxScaledCurrent = 4095;

        float setting = ((value - minCurrent) / (maxCurrent - minCurrent) * (maxScaledCurrent - minScaledCurrent) + minScaledCurrent);
        byte[] writebyte = int_to_bb_be((int)setting);
        try {
            getSpiMaster().writeRead(0, writebyte, 2, 2, null, 0);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean setBattVoltage(final IOIO ioio_, boolean calibrate, int pin, float scaling, final float voltage) {
        boolean reached = false;
        boolean adjusting = true;
        float measured = 0;
        int DAC = 0;
        int stepsize = 1;
        float min=0;
        float max=0;
        float precision = 0.001f;

        if (PeriCoachTestApplication.getGradient() == 0) {
            setDAC(DAC);      // Set and measure max voltage to work out gradient
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                max = (Voltage.getVoltage(ioio_, pin, 20, 1) * scaling);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PeriCoachTestApplication.setMaxBatteryVoltage(max);
            Log.d(TAG, "DAC " + DAC + " | Max Voltage " + max);


            DAC = 255;
            setDAC(DAC);    // Set and measure min voltage to work out gradient
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                min = (Voltage.getVoltage(ioio_, pin, 20, 1) * scaling);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PeriCoachTestApplication.setMinBatteryVoltage(min);
            Log.d(TAG, "DAC " + DAC + " | Min Voltage " + min);

            PeriCoachTestApplication.setGradient((max-min) / 256);
//            grad = (max - min) / 256;
            Log.d(TAG, "Grad " + PeriCoachTestApplication.getGradient());
        }
        //Use linear equation based LT1671 and MCP4706 DAC, to set DAC output based on voltage setpoint
        DAC = (int) ((voltage - PeriCoachTestApplication.getMaxBatteryVoltage()) / -(PeriCoachTestApplication.getGradient()));
        Log.d(TAG, "Max - " + PeriCoachTestApplication.getMaxBatteryVoltage() + " | Grad - " + PeriCoachTestApplication.getGradient());
        Log.d(TAG, "Corresponding DAC for voltage: " + voltage + " is " + DAC);
        setDAC(DAC);

        try {
            measured = (Voltage.getVoltage(ioio_, pin, 10, 0) * scaling);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while(adjusting) {
//            error = Math.abs(measured-voltage);
            if (measured < (voltage - (voltage * precision))) {
                // Increase set voltage, or decrease DAC setting
                DAC = DAC - stepsize;
            }
            if (measured > (voltage + (voltage * precision))){
                // Increase set voltage, or decrease DAC setting
                DAC = DAC + stepsize;
            }
            setDAC(DAC);

            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }

            try {
                measured = (Voltage.getVoltage(ioio_, pin, 10, 0) * scaling);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (measured < (voltage + (voltage * precision)) && measured > (voltage - (voltage * precision))){
                Log.d(TAG, "Voltage Setpoint reached");
                reached = true;
                adjusting = false;
            }
            System.out.println("Target " + voltage + " | DAC set to " + DAC + " | Bat V is " + measured + "V");

            // If we hit the DAC limits, stop adjusting.
            if (DAC == min || DAC == max) {
                adjusting = false;
            }
        }

        return reached;
    }

    @Override
    public DigitalOutput getDigitalOutput(int pinNumber) {
        switch (pinNumber) {
            case 1:
                return Sensor_High;
//			break;
            case 2:
                return Sensor_Low;
//			break;
            case 3:
                return EMag;
//			break;
            case 9:
                return Irange;
//			break;
            case 17:
                return barcodeTRGR;
//			break;
            case 18:
                return _5V_DC;
//			break;
            case 19:
                return POWER;
//			break;
            case 20:
                return reset;
//			break;
            case 21:
                return HallInt;
//			break;
            case 23:
                return boot1;
//			break;
            case 24:
                return boot0;
//			break;
        }
        return null;
    }


//	@Override
//	public Mode getUutMode() {return uutMode;}

    @Override
    public void driveChargeLed(final IOIO ioio_, final Activity ac) {
        if (CHGPinIn != null) {
            CHGPinIn.close();
        }

        try {
            CHGPinOut = ioio_.openDigitalOutput(27, DigitalOutput.Spec.Mode.NORMAL, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void resetUart2(final IOIO ioio_, final Activity ac) {
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        uart2.close();

        try {
            uart2 = ioio_.openUart(13, 14, 115200, Uart.Parity.EVEN, // STM32
                    // Bootloader
                    // requires
                    // EVEN
                    // Parity..
                    Uart.StopBits.ONE);
        } catch (ConnectionLostException e) {
            Log.e(TAG, e.toString());
        }

        inputStream = uart2.getInputStream();
        outputStream = uart2.getOutputStream();


    }

    public DigitalOutput getBarcodeTrgr() {
        return barcodeTRGR;
    }

    public DigitalInput getBarcodeOK() {
        return barcodeOK;
    }

    /* (non-Javadoc)
    * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getCHGPin()
    */
    @Override
    public DigitalInput getCHGPinIn() {
        return CHGPinIn;
    }

    /* (non-Javadoc)
    * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getCHGPin()
    */
    @Override
    public DigitalOutput getCHGPinOut() {
        return CHGPinOut;
    }

    public DigitalOutput getTrigger() {
        return trigger;
    }

    public DigitalOutput getSensor_Low() {
        return Sensor_Low;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getSensor_High()
     */
    @Override
    public DigitalOutput getSensor_High() {
        return Sensor_High;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#get_5V_DC()
     */
    @Override
    public DigitalOutput get_5V_DC() {
        return _5V_DC;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getMaster()
     */
    @Override
    public TwiMaster getMaster() {
        return master;
    }

    /* (non-Javadoc)
    * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getSpiMaster()
    */
    @Override
    public SpiMaster getSpiMaster() {
        return spiMaster;
    }

    /* (non-Javadoc)
    * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getServo_pwm()
    */
    @Override
    public PwmOutput getServo_pwm() {
        return servo_pwm;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getPOWER()
     */
    @Override
    public DigitalOutput getPOWER() {
        return POWER;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getReset()
     */
    @Override
    public DigitalOutput getReset() {
        return reset;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getHallInt()
     */
    @Override
    public DigitalOutput getHallInt() {
        return HallInt;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getEMag()
     */
    @Override
    public DigitalOutput getEmag() {
        return EMag;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#resetDevice(android.app.Activity)
     */
    @Override
    public void resetDevice(Activity activity) {
        if (isinterrupted) return;
        Log.d(TAG, "Resetting UUT");

        try {
            reset.write(false);
        } catch (Exception e) {
            report(e, activity);
            return;
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e1) {
            report(e1, activity);
            return;
        }
        try {
            reset.write(true);
        } catch (Exception e1) {
            report(e1, activity);
            return;
        }

    }

    @SuppressWarnings("ucd")
    public void toggleHall(Activity activity) {
        if (isinterrupted) return;
        try {
            HallInt.write(false);
        } catch (Exception e) {
            report(e, activity);
            return;
        }
        try {
            Thread.sleep(2000);
        } catch (Exception e1) {
            report(e1, activity);
            return;
        }
        try {
            HallInt.write(true);
        } catch (Exception e1) {
            report(e1, activity);
            return;
        }

    }

    public void toggle5VDC(Activity activity) {
        if (isinterrupted) return;
        Log.d(TAG, "Toggling 5V DC Charging Power");
        try {
            get_5V_DC().write(false);
        } catch (ConnectionLostException e) {
            report(e, activity);
        }
        try {
            Thread.sleep(3000); // Note, 3secs to ensure device goes back to sleep
        } catch (InterruptedException e) {
            report(e, activity);
        }
        try {
            get_5V_DC().write(true);
        } catch (ConnectionLostException e) {
            report(e, activity);
        }
    }

    @Override
    public void setEMag(Activity activity, Boolean state) {
        if (isinterrupted) return;
        Log.d(TAG, "Setting Electromagnet" + state);
        try {
            getEmag().write(state);
        } catch (ConnectionLostException e) {
            report(e, activity);
        }
    }

    @Override
    public void toggleEMag(Activity activity) {
        if (isinterrupted) return;
        Log.d(TAG, "Toggling Electromagnet");
        boolean stateEmag = true;
        if (PeriCoachTestApplication.getCurrentJob().getTesttypeId() == 1) {
            stateEmag = false;
        }
        try {
            getEmag().write(stateEmag);
        } catch (ConnectionLostException e) {
            report(e, activity);
        }
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            report(e, activity);
        }
        try {
            getEmag().write(!stateEmag);
        } catch (ConnectionLostException e) {
            report(e, activity);
        }
    }


    @SuppressWarnings("ucd")
    public void toggleTrigger(Activity activity) {
        if (isinterrupted) return;

        if (triggervalue) {
            triggervalue = false;
        } else {
            triggervalue = true;
        }

        try {
            trigger.write(triggervalue);
        } catch (Exception e) {
            report(e, activity);
            return;
        }
    }

    @Override
    public void closeUart(Activity activity) {
        try {
            if (uart2 != null) uart2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Mode getUutMode(Activity activity) {
        return uutMode;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#modeApplication(android.app.Activity)
     */
    @Override
    public void modeApplication(Activity activity) {

        if (isinterrupted) return;

        // When entering Application mode, start the Uart Thread so that console is captured.  We will search console log for signs of life, etc...
        if (thread == null || stopthread) {
            thread = new Uart2Thread((SerialConsoleFragmentCallback) activity);
            Log.d(TAG, "Starting UART Thread");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
        if (isinterrupted) return;


        Log.d(TAG, "Switching to Application Boot Mode");
        try {
            boot0.write(false);
        } catch (Exception e) {
            report(e, activity);
            return;
        }
        if (isinterrupted) return;

        try {
            boot1.write(true);
        } catch (Exception e) {
            report(e, activity);
            return;
        }
        if (isinterrupted) return;

        toggle5VDC(activity);

        uutMode = Mode.application;
        if (isinterrupted) return;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getIrange()
     */
    @Override
    public DigitalOutput getIrange() {
        return Irange;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#setIrange()
     */
    @Override
    @SuppressWarnings("ucd")
    public void setIrange(Activity activity, Boolean value) {
        try {
            Irange.write(value);
        } catch (Exception e) {
            report(e, activity);
        }

        toggle5VDC(activity);        // When switching current reading range, need to toggle charger on/off to re-power UUT

    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getBoot0()
     */
    public DigitalOutput getBoot0() {
        return boot0;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getBoot1()
     */
    public DigitalOutput getBoot1() {
        return boot1;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getIOIOUart()
     */
    @Override
    public Uart getIOIOUart() {
        return uart2;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartLog()
     */
    @Override
    @DebugLog
//    public StringBuilder getUartLog() {
    public String getUartLog() {
        return new String(log);
    }

    @Override
    public void appendUartLog(Activity activity, byte[] bytes, int numBytes){
        callback = ((SerialConsoleFragmentCallback) activity);
        byte[] temp = log;
        log = new byte[temp.length + numBytes];
        if (temp.length != 0) {
            System.arraycopy(temp, 0, log, 0, temp.length);
        }
        System.arraycopy(bytes, 0, log, temp.length, numBytes);

        callback.updateUI(new String(Arrays.copyOfRange(log, temp.length, temp.length + numBytes)));

    }

    @Override
    public void clearUartLog() {
        Log.d(TAG, "Clearing Uart Log StringBuilder instance");
        log = new byte[0];
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartInStream()
     */
    @Override
    public InputStream getUartInStream() {
        return inputStream;
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#getUartOutStream()
     */
    @Override
    public OutputStream getUartOutStream() {
        return outputStream;
    }


    private class Uart2Thread extends Thread {

        private final SerialConsoleFragmentCallback callback;
        @SuppressWarnings("unused")
        private String line;
        private long now = System.currentTimeMillis();
        private boolean setup = false;
        private InputStream is;
        private final int timeout = 200;


        public Uart2Thread(SerialConsoleFragmentCallback callback) {
            stopthread = false;
            this.callback=callback;
        }

        @Override
        public void run() {

            if (!setup) {
                is = getIOIOUart().getInputStream();
                setup = true;
            }

            while (!stopthread) {
                if (System.currentTimeMillis() > (now + 5000)) {
                    Log.d(TAG, "UART Task is running: ");
                    now = System.currentTimeMillis();
                }
                line = null;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final Thread readThread = Thread.currentThread();
                Timer t = new Timer();
                final TimerTask readTask = new TimerTask() {
                    @Override
                    public void run() {
                        readThread.interrupt();
                        System.out.printf("Timer expired, interrupt");
                        this.cancel();
                    }
                };
//                Log.d(TAG, "Schedule readTask timer for " + String.valueOf(timeout) + " ms");
                t.schedule(readTask, timeout);

                byte[] buf = new byte[1024];
                int numbytes = 0;
                try {
                    numbytes = is.read(buf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (numbytes > 0) {
//                    appendUartLog(buf, numbytes);
                    byte[] temp = log;
                    log = new byte[temp.length + numbytes];
                    if (temp.length != 0) {
                        System.arraycopy(temp, 0, log, 0, temp.length);
                    }
                    System.arraycopy(buf, 0, log, temp.length, numbytes);

                    t.cancel();
                    t.purge();
                    t = null;
                    System.out.printf("Timer Cancelled");
                    if (temp != null) {
                        Log.d(TAG + " - CALL", new String(Arrays.copyOfRange(log, temp.length, temp.length+numbytes)));
                    }
                    if (temp != null) {
                        callback.updateUI(new String(Arrays.copyOfRange(log, temp.length, temp.length+numbytes)));
                    }
                } else {
                    t.cancel();
                    t.purge();
                    t = null;
                }
            }

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                uart2.close();
            } catch (Exception e) {}

            uart2 = null;
        }
    }

    /* (non-Javadoc)
     * @see com.pietrantuono.ioioutils.IOIOUtilsInterface#stopUartThread()
     */
    @Override
    public void stopUartThread() {
        stopthread = true;
    }

}
