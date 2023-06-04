package com.github.nianna.karedi.problem;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import com.github.nianna.karedi.util.ListenersUtils;

public class ProblemsCombiner implements Problematic {
	private ObservableList<Problem> parentProblems = FXCollections.observableArrayList();
	private ObservableList<Problem> problems = FXCollections.observableArrayList();
	private ObservableList<Problem> unmodifiableProblems = FXCollections
			.unmodifiableObservableList(problems);

	private ListChangeListener<? super Problem> problemListChangeListener;

	public ProblemsCombiner(ObservableList<? extends Problematic> children) {
		problemListChangeListener = ListenersUtils.createListContentChangeListener(problems::add,
				problems::remove);
		children.addListener(ListenersUtils.createListContentChangeListener(child -> {
			child.getProblems().addListener(problemListChangeListener);
			problems.addAll(child.getProblems());
		}, child -> {
			child.getProblems().removeListener(problemListChangeListener);
			problems.removeAll(child.getProblems());
		}));
		parentProblems.addListener(
				ListenersUtils.createListContentChangeListener(problems::add, problems::remove));
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return unmodifiableProblems;
	}

	public boolean add(Problem problem) {
		return parentProblems.add(problem);
	}

	public boolean remove(Problem problem) {
		return parentProblems.remove(problem);
	}

	public boolean removeIf(Predicate<? super Problem> filter) {
		return parentProblems.removeIf(filter);
	}

	public boolean remove(String title) {
		return removeIf(problem -> problem.getTitle().equals(title));
	}

	public void get(String title) {
		problems.stream().filter(problem -> problem.getTitle().equals(title))
				.collect(Collectors.toList());
	}

}