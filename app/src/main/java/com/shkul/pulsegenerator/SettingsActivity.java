package com.shkul.pulsegenerator;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.settings, new SettingsFragment())
				.commit();
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
		List<CheckBoxPreference> cbp_list = new ArrayList<CheckBoxPreference>();

		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey);
			cbp_list.add((CheckBoxPreference) getPreferenceManager().findPreference("generateIncSequence"));
			cbp_list.add((CheckBoxPreference) getPreferenceManager().findPreference("generateServoOne"));
			for (CheckBoxPreference cbp : cbp_list) {
				cbp.setOnPreferenceClickListener(this);
			}
			EditTextPreference byteAtPacket = getPreferenceManager().findPreference("byteAtPacket");
			assert byteAtPacket != null;
			byteAtPacket.setOnPreferenceChangeListener(this);
			onPreferenceChange(byteAtPacket, byteAtPacket.getText());
		}

		@Override
		public boolean onPreferenceClick(Preference arg0) {
			for (CheckBoxPreference cbp : cbp_list) {
				if (!cbp.getKey().equals(arg0.getKey()) && cbp.isChecked()) {
					cbp.setChecked(false);
				}
			}
			return false;
		}

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(preference.getKey().equals("byteAtPacket")) {
				Long value = Long.parseLong(newValue.toString());
				CheckBoxPreference generateServoOne = findPreference("generateServoOne");
				assert generateServoOne != null;
				generateServoOne.setEnabled(value.equals(2L));
				if(!generateServoOne.isEnabled() && generateServoOne.isChecked()) {
					generateServoOne.setChecked(false);
				}
			}
			return true;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}}