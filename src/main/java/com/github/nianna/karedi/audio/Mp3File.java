package com.github.nianna.karedi.audio;

import com.github.nianna.karedi.I18N;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Mp3File extends PreloadedAudioFile {
	private static final Logger LOGGER = Logger.getLogger(Mp3File.class.getName());
	private byte[] cache;

	private long duration;
	private Double fps;

	public Mp3File(File file) throws IOException {
		super(file);
		reload();
	}

	private void cache() {
		try {
			cache = new FileLoader(getFile()).load();
		} catch (Exception e) {
			LOGGER.severe(I18N.get("mp3file.cache.fail"));
			e.printStackTrace();
		}
	}

	public void reload() throws IOException {
		loadInfo();
		cache();
	}

	private void loadInfo() throws IOException {
		com.mpatric.mp3agic.Mp3File mp3file = null;
		try {
			mp3file = new com.mpatric.mp3agic.Mp3File(getFile().getAbsolutePath());
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

	public byte[] getCache() {
		return cache;
	}

	public Double getFPS() {
		return fps;
	}

	@Override
	public long getDuration() {
		return duration;
	}

}
