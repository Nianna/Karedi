package com.github.nianna.karedi.audio;

import com.github.nianna.karedi.audio.sonic.Sonic;
import javafx.concurrent.Task;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class SourceDataLineAudioFilePlayTask extends Task<Long> {
    private static final int BUFFER_SIZE = 2200;

    private final SourceDataLineAudioFile file;
    private final int startFrame;
    private final int endFrame;
    private final int speedPercent;

    SourceDataLineAudioFilePlayTask(SourceDataLineAudioFile file, long startMillis, long endMillis, int speedPercent) {
        super();
        this.file = file;
        startFrame = millisToFrames(startMillis);
        endFrame = millisToFrames(endMillis);
        this.speedPercent = speedPercent;
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
        line.start();
        int bytesToRead = framesToPlay * line.getFormat().getFrameSize();
        if (speedPercent == 100) {
            playNBytes(bis, bytesToRead, line);
        } else {
            playNBytesWithSonic(bis, bytesToRead, line);
        }
        line.drain();
        stopLine();
    }

    private void playNBytes(ByteArrayInputStream bis, int bytesToRead, SourceDataLine line) throws IOException {
        byte[] bufferBytes = new byte[BUFFER_SIZE];
        int readBytes;
        int totalRead = 0;

        while (!isCancelled() && (readBytes = bis.read(bufferBytes)) != -1 && totalRead < bytesToRead) {
            int relevantBytes = Math.min(readBytes, bytesToRead - totalRead);
            line.write(bufferBytes, 0, relevantBytes);
            totalRead += relevantBytes;
        }
    }

    private void playNBytesWithSonic(ByteArrayInputStream bis, int bytesToRead, SourceDataLine line) throws IOException {
        Sonic sonic = new Sonic((int) line.getFormat().getSampleRate(), line.getFormat().getChannels());
        sonic.setSpeed(speedPercent / 100f);

        byte[] inBuffer = new byte[BUFFER_SIZE];
        byte[] outBuffer = new byte[BUFFER_SIZE];
        int readBytes;
        int totalRead = 0;
        int numWritten;

        while (!isCancelled() && (readBytes = bis.read(inBuffer)) != -1 && totalRead < bytesToRead) {
            int relevantBytes = Math.min(readBytes, bytesToRead - totalRead);
            if (relevantBytes <= 0) {
                sonic.flushStream();
            } else {
                sonic.writeBytesToStream(inBuffer, relevantBytes);
            }
            do {
                numWritten = sonic.readBytesFromStream(outBuffer, relevantBytes);
                if (numWritten > 0) {
                    line.write(outBuffer, 0, numWritten);
                }
            } while (numWritten > 0);
            totalRead += relevantBytes;
        }
    }

    private void stopLine() {
        file.getSourceDataLine().stop();
        file.getSourceDataLine().flush();
    }

}

