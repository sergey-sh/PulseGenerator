package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalGeneratorTriangle extends WaveFormDigitalGeneratorPeriodic {

	public WaveFormDigitalGeneratorTriangle(WaveFormGenerator parent) {
		super(parent);
	}

	@Override
	protected short getValue(int i) {
		i = i % getPeriod();
		int c = (getPeriod()*center/100);
		int m;
		if(i<=c) {
			if(i<c/2) {
				if(c>0) {
					return (short) (Short.MAX_VALUE * 4 * i / c - Short.MAX_VALUE);
				} else {
					return Short.MAX_VALUE;
				}
			} else {
				if(c>0) {
					return (short) (-Short.MAX_VALUE * 4 * (i - c / 2) / c + Short.MAX_VALUE);
				} else {
					return -Short.MAX_VALUE;
				}
			}
		} else {
			if((i-c)<(getPeriod()-c)/2) {
				if ((getPeriod() - c) > 0) {
					return (short) (Short.MAX_VALUE * 4 * (i - c) / (getPeriod() - c) - Short.MAX_VALUE);
				} else {
					return Short.MAX_VALUE;
				}
			} else {
				if ((getPeriod() - c) > 0) {
					return (short) (-Short.MAX_VALUE * 4 * (i - c - (getPeriod() - c) / 2) / (getPeriod() - c) + Short.MAX_VALUE);
				} else {
					return -Short.MAX_VALUE;
				}
			}
		}
	}

}

