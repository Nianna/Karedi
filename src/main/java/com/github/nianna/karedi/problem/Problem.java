package com.github.nianna.karedi.problem;

import java.util.List;
import java.util.Optional;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.SongTrack;

public abstract class Problem implements Solvable {
	public enum Severity {
		ERROR,
		WARNING
	}

	private final Severity severity;
	private Solvable solver;
	private final String title;
	private String description;

	public Problem(Severity severity, String title) {
		this.severity = severity;
		this.title = title;
	}

	public Problem(Severity severity, String title, Solvable solver) {
		this(severity, title);
		setSolver(solver);
	}

	public final Severity getSeverity() {
		return severity;
	}

	public abstract List<? extends Object> getElements();

	public abstract Optional<? extends IntBounded> getAffectedBounds();

	@Override
	public Optional<Command> getSolution() {
		if (solver != null) {
			return rename(solver.getSolution(), false);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Command> getInvasiveSolution() {
		if (solver != null) {
			return rename(solver.getInvasiveSolution(), true);
		}
		return rename(getSolution(), true);
	}

	public void setSolver(Solvable solver) {
		this.solver = solver;
	}

	public final String getTitle() {
		return title;
	}

	protected Optional<Command> rename(Optional<Command> optCmd, boolean invasive) {
		String cmdTitle = I18N
				.get(invasive ? "command.correct_problem_invasive" : "command.correct_problem", getTitle());
		optCmd.ifPresent(cmd -> cmd.setTitle(cmdTitle));
		return optCmd;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public Optional<String> getDescription() {
		return Optional.ofNullable(description);
	}

	public boolean equals(Problem problem) {
		return getTitle().equals(problem.getTitle());
	}

	@Override
	public String toString() {
		return getTitle();
	}

	public final boolean hasSolution() {
		return getSolution().isPresent();
	}

	public final boolean hasInvasiveSolution() {
		return getInvasiveSolution().isPresent();
	}

	public abstract Optional<SongTrack> getTrack();

}
