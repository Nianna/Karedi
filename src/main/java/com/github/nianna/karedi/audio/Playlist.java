package com.github.nianna.karedi.audio;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.github.nianna.karedi.util.MathUtils;

/**
 * A container for {@link CachedAudioFile}s. Only one of them can be currently
 * active.
 */
public class Playlist {
	private final ObservableList<CachedAudioFile> audioFiles = FXCollections.observableArrayList();
	private final ObservableList<CachedAudioFile> roAudioFiles = FXCollections
			.unmodifiableObservableList(audioFiles);

	private ReadOnlyObjectWrapper<CachedAudioFile> activeAudioFile = new ReadOnlyObjectWrapper<>();

	public ObservableList<CachedAudioFile> getAudioFiles() {
		return roAudioFiles;
	}

	public void addAudioFile(CachedAudioFile file) {
		if (file != null) {
			for (CachedAudioFile cachedAudio : audioFiles) {
				if (cachedAudio.getFile().equals(file.getFile())) {
					audioFiles.remove(cachedAudio);
					audioFiles.add(file);
					return;
				}
			}
			audioFiles.add(file);
		}
	}

	public void removeAudioFile(CachedAudioFile file) {
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

	public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
		return activeAudioFile.getReadOnlyProperty();
	}

	public final CachedAudioFile getActiveAudioFile() {
		return activeAudioFile.get();
	}

	public final void setActiveAudioFile(CachedAudioFile file) {
		activeAudioFile.set(file);
	}

	public void clear() {
		audioFiles.clear();
		setActiveAudioFile(null);
	}
}
