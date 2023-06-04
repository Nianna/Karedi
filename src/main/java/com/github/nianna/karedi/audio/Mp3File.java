package com.github.nianna.karedi.audio;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import com.github.nianna.karedi.I18N;

public class Mp3File implements CachedAudioFile {
	private static final Logger LOGGER = Logger.getLogger(Mp3File.class.getName());
	private File file;
	private byte[] cache;

	private DoubleProperty volumeProperty = new SimpleDoubleProperty(0.6);
	private long duration;
	private Double fps;

	public Mp3File(File file) throws IOException {
		this.file = file;
		reload();
	}

	private void cache() {
		try {
			cache = new FileLoader(file).load();
		} catch (Exception e) {
			LOGGER.severe(I18N.get("mp3file.cache.fail"));
			e.printStackTrace();
		}
	}

	@Override
	public void reload() throws IOException {
		loadInfo();
		cache();
	}

	private void loadInfo() throws IOException {
		com.mpatric.mp3agic.Mp3File mp3file = null;
		try {
			mp3file = new com.mpatric.mp3agic.Mp3File(file.getAbsolutePath());
		} catch (UnsupportedTagException | InvalidDataException e) {
			LOGGER.warning(I18N.get("mp3file.invalid_source"));
			e.printStackTrace();
		} catch (IOException e) {
			throw (e);
		}
		double spr = SamplesPerFrame.get(mp3file.getVersion(), mp3file.getLayer());
		fps = (double) (mp3file.getSampleRate() / spr);
		duration = mp3file.getLengthInMilliseconds();
	}

	@Override
	public byte[] getCache() {
		return cache;
	}

	@Override
	public Double getFPS() {
		return fps;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public DoubleProperty volumeProperty() {
		return volumeProperty;
	}

	@Override
	public final void setVolume(double value) {
		volumeProperty.set(value);
	}

}
