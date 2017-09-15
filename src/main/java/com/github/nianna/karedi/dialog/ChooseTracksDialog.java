package main.java.com.github.nianna.karedi.dialog;

import java.util.List;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class ChooseTracksDialog extends CheckListViewDialog<SongTrack> {

	public ChooseTracksDialog(List<? extends SongTrack> items, int count) {
		super(items, SongTrack::getName);
		setUpperLimit(count);
		setLowerLimit(count);

		setTitle(I18N.get("dialog.track_export.title"));
		setHeaderText(I18N.get("dialog.track_export.header", count));
	}
}
