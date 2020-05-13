package com.shkul.pulsegenerator.model;

import android.content.SharedPreferences;

public class SettingsDataModel {
	public enum SettingsDataModeModel {
		STEREO, MONO, LEFT, RIGHT;

		@Override
		public String toString() {
			String def = super.toString();
			return def.substring(0,1).toUpperCase()+def.substring(1).toLowerCase();
		}
	}

	public int frequency;
	public int volume;
	public SettingsDataModeModel mode;
	public int center;
	public String message;

	public SettingsDataModel() {
	}

	public SettingsDataModel(SharedPreferences preferences) {
		readFromPreference(preferences);
	}

	public void readFromPreference(SharedPreferences preferences) {
		try {
			frequency = Integer.parseInt(preferences.getString("frequency", "2400"));
			volume = Integer.parseInt(preferences.getString("volume", "100"));
			String modeString = preferences.getString("type_wave_mode", "STEREO").toUpperCase();
			mode = SettingsDataModeModel.valueOf(modeString);
			center = Integer.parseInt(preferences.getString("center", "50"));
			message = preferences.getString("message", "");
		} catch(Exception e) {
		}
	}
}
