package com.github.nianna.karedi.controller;

import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.ListenersUtils;

public class LineNumberController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private Text label;

	private AppContext appContext;
	private ListChangeListener<? super SongLine> lineListChangeListener;

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;

		lineListChangeListener = ListenersUtils.createListChangeListener(line -> updateLabel(),
				ListenersUtils::pass, ListenersUtils::pass, ListenersUtils::pass);

		appContext.activeLineProperty().addListener(obs -> updateLabel());
		appContext.activeTrackProperty().addListener(this::onTrackChanged);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private String getActiveLineNumber() {
		if (appContext.getActiveTrack() != null && appContext.getActiveLine() != null) {
			return (appContext.getActiveTrack().indexOf(appContext.getActiveLine()) + 1) + "";
		}
		return "";
	}

	private void onTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		if (oldTrack != null) {
			oldTrack.removeLineListListener(lineListChangeListener);
		}
		if (newTrack != null) {
			newTrack.addLineListListener(lineListChangeListener);
		}
	}

	private void updateLabel() {
		label.setText(getActiveLineNumber());
	}
}
