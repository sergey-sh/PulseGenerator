package com.shkul.pulsegenerator.service.digitalgenerator;


import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGeneratorHarmonic extends WaveFormDigitalGeneratorPeriodic {

	public WaveFormDigitalGeneratorHarmonic(WaveFormGenerator parent) {
		super(parent);
	}

	@Override
	protected short getValue(int i) {
		i = i % getPeriod();
		int c = (getPeriod()*center/100);
		if(i<=c) {
			if(c>0) {
				return (short) Math.round(Short.MAX_VALUE * Math.sin((float) i / c * Math.PI));
			} else {
				return Short.MAX_VALUE;
			}
		} else {
			if((getPeriod()-c)>0) {
				return (short) Math.round(Short.MAX_VALUE * Math.sin((float) (i - c) / (getPeriod() - c) * Math.PI + Math.PI));
			} else {
				return -Short.MAX_VALUE;
			}
		}
	}

}
