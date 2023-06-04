package com.github.nianna.karedi.problem;

import java.util.Optional;

import com.github.nianna.karedi.command.Command;

public interface Solvable {
	Optional<Command> getSolution();
	Optional<Command> getInvasiveSolution();
}
