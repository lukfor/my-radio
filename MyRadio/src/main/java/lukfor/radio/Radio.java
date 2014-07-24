package lukfor.radio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import lukfor.radio.mplayer.IMPlayerListener;
import lukfor.radio.mplayer.MPlayer;

public class Radio {

	private MPlayer player;

	private RadioStation currentStation = null;

	private RadioConfig config;

	private List<RadioStation> stations;

	private List<IRadioListener> listeners = new Vector<IRadioListener>();

	// private Clip clip;

	public boolean on() {

		if (!loadStations()) {
			return false;
		}

		player = new MPlayer("/usr/bin/mplayer");
		player.addListener(new IMPlayerListener() {

			@Override
			public void onChange(int type) {
				switch (type) {
				case STREAM_TITLE_CHANGED:
					if (currentStation != null) {

						/*
						 * if (clip != null){ if (clip.isRunning()){
						 * clip.stop(); } }
						 */

						currentStation.setTrackTitle(player.getStreamTitle());
						fireUpdate(currentStation);
					}
					break;
				}
			}
		});

		player.init();

		playNext();

		return true;

	}

	public void off() {
		try {
			player.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected boolean loadStations() {

		try {
			YamlReader reader = new YamlReader(new FileReader("stations.txt"));
			reader.getConfig().setPropertyElementType(RadioConfig.class,
					"stations", RadioStation.class);
			config = reader.read(RadioConfig.class);
			stations = config.getStations();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void playNext() {

		int index = -1;

		if (currentStation != null) {
			index = stations.indexOf(currentStation) + 1;
			if (index >= stations.size()) {
				index = 0;
			}
		} else {
			index = 0;
		}

		RadioStation station = stations.get(index);
		play(station);

	}

	public void playPrevious() {

		int index = -1;

		if (currentStation != null) {
			index = stations.indexOf(currentStation) - 1;
			if (index < 0) {
				index = stations.size() - 1;
			}
		} else {
			index = stations.size() - 1;
			;
		}

		RadioStation station = stations.get(index);
		play(station);

	}

	protected void play(RadioStation station) {

		/*
		 * try{ AudioInputStream audioIn = AudioSystem.getAudioInputStream(new
		 * File("rauschen.wav")); clip = AudioSystem.getClip();
		 * clip.open(audioIn); clip.start(); }catch(Exception e){
		 * e.printStackTrace(); }
		 */

		currentStation = station;
		try {
			player.play(station.getUrl());
			fireUpdate(currentStation);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isPlaying() {
		return currentStation != null;
	}

	public RadioStation getRadioStation() {
		return currentStation;
	}

	public void addListener(IRadioListener listener) {
		listeners.add(listener);
	}

	protected void fireUpdate(RadioStation station) {
		for (IRadioListener listener : listeners) {
			listener.onUpdate(station);
		}
	}

}
