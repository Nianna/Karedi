package com.github.nianna.karedi.audio;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * ClipPlayer is a class designed for playing short sounds. It stores the sound
 * as a Clip, which is loaded prior to the playback.
 * 
 * @see Clip
 */
public class ClipPlayer {
	private Clip clip;

	/**
	 * Loads the audio file that will be later used by this player from the
	 * specified URL.
	 * 
	 * @param clipUrl
	 *            URL giving the location of the clip
	 */
	public void setClip(URL clipUrl) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(clipUrl);
	        /* previous approach - doesn't work with OpenJDK
	        clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        */
			DataLine.Info info = new DataLine.Info(Clip.class, audioIn.getFormat());
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays the sound from the beginning. If the previous execution has not yet
	 * finished, interrupts it.
	 */
	public void play() {
		if (clip != null) {
			if (clip.isRunning()) {
				clip.stop();
			}
			clip.setFramePosition(0);
			clip.start();
		}
	}
}
