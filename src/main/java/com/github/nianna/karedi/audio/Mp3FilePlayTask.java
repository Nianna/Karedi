package com.github.nianna.karedi.audio;

import com.github.nianna.karedi.I18N;
import javafx.concurrent.Task;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

class Mp3FilePlayTask extends Task<Long> {
    private static final Logger LOGGER = Logger.getLogger(Mp3FilePlayTask.class.getName());

    private int startFrame;
    private int endFrame;
    private AdvancedPlayer player;

    Mp3FilePlayTask(Mp3File file, long startMillis, long endMillis) {
        super();
        startFrame = getFrameForMillis(file.getFPS(), startMillis);
        endFrame = Math.max(getFrameForMillis(file.getFPS(), endMillis), startFrame + 1);
        ByteArrayInputStream bis = new ByteArrayInputStream(file.getCache());
        try {
            player = new AdvancedPlayer(bis);
            player.setVolume(file.getVolume().floatValue());
        } catch (JavaLayerException e) {
            LOGGER.severe(I18N.get("player.mp3.fail"));
            e.printStackTrace();
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

