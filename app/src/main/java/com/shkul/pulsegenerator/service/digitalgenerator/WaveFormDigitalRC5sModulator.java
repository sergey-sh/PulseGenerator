package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

import java.io.UnsupportedEncodingException;

public class WaveFormDigitalRC5sModulator extends WaveFormDigitalGenerator {
	// Similar UART 8N1
	final private int charLength = 8;
	final private int startByteLength = 10;

	private short[] data;
	private short[] buffer;
	private int countSamples;
	private int period;
	private String message;

	public WaveFormDigitalRC5sModulator(WaveFormGenerator parent) {
		super(parent);
	}

	public void setParam(String message) {
		this.message = message;
	}


	public void init() {
		if(message != null) {
			period = parent.getSampleRate()/parent.getFrequency();
			data = new short[period*(charLength+2+startByteLength)*message.length()];
			try {
				int j = 0;
				for(byte c: message.getBytes("US-ASCII")) {
					j = fillSetBit(data, j, startByteLength);
					j = fillClearBit(data, j, 1);
					byte mask = 1;
					for(int n = 0;n<charLength;n++) {
						if((c & mask) != 0) {
							j = fillSetBit(data, j, 1);
						} else {
							j = fillClearBit(data, j, 1);

						}
						mask<<=1;
					}
					j = fillSetBit(data, j, 1);
				}
			} catch (UnsupportedEncodingException e) {
			}
		}
	}

	public short[] getDigitalData(int minSamples) {
		if(data!=null) {
			if (data.length < minSamples) {
				countSamples = data.length*((minSamples+data.length-1)/data.length);
				if(buffer == null || buffer.length<countSamples) {
					buffer = new short[countSamples];
				}
				for(int i=0;i<countSamples;i+=data.length) {
					System.arraycopy(data, 0, buffer, i, data.length);
				}
				return buffer;
			} else {
				countSamples = data.length;
				return data;
			}
		}
		return null;
	}

	public int getCountSamples() {
		return countSamples;
	}

	private int fillSamples(short[] buffer, int i, int countBits, short firstValue) {
		for(int j=0;j<countBits;j++) {
			int n;
			for(n=0;n<period/2;n++) {
				buffer[i++] = firstValue;
			}
			for(;n<period;n++) {
				buffer[i++] = (short) -firstValue;
			}
		}
		return i;
	}
	private int fillSetBit(short[] buffer, int i, int countBits) {
		return fillSamples(buffer, i, countBits, Short.MAX_VALUE);
	}

	private int fillClearBit(short[] buffer, int i, int countBits) {
		return fillSamples(buffer, i, countBits, (short)-Short.MAX_VALUE);
	}
}
