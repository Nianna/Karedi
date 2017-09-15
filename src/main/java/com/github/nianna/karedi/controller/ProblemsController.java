package main.java.com.github.nianna.karedi.controller;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.controlsfx.glyphfont.Glyph;

import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.event.StateEvent;
import main.java.com.github.nianna.karedi.event.StateEvent.State;
import main.java.com.github.nianna.karedi.problem.Problem;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.util.BindingsUtils;
import main.java.com.github.nianna.karedi.util.ListenersUtils;

public class ProblemsController implements Controller {
	@FXML
	private AnchorPane pane;
	@FXML
	private TreeView<TreeViewChild> tree;
	@FXML
	private TreeItem<TreeViewChild> errorsRoot;
	@FXML
	private TreeItem<TreeViewChild> warningsRoot;

	@FXML
	private Glyph errorGlyph;
	@FXML
	private Glyph warningGlyph;

	private AppContext appContext;
	private ListChangeListener<? super Problem> problemListChangeListener;

	private IntegerBinding errorsCount;
	private IntegerBinding warningsCount;

	@FXML
	public void initialize() {
		errorGlyph.setColor(Color.RED);
		errorGlyph.useGradientEffect();
		warningGlyph.setColor(Color.YELLOW);
		warningGlyph.useGradientEffect();

		tree.setCellFactory(params -> new ProblemTreeCell());

		errorsCount = BindingsUtils.sizeOf(errorsRoot.getChildren());
		warningsCount = BindingsUtils.sizeOf(warningsRoot.getChildren());

		errorsCount.addListener(obs -> updateErrorsLabel());
		updateErrorsLabel();
		warningsCount.addListener(obs -> updateWarningsLabel());
		updateWarningsLabel();

		problemListChangeListener = ListenersUtils
				.createListContentChangeListener(this::onProblemAdded, this::onProblemRemoved);
	}

	private void updateErrorsLabel() {
		errorsRoot
				.setValue(new TreeViewChild(I18N.get("problems.errors.header", errorsCount.get())));
	}

	private void updateWarningsLabel() {
		warningsRoot.setValue(
				new TreeViewChild(I18N.get("problems.warnings.header", warningsCount.get())));
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		appContext.activeSongProperty().addListener(this::onSongChanged);

		tree.getSelectionModel().selectedItemProperty().addListener(this::onSelectionInvalidated);
	}

	@Override
	public Node getContent() {
		return pane;
	}

	private void onSelectionInvalidated(Observable obs) {
		if (tree.getSelectionModel().getSelectedItem() != null) {
			TreeViewChild child = tree.getSelectionModel().getSelectedItem().getValue();
			child.getProblem().ifPresent(problem -> selectAffectedBounds(problem));
		}
	}

	private void selectAffectedBounds(Problem problem) {
		problem.getTrack().ifPresent(appContext::setActiveTrack);
		problem.getAffectedBounds().ifPresent(bounds -> {
			if (bounds.isValid()) {
				List<Note> affectedNotes = appContext.getActiveTrack()
						.getNotes(bounds.getLowerXBound(), bounds.getUpperXBound());
				if (affectedNotes.size() > 0) {
					SongLine line = affectedNotes.get(0).getLine();
					if (line != null
							&& line == affectedNotes.get(affectedNotes.size() - 1).getLine()) {
						appContext.setActiveLine(line);
					}
				}
				appContext.getSelection().set(affectedNotes);
			}
		});
	}

	private void onProblemAdded(Problem problem) {
		switch (problem.getSeverity()) {
		case ERROR:
			errorsRoot.getChildren().add(createItemForProblem(problem));
			break;
		case WARNING:
			warningsRoot.getChildren().add(createItemForProblem(problem));
		}
		onStateInvalidated();
	}

	private void onProblemRemoved(Problem problem) {
		getItemForProblem(problem).ifPresent(item -> {
			TreeItem<TreeViewChild> selectedItem = tree.getSelectionModel().getSelectedItem();
			if (item.equals(selectedItem)) {
				tree.getSelectionModel().clearSelection();
			}
			errorsRoot.getChildren().remove(item);
			warningsRoot.getChildren().remove(item);
		});
		onStateInvalidated();
	}

	private TreeItem<TreeViewChild> createItemForProblem(Problem problem) {
		return getItemForProblem(problem).orElse(new TreeItem<>(new TreeViewChild(problem)));
	}

	private Optional<TreeItem<TreeViewChild>> getItemForProblem(Problem problem) {
		Predicate<? super TreeItem<TreeViewChild>> sameProblem = item -> item.getValue()
				.getProblem().orElse(null) == problem;
		switch (problem.getSeverity()) {
		case ERROR:
			return errorsRoot.getChildren().stream().filter(sameProblem).findAny();
		case WARNING:
			return warningsRoot.getChildren().stream().filter(sameProblem).findAny();
		}
		return Optional.empty();
	}

