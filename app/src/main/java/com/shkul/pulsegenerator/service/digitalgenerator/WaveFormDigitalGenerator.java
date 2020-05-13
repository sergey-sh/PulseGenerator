package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGenerator {

	protected WaveFormGenerator parent;
	protected int center;

	public WaveFormDigitalGenerator(WaveFormGenerator parent) {
		this.parent = parent;
	}

	public void init() {

	}

	public void setParam(int center) {
		this.center = center;
	}

	public short[] getDigitalData(int minSamples) {
		return null;
	}
	public int getCountSamples() {
		return 0;
	}

}
