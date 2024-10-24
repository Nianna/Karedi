package com.github.nianna.karedi.audio;

import javafx.concurrent.Task;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class SourceDataLineAudioFilePlayTask extends Task<Long> {

    private final SourceDataLineAudioFile file;
    private final int startFrame;
    private final int endFrame;

    SourceDataLineAudioFilePlayTask(SourceDataLineAudioFile file, long startMillis, long endMillis) {
        super();
        this.file = file;
        startFrame = millisToFrames(startMillis);
        endFrame = millisToFrames(endMillis);
		setVolume(file.getVolume().floatValue());
    }

	@Override
    protected Long call() throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getContent());
        discardNFrames(bis, startFrame);
        playNFrames(bis, endFrame - startFrame);
        bis.close();
        return null;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        stopLine();
        return super.cancel(mayInterruptIfRunning);
    }

    private int millisToFrames(long millis) {
        return (int) (millis * (((double) file.getFormat().getSampleRate()) / 1000.0));
    }

    private void setVolume(float volume) {
        FloatControl gainControl = (FloatControl) file.getSourceDataLine().getControl(FloatControl.Type.MASTER_GAIN);
        if (volume == 0.0F) {
            gainControl.setValue(gainControl.getMinimum());
        } else {
            float newGain = 0.5F * gainControl.getMinimum()
                    + volume * (gainControl.getMaximum() - 0.5F * gainControl.getMinimum());
            gainControl.setValue(newGain);
        }
    }

    private void discardNFrames(ByteArrayInputStream bis, int frames) throws IOException {
        bis.readNBytes(frames * file.getFormat().getFrameSize());
    }

    private void playNFrames(ByteArrayInputStream bis, int framesToPlay) throws IOException {
        SourceDataLine line = file.getSourceDataLine();

        byte[] bufferBytes = new byte[4096];
        int readBytes;
        int totalRead = 0;
        int bytesToRead = framesToPlay * line.getFormat().getFrameSize();

        line.start();
        while ((readBytes = bis.read(bufferBytes)) != -1 && totalRead < bytesToRead) {
            int relevantBytes = Math.min(readBytes, bytesToRead - totalRead);
            line.write(bufferBytes, 0, relevantBytes);
            totalRead += relevantBytes;
        }
        line.drain();
        stopLine();
    }

    private void stopLine() {
        file.getSourceDataLine().stop();
        file.getSourceDataLine().flush();
    }

}

