package com.github.nianna.karedi.problem;

import java.util.Arrays;
import java.util.Optional;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.MoveOneCommand;
import com.github.nianna.karedi.command.ResizeNotesCommand;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Note;

class NoteIntervalProblemSolver implements Solvable {
	private final Note first;
	private final Note second;
	private final int requestedInterval;

	NoteIntervalProblemSolver(Note first, Note second, int requestedInterval) {
		this.first = first;
		this.second = second;
		this.requestedInterval = requestedInterval;
	}

	@Override
	public Optional<Command> getSolution() {
		if (first != null && (first.getLength()
				- lackingBeatCount()) >= InvalidNoteLengthProblem.MIN_LENGTH) {
			return Optional.of(new ResizeNotesCommand(Arrays.asList(first), Direction.RIGHT,
					-lackingBeatCount()));
		}
		return Optional.empty();
	}

	@Override
	public Optional<Command> getInvasiveSolution() {
		Optional<Command> cmd = getSolution();
		if (!cmd.isPresent() && first != null && second != null) {
			return Optional.of(new CommandComposite(I18N.get("problem_solver.interval.correct")) {
				@Override
				protected void buildSubCommands() {
					int beatsLeft = lackingBeatCount();
					int shortenFirstBy = Math
							.max(first.getLength() - InvalidNoteLengthProblem.MIN_LENGTH, 0);
					if (shortenFirstBy > 0) {
						addSubCommand(new ResizeNotesCommand(Arrays.asList(first), Direction.RIGHT,
								-shortenFirstBy));
					}
					beatsLeft -= shortenFirstBy;
					int newSecondNoteLength = Math.max(second.getLength() - beatsLeft,
							InvalidNoteLengthProblem.MIN_LENGTH);
					int shortenSecondBy = Math.max(second.getLength() - newSecondNoteLength, 0);
					if (shortenSecondBy > 0) {
						addSubCommand(new ResizeNotesCommand(Arrays.asList(second), Direction.LEFT,
								-shortenSecondBy));
					}
					beatsLeft -= shortenSecondBy;
					if (beatsLeft > 0) {
						addSubCommand(new MoveOneCommand<>(second, Direction.RIGHT, beatsLeft));
					}
				}

			});
		}
		return cmd;
	}

	private int lackingBeatCount() {
		return requestedInterval - (second.getLowerXBound() - first.getUpperXBound());
	}

}
