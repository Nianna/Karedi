package main.java.com.github.nianna.karedi.song;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.github.nianna.karedi.problem.InvalidNoteLengthProblem;
import main.java.com.github.nianna.karedi.problem.InvalidNoteLyricsProblem;
import main.java.com.github.nianna.karedi.problem.NotNormalizedNoteLyricsProblem;
import main.java.com.github.nianna.karedi.problem.Problem;
import main.java.com.github.nianna.karedi.problem.Problematic;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class NoteChecker implements Problematic {
	private ObservableList<Problem> problems = FXCollections.observableArrayList();
	private final Note note;

	public NoteChecker(Note note) {
		this.note = note;
		note.lyricsProperty().addListener(obs -> onLyricsChanged());
		onLyricsChanged();
		note.lengthProperty().addListener(obs -> onLengthChanged());
		onLengthChanged();
	}

	private void onLyricsChanged() {
		filterOut(InvalidNoteLyricsProblem.TITLE);
		filterOut(NotNormalizedNoteLyricsProblem.TITLE);
		String lyrics = note.getLyrics();
		if (!areLyricsValid(lyrics)) {
			problems.add(new InvalidNoteLyricsProblem(note));
		} else {
			if (!LyricsHelper.normalize(lyrics).equals(lyrics)) {
				problems.add(new NotNormalizedNoteLyricsProblem(note));
			}
		}
	}

	private void onLengthChanged() {
		filterOut(InvalidNoteLengthProblem.TITLE);
		if (note.getLength() < InvalidNoteLengthProblem.MIN_LENGTH) {
			problems.add(new InvalidNoteLengthProblem(note));
		}
	}

	private boolean areLyricsValid(String lyrics) {
		return lyrics != null && LyricsHelper.containsLettersOrDigits(lyrics)
				|| lyrics.contains(LyricsHelper.defaultLyrics());
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return problems;
	}

	private void filterOut(String title) {
		problems.removeIf(problem -> problem.getTitle().equals(title));
	}
	
	
}
