package com.shkul.pulsegenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shkul.pulsegenerator.adapter.TypeWaveListAdapter;
import com.shkul.pulsegenerator.model.SettingsDataModel;
import com.shkul.pulsegenerator.model.TypeWaveModel;
import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGenerator;
import com.shkul.pulsegenerator.service.WaveFormGenerator;
import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGeneratorHarmonic;
import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGeneratorPulse;
import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGeneratorSaw;
import com.shkul.pulsegenerator.service.digitalgenerator.WaveFormDigitalGeneratorTriangle;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	final private int sampleRate = 48000;
	final private int waitPeriod = 200;

	FloatingActionButton play_btn;
	FloatingActionButton stop_btn;

	SettingsDataModel settingsData;

	private int currentTypeWave = 0;
	private boolean playing = false;

	private int minSamplesSize;

	private AudioTrack audioTrack;
	private WaveFormGenerator waveGenerator;
	private Thread threadWaveGenerator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		init_btn();
		init_list();

		if(savedInstanceState != null) {
			currentTypeWave = savedInstanceState.getInt("currentTypeWave", 0);
		}

		settingsData = new SettingsDataModel();
	}

	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentTypeWave", currentTypeWave);
	}


	private void updateCurrentTypeWave() {
		updateDetailText();
		waveGenerator = new WaveFormGenerator(sampleRate);
		waveGenerator.setParam(
				settingsData.frequency,
				settingsData.volume,
				settingsData.mode.equals(SettingsDataModel.SettingsDataModeModel.MONO),
				settingsData.mode.equals(SettingsDataModel.SettingsDataModeModel.LEFT) || settingsData.mode.equals(SettingsDataModel.SettingsDataModeModel.STEREO),
				settingsData.mode.equals(SettingsDataModel.SettingsDataModeModel.RIGHT) || settingsData.mode.equals(SettingsDataModel.SettingsDataModeModel.STEREO)
		);
		WaveFormDigitalGenerator digitalGenerator = null;
		if(currentTypeWave == 0) {
			digitalGenerator = new WaveFormDigitalGeneratorHarmonic(waveGenerator);
		} else if(currentTypeWave == 1) {
			digitalGenerator = new WaveFormDigitalGeneratorPulse(waveGenerator);
		} else if(currentTypeWave == 2) {
			digitalGenerator = new WaveFormDigitalGeneratorTriangle(waveGenerator);
		} else if(currentTypeWave == 3) {
			digitalGenerator = new WaveFormDigitalGeneratorSaw(waveGenerator);
		}
		if(digitalGenerator != null) {
			digitalGenerator.setParam(settingsData.center);
		}
		waveGenerator.setDigitalGenerator(digitalGenerator);
		waveGenerator.init();
		if(playing) {
			typeWaveStopPlay();
			typeWaveStartPlay();
		}
	}

	private void updateDetailText() {
		TextView detailText = findViewById(R.id.listItemDetailView);
		String[] listTitle = getResources().getStringArray(R.array.listWaveForm);
		String content = "<H1>"+(currentTypeWave>=0 && currentTypeWave<listTitle.length ? listTitle[currentTypeWave]:"")+"</H1>"
			+ "<p>"
			+ "Frequency: " + settingsData.frequency+ ", "
			+ "Volume: " + settingsData.volume + "%, "
			+ "Mode: " + settingsData.mode.toString() + ", "
			+ "Center: " + settingsData.center + "% "
			+ "</p>"
		;
		if(currentTypeWave == 4) {
			content+= "<p>"
				+ "Message: " + settingsData.message+ " "
				+ "</p>"
			;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			detailText.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
		} else {
			detailText.setText(Html.fromHtml(content));
		}
	}

	private void  init_list() {
		ListView list = findViewById(R.id.listView);

		String[] listTitle = getResources().getStringArray(R.array.listWaveForm);

		TypeWaveModel[] listData = new TypeWaveModel[] {
				new TypeWaveModel(R.drawable.ic_harmonic_wave, listTitle[0]),
				new TypeWaveModel(R.drawable.ic_pulse_wave, listTitle[1]),
				new TypeWaveModel(R.drawable.ic_triangle_wave, listTitle[2]),
				new TypeWaveModel(R.drawable.ic_saw_wave, listTitle[3])
		};

		final TypeWaveListAdapter adapter = new TypeWaveListAdapter(this, listData);
		list.setAdapter(adapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				setCurrentTypeWave(position);
			}
		});
	}

	private void setCurrentTypeWave(int position) {
		currentTypeWave = position;
		updateCurrentTypeWave();
	}

	private void typeWaveStartPlay() {
		playing = true;
		updateUIButton();
		int channelOutMode = (waveGenerator!=null && waveGenerator.isMono()) ? AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;
		int minBufferSize;
		minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelOutMode, AudioFormat.ENCODING_PCM_16BIT);
		minSamplesSize = minBufferSize / 2;
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelOutMode, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
		if(waveGenerator!=null && waveGenerator.getDigitalGenerator()!=null) {
			threadWaveGenerator = new Thread() {
				@Override
				public void run() {
					while(playing) {
						short[] data = waveGenerator.getDigitalData(minSamplesSize);
						if (data != null) {
							audioTrack.write(data, 0, waveGenerator.getSamples());
						} else {
							try {
								Thread.sleep(waitPeriod);
							} catch (InterruptedException e) {
							}
						}
					}
				}
			};
			threadWaveGenerator.start();
			audioTrack.play();
		}
	}

	private void typeWaveStopPlay() {
		playing = false;
		updateUIButton();
		if(audioTrack != null) {
			audioTrack.stop();
			audioTrack.release();
			audioTrack = null;
		}
		if(threadWaveGenerator!=null) {
			try {
				threadWaveGenerator.join(waitPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threadWaveGenerator = null;
		}
	}

	private void updateUIButton() {
		play_btn = findViewById(R.id.play);
		stop_btn = findViewById(R.id.pause);
		if(playing) {
			play_btn.setVisibility(View.INVISIBLE);
			stop_btn.setVisibility(View.VISIBLE);
		} else {
			play_btn.setVisibility(View.VISIBLE);
			stop_btn.setVisibility(View.INVISIBLE);
		}
	}

	private void init_btn() {
		play_btn = findViewById(R.id.play);
		play_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				typeWaveStartPlay();
			}
		});
		stop_btn = findViewById(R.id.pause);
		stop_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				typeWaveStopPlay();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		settingsData.readFromPreference(sharedPreferences);
		updateCurrentTypeWave();
	}

	@Override
	public void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		if(playing) {
			typeWaveStopPlay();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		settingsData.readFromPreference(sharedPreferences);
		updateCurrentTypeWave();
	}

}
