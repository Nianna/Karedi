package com.github.nianna.karedi.display;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.BeatRangeContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.MathUtils;
import com.github.nianna.karedi.util.MusicalScale;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Tooltip;
import javafx.stage.WindowEvent;

class NoteTooltip extends Tooltip {

	private final BeatRangeContext beatRangeContext;

	private final Note note;

	private final NoteTooltipDisplayer displayer = new NoteTooltipDisplayer();

	NoteTooltip(AppContext appContext, Note note) {
		this.beatRangeContext = appContext.getBeatRangeContext();
		this.note = note;
		setOnShowing(this::onShowing);
		setOnHidden(this::onHidden);
		setGraphic(displayer);
	}

	private void onShowing(WindowEvent event) {
		displayer.lyricsProperty().bind(note.lyricsProperty());
		displayer.toneProperty().bind(Bindings.createStringBinding(() -> {
			return MusicalScale.getNote(note.getTone()) + " (" + note.getTone() + ")";
		}, note.toneProperty()));
		displayer.lengthProperty().bind(note.lengthProperty().asString());
		displayer.startBeatProperty().bind(note.startProperty().asString());
		displayer.startTimeProperty().bind(Bindings.createStringBinding(() -> {
			double startTime = MathUtils.msToSeconds(beatRangeContext.beatToMillis(note.getStart()));
			if (startTime >= 0) {
				return startTime + " s";
			}
			return "?";
		}, note.startProperty()));
	}

	private void onHidden(WindowEvent event) {
		displayer.lyricsProperty().unbind();
		displayer.toneProperty().unbind();
		displayer.lengthProperty().unbind();
		displayer.startBeatProperty().unbind();
		displayer.startTimeProperty().unbind();
	}
}
