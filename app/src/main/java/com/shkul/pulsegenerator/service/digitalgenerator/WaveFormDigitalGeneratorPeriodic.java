package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGeneratorPeriodic extends WaveFormDigitalGenerator {

	private short[] data;
	private short[] buffer;
	private int countSamples;
	private int period;

	public WaveFormDigitalGeneratorPeriodic(WaveFormGenerator parent) {
		super(parent);
	}

	@Override
	public void init() {
		period = parent.getSampleRate()/parent.getFrequency();
		data = new short[period];
		for(int i=0;i<period;i++) {
			data[i] = getValue(i);
		}
	}

	protected int getPeriod() {
		return period;
	}

	protected short getValue(int i) {
		return 0;
	}

	@Override
	public short[] getDigitalData(int minSamples) {
		countSamples = period*((int)((minSamples + period - 1)/period));
		if(buffer == null || buffer.length<countSamples) {
			buffer = new short[countSamples];
		}
		for(int i=0;i<countSamples;i+=period) {
			System.arraycopy(data, 0, buffer, i, period);
		}
		return buffer;
	}

	@Override
	public int getCountSamples() {
		return countSamples;
	}
}
