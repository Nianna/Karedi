package com.github.nianna.karedi.context;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.KarediApp.ViewMode;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.logging.Logger;

public class AppContext {

	private static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

	private final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode()
	);

	private final ActiveSongContext activeSongContext = new ActiveSongContext();

	private final SelectionContext selectionContext = new SelectionContext(activeSongContext);

	private final BeatRangeContext beatRangeContext = new BeatRangeContext(activeSongContext);

	private final AudioContext audioContext = new AudioContext(beatRangeContext, activeSongContext);

	private final VisibleAreaContext visibleAreaContext = new VisibleAreaContext(
			activeSongContext,
			beatRangeContext,
			selectionContext,
			audioContext
	);

	private final CommandContext commandContext = new CommandContext(this);

	private final IOContext ioContext = new IOContext(activeSongContext, audioContext, commandContext);

	private final ActionContext actionContext = new ActionContext(this);

	public AppContext() {
		LOGGER.setUseParentHandlers(false);
	}

	public ReadOnlyObjectProperty<ViewMode> activeViewModeProperty() {
		return activeViewMode.getReadOnlyProperty();
	}

	public final ViewMode getActiveViewMode() {
		return activeViewModeProperty().get();
	}

	public void setActiveViewMode(ViewMode viewMode) {
		activeViewMode.set(viewMode);
	}

	public Logger getMainLogger() {
		return LOGGER;
	}

	public ActiveSongContext getActiveSongContext() {
		return activeSongContext;
	}

	public SelectionContext getSelectionContext() {
		return selectionContext;
	}

	public BeatRangeContext getBeatRangeContext() {
		return beatRangeContext;
	}

	public AudioContext getAudioContext() {
		return audioContext;
	}

	public VisibleAreaContext getVisibleAreaContext() {
		return visibleAreaContext;
	}

	public CommandContext getCommandContext() {
		return commandContext;
	}

	public IOContext getIoContext() {
		return ioContext;
	}

	public ActionContext getActionContext() {
		return actionContext;
	}
}
