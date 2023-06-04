package com.github.nianna.karedi.audio;

import java.io.File;
import java.io.IOException;

import javafx.beans.property.DoubleProperty;

/**
 * Represents an audio file whose data is loaded prior to playback so that it
 * can be accessed faster.
 * <p>
 * Each CachedAudioFile offers its duration and FPS (frames per second) value,
 * which can be later used for calculating the appropriate frame number for the
 * position in milliseconds.
 */
public interface CachedAudioFile {

	/**
	 * Gets the audio data of this file.
	 * 
	 * @return the array of bytes with audio data
	 */
	public byte[] getCache();

	/**
	 * Obtains the frames per second value of this file. It can be later used
	 * for calculating frame number from positions expressed in milliseconds.
	 * 
	 * @return the frames per second value of this audio file
	 */
	public Double getFPS();

	/**
	 * Obtains the media length in milliseconds.
	 * 
	 * @return the length in milliseconds
	 */
	public long getDuration();

	/**
	 * Gets the associated file.
	 * 
	 * @return file
	 */
	public File getFile();

	/**
	 * Loads the content of the file and caches it. Updates all properties.
	 * 
	 * @throws IOException
	 *             if an input error has occurred
	 */
	public void reload() throws IOException;

	/**
	 * The volume of the file. It is expressed as a number between 0 and 1, 0.6
	 * is assumed to be the original volume.
	 */
	public DoubleProperty volumeProperty();

	default public Double getVolume() {
		return volumeProperty().get();
	}

	default public void setVolume(double value) {
		volumeProperty().set(value);
	}

	/**
	 * Obtains the name of the associated file.
	 * 
	 * @return the name of the file (just the last name in the pathname's
	 *         sequence)
	 */
	default public String getName() {
		return getFile().getName();
	}

}
