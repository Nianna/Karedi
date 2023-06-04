package com.github.nianna.karedi.song;

import javafx.collections.ObservableList;
import com.github.nianna.karedi.problem.IntervalTooSmallProblem;
import com.github.nianna.karedi.problem.NoIntervalBetweenLinesProblem;
import com.github.nianna.karedi.problem.OverlappingLinesProblem;
import com.github.nianna.karedi.problem.Problem;
import com.github.nianna.karedi.problem.Problematic;
import com.github.nianna.karedi.problem.ProblemsCombiner;
import com.github.nianna.karedi.problem.UncommonGoldenBonusProblem;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.MathUtils;

public class SongTrackChecker implements Problematic {
	private SongTrack track;
	private ProblemsCombiner combiner;

	public SongTrackChecker(SongTrack track) {
		this.track = track;
		track.getLines().addListener(ListenersUtils.createListChangeListener(this::refreshLine,
				this::refreshLine, this::refreshLine, this::onLineRemoved));
		combiner = new ProblemsCombiner(track.getLines());
		track.getScoreCounter().addListener(obs -> onScoreCountInvalidated());
		onScoreCountInvalidated();
	}

	private void onLineRemoved(SongLine line) {
		removeLineProblems(line);
		line.getPrevious().ifPresent(prevLine -> findProblems(prevLine));
	}

	private void removeLineProblems(SongLine line) {
		combiner.removeIf(problem -> {
			return problem.getElements().contains(line);
		});
	}

	private void refreshLine(SongLine line) {
		removeLineProblems(line);
		findProblems(line);
		line.getPrevious().ifPresent(prevLine -> findProblems(prevLine));
	}

	private void addIntervalProblems(SongLine line, SongLine nextLine, int interval) {
		if (interval >= IntervalTooSmallProblem.MIN_INTERVAL) {
			return;
		}
		if (interval < 0) {
			add(new OverlappingLinesProblem(line, nextLine));
		} else {
			if (interval == 0) {
				add(new NoIntervalBetweenLinesProblem(line, nextLine));
			} else {
				add(new IntervalTooSmallProblem(line, nextLine));
			}
		}
	}

	private void findProblems(SongLine line) {
		if (line.isValid()) {
			line.getNext().ifPresent(nextLine -> {
				int lineEndBeat = line.getUpperXBound();
				int nextLineStartBeat = nextLine.getLowerXBound();
				int beatInterval = nextLineStartBeat - lineEndBeat;
				addIntervalProblems(line, nextLine, beatInterval);
			});
		}
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return combiner.getProblems();
	}

	private void add(Problem problem) {
		combiner.add(problem);
	}

	private void onScoreCountInvalidated() {
		combiner.remove(UncommonGoldenBonusProblem.TITLE);
		if (!MathUtils.inRange(track.getGoldenBonusPoints(),
				UncommonGoldenBonusProblem.MIN_GOLDEN_BONUS_POINTS,
				UncommonGoldenBonusProblem.MAX_GOLDEN_BONUS_POINTS + 1)) {
			combiner.add(new UncommonGoldenBonusProblem(track));
		}
	}

}
