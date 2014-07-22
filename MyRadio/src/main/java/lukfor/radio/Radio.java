package lukfor.radio;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import lukfor.radio.mplayer.IMPlayerListener;
import lukfor.radio.mplayer.MPlayer;

public class Radio {

	private MPlayer player;

	private RadioStation currentStation = null;

	private List<RadioStation> stations;

	private List<IRadioListener> listeners = new Vector<IRadioListener>();

	//private Clip clip;

	public void on() {

		loadStations();

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

	protected void loadStations() {

		stations = new Vector<RadioStation>();

		RadioStation oe3 = new RadioStation();
		oe3.setTitle("Hitradio Ö3");
		oe3.setUrl("http://mp3stream7.apasf.apa.at:8000");
		stations.add(oe3);

		RadioStation rockAntenne = new RadioStation();
		rockAntenne.setTitle("Rock Antenne");
		rockAntenne
				.setUrl("http://www.rockantenne.de/webradio/rockantenne-aac.pls");
		stations.add(rockAntenne);

		RadioStation bozen = new RadioStation();
		bozen.setTitle("RAI Sender Bozen");
		bozen.setUrl("http://radiobzlive.rai.it/RAIBZ_Livestream");
		stations.add(bozen);

		RadioStation s1 = new RadioStation();
		s1.setTitle("Südtirol 1");
		s1.setUrl("http://str2.creacast.com:80/sudtirol1a");
		stations.add(s1);

		RadioStation fm4 = new RadioStation();
		fm4.setTitle("FM4");
		fm4.setUrl("http://mp3stream1.apasf.apa.at:8000");
		stations.add(fm4);

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
