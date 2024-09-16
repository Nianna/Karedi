package com.github.nianna.karedi.audio;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.io.File;

/**
 * Represents an audio file whose data is loaded prior to playback so that it
 * can be accessed faster.
 */
public abstract class PreloadedAudioFile {

	private final File file;

	private final DoubleProperty volumeProperty = new SimpleDoubleProperty(0.6);

	public PreloadedAudioFile(File file) {
		this.file = file;
	}

	/**
	 * Obtains the media length in milliseconds.
	 * 
	 * @return the length in milliseconds
	 */
	public abstract long getDuration();

	/**
	 * Gets the associated file.
	 * 
	 * @return file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * The volume of the file. It is expressed as a number between 0 and 1, 0.6
	 * is assumed to be the original volume.
	 */
	public DoubleProperty volumeProperty() {
		return volumeProperty;
	}

	public Double getVolume() {
		return volumeProperty().get();
	}

	/**
	 * Obtains the name of the associated file.
	 * 
	 * @return the name of the file (just the last name in the pathname's
	 *         sequence)
	 */
	public String getName() {
		return getFile().getName();
	}

	public abstract void releaseResources();

}
