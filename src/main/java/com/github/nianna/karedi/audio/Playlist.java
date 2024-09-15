package com.github.nianna.karedi.audio;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.github.nianna.karedi.util.MathUtils;

/**
 * A container for {@link PreloadedAudioFile}s. Only one of them can be currently
 * active.
 */
public class Playlist {
	private final ObservableList<PreloadedAudioFile> audioFiles = FXCollections.observableArrayList();
	private final ObservableList<PreloadedAudioFile> roAudioFiles = FXCollections
			.unmodifiableObservableList(audioFiles);

	private ReadOnlyObjectWrapper<PreloadedAudioFile> activeAudioFile = new ReadOnlyObjectWrapper<>();

	public ObservableList<PreloadedAudioFile> getAudioFiles() {
		return roAudioFiles;
	}

	public void addAudioFile(PreloadedAudioFile file) {
		if (file != null) {
			for (PreloadedAudioFile audioFile : audioFiles) {
				if (audioFile.getFile().equals(file.getFile())) {
					audioFiles.remove(audioFile);
					audioFiles.add(file);
					return;
				}
			}
			audioFiles.add(file);
		}
	}

	public void removeAudioFile(PreloadedAudioFile file) {
		int index = audioFiles.indexOf(file);
		audioFiles.remove(file);
		if (getActiveAudioFile() == file) {
			if (MathUtils.inRange(index, 0, audioFiles.size())) {
				setActiveAudioFile(audioFiles.get(index));
			} else {
				setActiveAudioFile(null);
			}
		}
	}

	public ReadOnlyObjectProperty<PreloadedAudioFile> activeAudioFileProperty() {
		return activeAudioFile.getReadOnlyProperty();
	}

	public final PreloadedAudioFile getActiveAudioFile() {
		return activeAudioFile.get();
	}

	public final void setActiveAudioFile(PreloadedAudioFile file) {
		activeAudioFile.set(file);
	}

	public void clear() {
		audioFiles.clear();
		setActiveAudioFile(null);
	}
}
