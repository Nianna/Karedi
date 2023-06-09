package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.dialog.OverwriteAlert;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.github.nianna.karedi.context.AppContext.LOGGER;

class NewSongAction extends ContextfulKarediAction {

    private Song song;
    private File audioFile;
    private File outputDir;

    NewSongAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (appContext.needsSaving()) {
            boolean proceed = KarediApp.getInstance().saveChangesIfUserWantsTo();
            if (!proceed) {
                return;
            }
        }
        new NewSongWizard().start().ifPresent(result -> {
            song = result.getSong();
            audioFile = result.getAudioFile();
            outputDir = result.getOutputDir();
            finish();
        });
    }

    private boolean finish() {
        appContext.reset(true);
        if (outputDir == null && audioFile != null) {
            appContext.loadAudioFile(audioFile);
        }
        appContext.setSong(song);
        if (outputDir != null) {
            File songFolder = new File(outputDir, getSongFilename());
            if ((songFolder.exists() || songFolder.mkdirs()) && songFolder.canWrite()) {
                LOGGER.info(I18N.get("creator.subfolder.success"));
                KarediApp.getInstance().setInitialDirectory(songFolder);
                copyAudioFile(songFolder);
                createTxtFile(songFolder);
            } else {
                LOGGER.severe(I18N.get("creator.subfolder.fail"));
            }
        }
        return true;
    }

    private void createTxtFile(File songFolder) {
        File txtFile = new File(songFolder, getSongFilename() + ".txt");
        if (canProceedToWriteFile(txtFile)) {
            appContext.saveSongToFile(txtFile);
            appContext.setActiveFile(txtFile);
        }
    }

    private void copyAudioFile(File songFolder) {
        if (audioFile != null) {
            song.getTagValue(TagKey.MP3).ifPresent(audioFilename -> {
                File newAudioFile = new File(songFolder, audioFilename);
                if (canProceedToWriteFile(newAudioFile)) {
                    try {
                        Files.copy(audioFile.toPath(), newAudioFile.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        LOGGER.warning(I18N.get("creator.copy_audio.fail"));
                        e.printStackTrace();
                    }
                    appContext.loadAudioFile(newAudioFile);
                }
            });
        }
    }

    private boolean canProceedToWriteFile(File file) {
        if (file.exists()) {
            return new OverwriteAlert(file).showAndWait().filter(type -> type == ButtonType.OK)
                    .map(type -> true).orElse(false);
        }
        return true;
    }

    private String getSongFilename() {
        StringBuilder sb = new StringBuilder();
        sb.append(song.getTagValue(TagKey.ARTIST).get());
        sb.append(" - ");
        sb.append(song.getTagValue(TagKey.TITLE).get());
        return sb.toString().replaceAll(ForbiddenCharacterRegex.FOR_FILENAME, "");
    }

}
