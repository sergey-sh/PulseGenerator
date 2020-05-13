package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGeneratorSaw extends WaveFormDigitalGeneratorPeriodic {

	public WaveFormDigitalGeneratorSaw(WaveFormGenerator parent) {
		super(parent);
	}

	@Override
	protected short getValue(int i) {
		i = i % getPeriod();
		if(getPeriod()>0) {
			return (short) (Short.MAX_VALUE * 2 * i / getPeriod() - Short.MAX_VALUE);
		} else {
			return 0;
		}
	}

}

