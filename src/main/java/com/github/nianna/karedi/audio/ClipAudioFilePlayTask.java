package com.github.nianna.karedi.audio;

import javafx.concurrent.Task;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.concurrent.TimeUnit;

class ClipAudioFilePlayTask extends Task<Long> {

    private static final long POSITION_CHECK_INTERVAL_IN_MS = 5;

    private final Clip clip;
    private final long startMicroseconds;
    private final long endMicroseconds;

    ClipAudioFilePlayTask(ClipAudioFile file, long startMillis, long endMillis) {
        super();
        clip = file.getClip();
        startMicroseconds = TimeUnit.MILLISECONDS.toMicros(startMillis);
        endMicroseconds = TimeUnit.MILLISECONDS.toMicros(endMillis);
		setVolume(file.getVolume().floatValue());
    }

	private void setVolume(float volume) {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		if (volume == 0.0F) {
			gainControl.setValue(gainControl.getMinimum());
		} else {
			float newGain = 0.5F * gainControl.getMinimum()
                    + volume * (gainControl.getMaximum() - 0.5F * gainControl.getMinimum());
			gainControl.setValue(newGain);
		}
	}

	@Override
    protected Long call() throws InterruptedException {
        clip.setMicrosecondPosition(startMicroseconds);
        clip.start();
        long initialSleepTime = TimeUnit.MICROSECONDS.toMillis(endMicroseconds - startMicroseconds)
                - 5 * POSITION_CHECK_INTERVAL_IN_MS;
        if (initialSleepTime > 0) {
            Thread.sleep(initialSleepTime);
        }
        while (!isCancelled() && clip.getMicrosecondPosition() < endMicroseconds) {
            Thread.sleep(POSITION_CHECK_INTERVAL_IN_MS);
        }
        clip.stop();
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        clip.stop();
        clip.flush();
        return super.cancel(mayInterruptIfRunning);
    }

}

