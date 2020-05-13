package com.shkul.pulsegenerator.service;

import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGenerator;

public class WaveFormGenerator {
	private int sampleRate;
	private int frequency;
	private int volume;

	private boolean mono;
	private boolean left;
	private boolean right;

	private int countSamples = 0;

	private WaveFormDigitalGenerator digitalGenerator;

	private short[] data;

	public WaveFormGenerator(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public void setParam(int frequency, int volume, boolean mono, boolean left, boolean right) {
		this.frequency = frequency;
		this.volume = volume;
		this.mono = mono;
		this.left = left;
		this.right = right;
	}

	public void init() {
		if(digitalGenerator!=null) {
			digitalGenerator.init();

		}

	}

	public WaveFormDigitalGenerator getDigitalGenerator() {
		return digitalGenerator;
	}

	public void init_data() {
		if(data==null || data.length<countSamples) {
			data = new short[countSamples];
		}
	}

	public void setDigitalGenerator(WaveFormDigitalGenerator digitalGenerator) {
		this.digitalGenerator = digitalGenerator;
	}

	public short[] getDigitalData(int minSamplesSize) {
		countSamples = 0;
		if(digitalGenerator!=null) {
			short[] data = digitalGenerator.getDigitalData(minSamplesSize);
			countSamples = digitalGenerator.getCountSamples();
			if(isMono()) {
				return filledMono(data, countSamples);
			} else if(isLeft() && isRight()) {
				return filledStereo(data, countSamples);
			} else if(isLeft()) {
				return filledLeft(data, countSamples);
			} else if(isRight()) {
				return filledRight(data, countSamples);
			}
		}
		return null;
	}

	private short[] filledMono(short[] source, int countSamples) {
		this.countSamples = countSamples;
		init_data();
		for(int i = 0;i<countSamples;i++) {
			data[i] = (short)((volume*source[i])/100);
		}
		return data;
	}

	private short[] filledStereo(short[] source,int countSamples) {
		this.countSamples = 2*countSamples;
		init_data();
		for(int i = 0, j =0;i<countSamples;i++, j+=2) {
			data[j] = (short)((volume*source[i])/100);
			data[j+1] = data[j];
		}
		return data;
	}

	private short[] filledLeft(short[] source,int countSamples) {
		this.countSamples = 2*countSamples;
		init_data();
		for(int i = 0, j =0;i<countSamples;i++, j+=2) {
			data[j] = (short)((volume*source[i])/100);
			data[j+1] = 0;
		}
		return data;
	}

	private short[] filledRight(short[] source,int countSamples) {
		this.countSamples = 2*countSamples;
		init_data();
		for(int i = 0, j =0;i<countSamples;i++, j+=2) {
			data[j] = 0;
			data[j+1] = (short)((volume*source[i])/100);
		}
		return data;
	}

	public int getSamples() {
		return countSamples;
	}

	public boolean isMono() {
		return mono;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isLeft() {
		return left;
	}

	public boolean isRight() {
		return right;
	}

	public int getVolume() {
		return volume;
	}
}
