package com.github.nianna.karedi.context;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.value.ChangeListener;
import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.audio.Player.Status;
import com.github.nianna.karedi.util.BeatMillisConverter;

public class Marker {
	private final ReadOnlyLongWrapper markerTime = new ReadOnlyLongWrapper();
	private final ReadOnlyIntegerWrapper markerBeat = new ReadOnlyIntegerWrapper();
	private Player player;
	private BeatMillisConverter converter;
	private ChangeListener<? super Player.Status> statusListener = this::onPlayerStatusChanged;
	private long lastMarkerPosition;

	public Marker(BeatMillisConverter converter, Player player) {
		setConverter(converter);
		setPlayer(player);
	}

	public Marker(Player player) {
		setPlayer(player);
	}

	public void setPlayer(Player player) {
		if (player != null) {
			player.statusProperty().removeListener(statusListener);
		}
		this.player = player;
		player.statusProperty().addListener(statusListener);
	}

	public void setConverter(BeatMillisConverter converter) {
		this.converter = converter;
		markerBeat.unbind();
		markerBeat.bind(Bindings.createIntegerBinding(() -> {
			return converter.millisToBeat(getTime());
		}, markerTime, converter));
	}

	public ReadOnlyIntegerProperty beatProperty() {
		return markerBeat.getReadOnlyProperty();
	}

	public final int getBeat() {
		return markerBeat.get();
	}

	public final void setBeat(int beat) {
		setTime(converter.beatToMillis(beat));
	}

	public ReadOnlyLongProperty timeProperty() {
		return markerTime.getReadOnlyProperty();
	}

	public final Long getTime() {
		return markerTime.get();
	}

	public final void setTime(long time) {
		markerTime.unbind();
		player.stop();
		markerTime.set(time);
	}

	private void onPlayerStatusChanged(Observable obs, Status oldStatus, Status newStatus) {
		if (newStatus == Status.PLAYING) {
			lastMarkerPosition = getTime();
			markerTime.bind(player.currentTimeProperty());
		} else {
			if (markerTime.isBound()) {
				markerTime.unbind();
				markerTime.set(lastMarkerPosition);
			}
		}
	}
}
