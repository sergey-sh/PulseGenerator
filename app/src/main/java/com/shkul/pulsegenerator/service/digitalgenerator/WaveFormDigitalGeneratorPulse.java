package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGeneratorPulse extends WaveFormDigitalGeneratorPeriodic {

	public WaveFormDigitalGeneratorPulse(WaveFormGenerator parent) {
		super(parent);
	}

	@Override
	protected short getValue(int i) {
		i = i % getPeriod();
		int c = (getPeriod()*center/100);
		if(i<=c) {
			return Short.MAX_VALUE;
		} else {
			return -Short.MAX_VALUE;
		}
	}

}
