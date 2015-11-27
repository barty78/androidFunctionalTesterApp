package com.pietrantuono.pericoach.newtestapp.test.ui.classes;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.CapSense;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalInput.Spec;
import ioio.lib.api.DigitalInput.Spec.Mode;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.IcspMaster;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PulseInput.ClockRate;
import ioio.lib.api.PulseInput.PulseMode;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.Sequencer;
import ioio.lib.api.Sequencer.ChannelConfig;
import ioio.lib.api.SpiMaster;
import ioio.lib.api.SpiMaster.Config;
import ioio.lib.api.SpiMaster.Rate;
import ioio.lib.api.TwiMaster;
import ioio.lib.api.Uart;
import ioio.lib.api.Uart.Parity;
import ioio.lib.api.Uart.StopBits;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.api.exception.IncompatibilityException;

public class IOIOMock implements IOIO {

	@Override
	public void waitForConnect() throws ConnectionLostException, IncompatibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void waitForDisconnect() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void softReset() throws ConnectionLostException {
		// TODO Auto-generated method stub

	}

	@Override
	public void hardReset() throws ConnectionLostException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getImplVersion(VersionType v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalInput openDigitalInput(Spec spec) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalInput openDigitalInput(int pin) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalInput openDigitalInput(int pin, Mode mode) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput openDigitalOutput(ioio.lib.api.DigitalOutput.Spec spec, boolean startValue)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput openDigitalOutput(int pin, ioio.lib.api.DigitalOutput.Spec.Mode mode, boolean startValue)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput openDigitalOutput(int pin, boolean startValue) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DigitalOutput openDigitalOutput(int pin) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalogInput openAnalogInput(int pin) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PwmOutput openPwmOutput(ioio.lib.api.DigitalOutput.Spec spec, int freqHz) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PwmOutput openPwmOutput(int pin, int freqHz) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PulseInput openPulseInput(Spec spec, ClockRate rate, PulseMode mode, boolean doublePrecision)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PulseInput openPulseInput(int pin, PulseMode mode) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uart openUart(Spec rx, ioio.lib.api.DigitalOutput.Spec tx, int baud, Parity parity, StopBits stopbits)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uart openUart(int rx, int tx, int baud, Parity parity, StopBits stopbits) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiMaster openSpiMaster(Spec miso, ioio.lib.api.DigitalOutput.Spec mosi, ioio.lib.api.DigitalOutput.Spec clk,
			ioio.lib.api.DigitalOutput.Spec[] slaveSelect, Config config) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiMaster openSpiMaster(int miso, int mosi, int clk, int[] slaveSelect, Rate rate)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiMaster openSpiMaster(int miso, int mosi, int clk, int slaveSelect, Rate rate)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TwiMaster openTwiMaster(int twiNum, ioio.lib.api.TwiMaster.Rate rate, boolean smbus)
			throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IcspMaster openIcspMaster() throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CapSense openCapSense(int pin) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CapSense openCapSense(int pin, float filterCoef) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sequencer openSequencer(ChannelConfig[] config) throws ConnectionLostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beginBatch() throws ConnectionLostException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endBatch() throws ConnectionLostException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sync() throws ConnectionLostException, InterruptedException {
		// TODO Auto-generated method stub

	}

}
