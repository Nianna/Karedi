package com.github.nianna.karedi.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;

class SourceDataLineAudioFile extends PreloadedAudioFile {

    private final SourceDataLine sourceDataLine;
    private final byte[] content;
    private final long duration;

    private SourceDataLineAudioFile(File file, SourceDataLine sourceDataLine, byte[] content, long duration) {
        super(file);
        this.sourceDataLine = sourceDataLine;
        this.content = content;
        this.duration = duration;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public AudioFormat getFormat() {
        return sourceDataLine.getFormat();
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public void releaseResources() {
        sourceDataLine.close();
    }

    public static SourceDataLineAudioFile aacFile(File file) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            AudioFormat inAudioFormat = in.getFormat();
            AudioFormat decodedAudioFormat = new AudioFormat(
                    AudioSystem.NOT_SPECIFIED,
                    inAudioFormat.getSampleSizeInBits(),
                    inAudioFormat.getChannels(),
                    true,
                    inAudioFormat.isBigEndian());
            in = AudioSystem.getAudioInputStream(decodedAudioFormat, in);

            return convertAndLoadAudio(file, in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PreloadedAudioFile vorbisFile(File file) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            return convertAndLoadAudio(file, in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PreloadedAudioFile wavFile(File file) {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            return loadAudio(file, in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static SourceDataLineAudioFile convertAndLoadAudio(File file, AudioInputStream in)
            throws LineUnavailableException, IOException {
        AudioFormat convertedFormat = in.getFormat();
        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                convertedFormat.getSampleRate(),
                16,
                convertedFormat.getChannels(),
                convertedFormat.getChannels() * 2,
                convertedFormat.getSampleRate(),
                false
        );
        in = AudioSystem.getAudioInputStream(targetFormat, in);
        return loadAudio(file, in);
    }

    private static SourceDataLineAudioFile loadAudio(File file, AudioInputStream in) throws LineUnavailableException, IOException {
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, in.getFormat()));
        sourceDataLine.open(in.getFormat());
        byte[] content = in.readAllBytes();
        long lengthInFrames = content.length / in.getFormat().getFrameSize();
        long duration = (long) ((double) lengthInFrames * (1000.0 / (double) in.getFormat().getSampleRate()));
        in.close();
        return new SourceDataLineAudioFile(file, sourceDataLine, content, duration);
    }

}
