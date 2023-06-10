package com.github.nianna.karedi.context;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.KarediApp.ViewMode;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.logging.Logger;

public class AppContext {

	public static final Logger LOGGER = Logger.getLogger(KarediApp.class.getPackage().getName());

	private final ReadOnlyObjectWrapper<ViewMode> activeViewMode = new ReadOnlyObjectWrapper<>(
			KarediApp.getInstance().getViewMode());

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

	public final IOContext ioContext = new IOContext(activeSongContext, audioContext, commandContext);

	public final ActionContext actionContext = new ActionContext(this);

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

}
