package com.shkul.pulsegenerator.service.digitalgenerator;

import com.shkul.pulsegenerator.service.WaveFormGenerator;

public class WaveFormDigitalRC5sModulator extends WaveFormDigitalGenerator {
	// Similar UART 8N1
	final private int charLength = 8;
	final private int startByteLength = 10;

	private short[] data;
	private short[] buffer;
	private int countSamples;
	private int period;
	private byte[] message;
	private int byteAtPacket;

	public WaveFormDigitalRC5sModulator(WaveFormGenerator parent) {
		super(parent);
	}

	public void setParam(byte[] message) {
		setParam(message, 1, false, false);
	}

	public void setParam(byte[] message,int byteAtPacket, boolean generateIncSequence, boolean generateServoOne) {
		this.message = message;
		this.byteAtPacket = byteAtPacket;
		if(generateIncSequence) {
			doGenerateIncSequence();
		} else if(generateServoOne && (byteAtPacket == 2)) {
			doGenerateServoOne();
		}
	}

	// 10 - 1
	// 01 - 0
	public void init() {
		if(message != null) {
			period = parent.getSampleRate()/parent.getFrequency();
			data = new short[period*(charLength*byteAtPacket+2+startByteLength)*(message.length/byteAtPacket)];
			int j = 0;
			int i = 0;
			int x;
			while(i < message.length) {
				j = fillSetBit(data, j, startByteLength);
				j = fillClearBit(data, j, 1);
				x = 0;
				while(x<byteAtPacket && i < message.length) {
					byte c = message[i];
					byte mask = 1;
					for (int n = 0; n < charLength; n++) {
						if ((c & mask) != 0) {
							j = fillSetBit(data, j, 1);
						} else {
							j = fillClearBit(data, j, 1);

						}
						mask <<= 1;
					}
					x++;
					i++;
				}
				j = fillSetBit(data, j, 1);
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

	private void doGenerateIncSequence() {
		int len = 0x100;
		int div = len % byteAtPacket;
		if(div>0) {
			len+=(byteAtPacket-div);
		}
		message = new byte[len];
		int i=0, j = 0;
		while(i<message.length) {
			message[i++] = (byte)(j & 0xFF);
			j++;
		}
	}

	private void doGenerateServoOne() {
		message = new byte[2];
		message[0] = 127;
		byte servo = 0;
		message[1] = (byte) ((((message[0] ^ (message[0] >> 4)) ^ servo) | 1) & 0x3F);
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