	private void onSongChanged(Observable obs, Song oldSong, Song newSong) {
		if (oldSong != null) {
			oldSong.getProblems().removeListener(problemListChangeListener);
			oldSong.getProblems().forEach(this::onProblemRemoved);
		}
		if (newSong != null) {
			newSong.getProblems().addListener(problemListChangeListener);
			newSong.getProblems().forEach(this::onProblemAdded);
		}
		onStateInvalidated();
		tree.setDisable(newSong == null);

	}

	private void onStateInvalidated() {
		if (errorsCount.get() > 0) {
			tree.fireEvent(new StateEvent(State.HAS_ERRORS));
		} else {
			if (warningsCount.get() > 0) {
				tree.fireEvent(new StateEvent(State.HAS_WARNINGS));
			} else {
				tree.fireEvent(new StateEvent(State.NO_PROBLEMS));
			}
		}
	}

	private class TreeViewChild {
		private String text;
		private Problem problem;

		private TreeViewChild(String text) {
			this.text = text;
		}

		private TreeViewChild(Problem problem) {
			this(problem.toString());
			this.problem = problem;
		}

		@Override
		public String toString() {
			return text;
		}

		public Optional<Problem> getProblem() {
			return Optional.ofNullable(problem);
		}

	}

	private final class ProblemTreeCell extends TreeCell<TreeViewChild> {

		private ContextMenu contextMenu = new ContextMenu();
		private Tooltip tooltip = new Tooltip();
		private MenuItem correctMenuItem = new MenuItem(I18N.get("problems.correct"));
		private MenuItem correctInvasiveMenuItem = new MenuItem(I18N.get("problems.correct_hard"));

		private ProblemTreeCell() {
			contextMenu.getItems().addAll(correctMenuItem, correctInvasiveMenuItem);
		}

		@Override
		protected void updateItem(TreeViewChild item, boolean empty) {
			super.updateItem(item, empty);
			setTooltip(null);
			setContextMenu(null);
			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				setText(getItem().toString());
				setGraphic(getTreeItem().getGraphic());
				item.getProblem().ifPresent(this::addTooltip);
				addContextMenu(item);
			}
		}

		private void addTooltip(Problem problem) {
			tooltip.setText(null);
			problem.getDescription().ifPresent(tooltip::setText);
			setTooltip(tooltip);
		}

		private void addContextMenu(TreeViewChild child) {
			if (child.getProblem().isPresent()) {
				addContextMenu(child.getProblem().get());
			} else {
				addRootContextMenu();
			}
		}

		private void addContextMenu(Problem problem) {
			contextMenu.setOnShowing(event -> {
				correctMenuItem.setDisable(!problem.hasSolution());
				correctInvasiveMenuItem.setDisable(!problem.hasInvasiveSolution());
			});
			correctMenuItem.setOnAction(event -> {
				executeSolution(problem);
			});
			correctInvasiveMenuItem.setOnAction(event -> {
				problem.getInvasiveSolution().ifPresent(appContext::execute);
			});
			setContextMenu(contextMenu);
		}

		private void addRootContextMenu() {
			contextMenu.setOnShowing(event -> {
				correctMenuItem
						.setDisable(!getTreeViewChildren().stream().anyMatch(this::hasSolution));
				correctInvasiveMenuItem.setDisable(
						!getTreeViewChildren().stream().anyMatch(this::hasInvasiveSolution));
			});
			correctMenuItem.setOnAction(event -> {
				getTreeViewChildren().forEach(child -> {
					child.getProblem().ifPresent(this::executeSolution);
				});
			});
			correctInvasiveMenuItem.setOnAction(event -> {
				getTreeViewChildren().forEach(child -> {
					child.getProblem().ifPresent(this::executeInvasiveSolution);
				});
			});
			setContextMenu(contextMenu);
		}

		private boolean hasInvasiveSolution(TreeViewChild child) {
			return child.getProblem().map(Problem::hasInvasiveSolution).orElse(false);
		}

		private boolean hasSolution(TreeViewChild child) {
			return child.getProblem().map(Problem::hasSolution).orElse(false);
		}

		private List<TreeViewChild> getTreeViewChildren() {
			return getTreeItem().getChildren().stream().map(TreeItem::getValue)
					.collect(Collectors.toList());
		}

		private void executeSolution(Problem problem) {
			problem.getSolution().ifPresent(cmd -> solveProblemWithCommand(problem, cmd));
		}

		private void executeInvasiveSolution(Problem problem) {
			problem.getInvasiveSolution().ifPresent(cmd -> solveProblemWithCommand(problem, cmd));
		}

		private void solveProblemWithCommand(Problem problem, Command command) {
			tree.getSelectionModel().clearSelection();
			selectAffectedBounds(problem);
			appContext.execute(command);
			assertChildIsSelected();
		}

		private void assertChildIsSelected() {
			if ((getSelectedItem() == errorsRoot && errorsCount.get() > 0)
					|| getSelectedItem() == warningsRoot && warningsCount.get() > 0) {
				tree.getSelectionModel().selectNext();
			}
		}

		private TreeItem<TreeViewChild> getSelectedItem() {
			return tree.getSelectionModel().getSelectedItem();
		}

	}

}
