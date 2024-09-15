package com.github.nianna.karedi.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.io.File;
import java.util.concurrent.TimeUnit;

class ClipAudioFile extends PreloadedAudioFile {
    private final Clip clip;

    private ClipAudioFile(File file, Clip clip) {
        super(file);
        this.clip = clip;
    }

    @Override
    public long getDuration() {
        return TimeUnit.MICROSECONDS.toMillis(clip.getMicrosecondLength());
    }

    public Clip getClip() {
        return clip;
    }

    @Override
    public void releaseResources() {
        clip.close();
    }

    public static ClipAudioFile aacFile(File file) {
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

            DataLine.Info info = new DataLine.Info(Clip.class, targetFormat);
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(in);
            in.close();
            return new ClipAudioFile(file, clip);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
