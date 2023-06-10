package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.KarediApp.ViewMode;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.txt.TxtFacade;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.io.File;
import java.util.logging.Logger;

public class AppContext {
	public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

	private final ReadOnlyObjectWrapper<File> activeFile = new ReadOnlyObjectWrapper<>();
	public final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode());

	public final TxtFacade txtFacade = new TxtFacade();

	private File directory;

	// Convenience bindings for actions
	public final BooleanBinding activeFileIsNull = activeFileProperty().isNull();

	public final ActiveSongContext activeSongContext = new ActiveSongContext();

	public final SelectionContext selectionContext = new SelectionContext(activeSongContext);

	public final BeatRangeContext beatRangeContext = new BeatRangeContext(activeSongContext);

	public final AudioContext audioContext = new AudioContext(beatRangeContext, activeSongContext);

	public final VisibleAreaContext visibleAreaContext = new VisibleAreaContext(
			activeSongContext,
			beatRangeContext,
			selectionContext,
			audioContext
	);

	public final CommandContext commandContext = new CommandContext(this);

	public final ActionContext actionContext = new ActionContext(this);

	public AppContext() {
		LOGGER.setUseParentHandlers(false);
		actionContext.initActions();
	}

	// Files
	public ReadOnlyObjectProperty<File> activeFileProperty() {
		return activeFile.getReadOnlyProperty();
	}

	public File getActiveFile() {
		return activeFileProperty().get();
	}

	public void setActiveFile(File file) {
		this.activeFile.set(file);
		KarediApp.getInstance().updatePrimaryStageTitle(file);
		directory = file == null ? null : file.getParentFile();
	}

	public File getDirectory() {
		return directory;
	}

	public void loadSongFile(File file) {
		loadSongFile(file, true);
	}

	public void loadSongFile(File file, boolean resetPlayer) {
		if (file != null) {
			reset(resetPlayer);
			setActiveFile(file);
			Song song = txtFacade.loadFromTxtFile(file);
			song.getTagValue(TagKey.MP3).ifPresent(audioFileName -> {
				audioContext.loadAudioFile(new File(file.getParent(), audioFileName));
			});
			activeSongContext.setSong(song);
			LOGGER.info(I18N.get("load.success"));
		}
	}

	public boolean saveSongToFile(File file) {
		if (file != null) {
			if (txtFacade.saveSongToFile(file, activeSongContext.getSong())) {
				commandContext.updateLastSavedCommand();
				return true;
			}
		}
		return false;
	}

	public ReadOnlyObjectProperty<ViewMode> activeViewModeProperty() {
		return activeViewMode.getReadOnlyProperty();
	}

	public final ViewMode getActiveViewMode() {
		return activeViewModeProperty().get();
	}

	// Other
	public boolean needsSaving() {
		return activeSongContext.getSong() != null && commandContext.hasUnsavedCommands();
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	public void reset(boolean resetPlayer) {
		activeSongContext.setSong(null);
		commandContext.reset();
		visibleAreaContext.reset();
		if (resetPlayer) {
			audioContext.reset();
		}
	}

}
