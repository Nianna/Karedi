package main.java.com.github.nianna.karedi.audio;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;
import main.java.com.github.nianna.karedi.I18N;

/**
 * A player operating on a preloaded mp3 file.
 */
public class Mp3Player {
	private static final Logger LOGGER = Logger.getLogger(Mp3Player.class.getName());

	private PlayTask playTask;
	private Mp3File file;
	private PlaybackListener playbackListener;

	public void play(long startMillis, long endMillis) {
		stop();

		if (file != null) {
			playTask = new PlayTask(startMillis, endMillis);
			Thread th = new Thread(playTask);
			th.setDaemon(true);
			th.start();
		}
	}

	public void stop() {
		if (playTask != null) {
			playTask.cancel();
		}
	}

	public void setFile(Mp3File file) {
		this.file = file;
	}

	public Mp3File getFile() {
		return file;
	}

	public void setPlaybackListener(PlaybackListener playbackListener) {
		this.playbackListener = playbackListener;
	}

	private class PlayTask extends Task<Long> {
		private int startFrame;
		private int endFrame;
		private AdvancedPlayer player;

		public PlayTask(long startMillis, long endMillis) {
			super();
			if (startMillis < endMillis) {
				startFrame = getFrameForMillis(file.getFPS(), startMillis);
				endFrame = Math.max(getFrameForMillis(file.getFPS(), endMillis), startFrame + 1);
				ByteArrayInputStream bis = new ByteArrayInputStream(file.getCache());
				try {
					player = new AdvancedPlayer(bis);
					player.setVolume(file.getVolume().floatValue());
					player.setPlayBackListener(playbackListener);
				} catch (JavaLayerException e) {
					LOGGER.severe(I18N.get("player.mp3.fail"));
					e.printStackTrace();
				}
			}
		}

		@Override
		protected Long call() throws Exception {
			if (player != null) {
				player.play(startFrame, endFrame);
			}
			return null;
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			if (player != null) {
				player.close();
			}
			return super.cancel(mayInterruptIfRunning);
		}

		private int getFrameForMillis(double fps, long millis) {
			return (int) (fps * (millis / 1000.0));
		}
	}

}
