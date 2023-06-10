package com.github.nianna.karedi.controller;

import static org.fxmisc.wellbehaved.event.InputMap.consume;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.command.AddNewSyllableCommand;
import com.github.nianna.karedi.command.AddNewWordCommand;
import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.DeleteTextCommand;
import com.github.nianna.karedi.command.InsertTextCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.NoteSelection;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.KeyCodeCombinations;
import com.github.nianna.karedi.util.KeyEventUtils;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.LyricsHelper;

public class LyricsEditorController implements Controller {
	private static final int TIME_LIMIT_MS = 30;

	@FXML
	private AnchorPane pane;

	private VirtualizedScrollPane<CodeArea> scrollPane;
	private NoteTextArea textArea;

	private AppContext appContext;
	private NoteSelection selection;
	private TextAndNoteSelectionSynchronizer synchronizer;

	private ChangeListener<? super String> lyricsChangeListener = (obs, oldVal,
			newVal) -> scheduleLyricsUpdate();
	private ChangeListener<? super Type> typeChangeListener = (obs, oldVal, newVal) -> textArea
			.applyHighlightning();
	private ListChangeListener<? super SongLine> lineListChangeListener;
	private ListChangeListener<? super Note> noteListChangeListener;

	private Timer lyricsUpdateTimer = new Timer(true);

	@FXML
	public void initialize() {
		initTextArea();
		initScrollPane();
	}

	private void initTextArea() {
		textArea = new NoteTextArea();
		textArea.getStyleClass().add("text-area");
		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));

		textArea.addEventFilter(KeyEvent.KEY_PRESSED, this::keyPressedFilter);
		textArea.addEventFilter(KeyEvent.KEY_TYPED, this::keyTypedFilter);
		Nodes.addInputMap(textArea, consume(KeyEvent.KEY_TYPED, this::onKeyTyped));
	}

	private void initScrollPane() {
		scrollPane = new VirtualizedScrollPane<>(textArea);
		pane.getChildren().add(scrollPane);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

		AnchorPane.setTopAnchor(scrollPane, 0.0);
		AnchorPane.setBottomAnchor(scrollPane, 0.0);
		AnchorPane.setLeftAnchor(scrollPane, 0.0);
		AnchorPane.setRightAnchor(scrollPane, 0.0);
	}

	private void keyPressedFilter(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ALT_GRAPH)) {
			event.consume();
			return;
		}
		onKeyPressed(event);
	}

	private void keyTypedFilter(KeyEvent event) {
		// pass legal characters if Shortcut isn't down, allow combinations with
		// AltGr
		if (!isLegal(event.getCharacter()) || (event.isShortcutDown() && !event.isAltDown())) {
			event.consume();
		}
	};

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		this.selection = appContext.selectionContext.getSelection();
		appContext.activeSongContext.activeTrackProperty().addListener(this::onTrackChanged);
		scrollPane.disableProperty().bind(appContext.activeSongContext.activeTrackIsNullBinding());

		appContext.actionContext.addAction(KarediActions.INSERT_MINUS, new InsertTextAction(NoteTextArea.MINUS));
		appContext.actionContext.addAction(KarediActions.INSERT_SPACE, new InsertTextAction(NoteTextArea.SPACE));

		lineListChangeListener = ListenersUtils.createListChangeListener(
				e -> scheduleLyricsUpdate(), ListenersUtils::pass, e -> scheduleLyricsUpdate(),
				e -> scheduleLyricsUpdate());
		noteListChangeListener = ListenersUtils.createListChangeListener(
				e -> scheduleLyricsUpdate(), ListenersUtils::pass, this::onNoteAdded,
				this::onNoteRemoved);

		synchronizer = new TextAndNoteSelectionSynchronizer(textArea, selection,
				this::preNoteSelectionChangeHandler);
	}

	private void preNoteSelectionChangeHandler(Note first, Note last) {
		if (first.getLine().equals(last.getLine())) {
			appContext.activeSongContext.setActiveLine(first.getLine());
		}
	}

	@Override
	public Node getContent() {
		return pane;
	}

	@Override
	public Node getFocusableContent() {
		return textArea;
	}

	private void onNoteAdded(Note note) {
		addListenersToNote(note);
		scheduleLyricsUpdate();
	}

	private void onNoteRemoved(Note note) {
		removeListenersFromNote(note);
		scheduleLyricsUpdate();
	}

	private void onTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		if (oldTrack != null) {
			oldTrack.removeLineListListener(lineListChangeListener);
			oldTrack.removeNoteListListener(noteListChangeListener);
			oldTrack.getNotes().forEach(this::removeListenersFromNote);
		}
		if (newTrack != null) {
			newTrack.addLineListListener(lineListChangeListener);
			newTrack.addNoteListListener(noteListChangeListener);
			newTrack.getNotes().forEach(this::addListenersToNote);
		}
		textArea.moveTo(0);
		lyricsUpdateTimer.cancel();
		synchronizer.freeze();
		textArea.setTrack(newTrack);
		synchronizer.unfreeze();
		textArea.applyHighlightning();
	}

	private void addListenersToNote(Note note) {
		note.lyricsProperty().addListener(lyricsChangeListener);
		note.typeProperty().addListener(typeChangeListener);
	}

	private void removeListenersFromNote(Note note) {
		note.lyricsProperty().removeListener(lyricsChangeListener);
		note.typeProperty().removeListener(typeChangeListener);
	}

	private void insertText(String text) {
		int startPos = textArea.getSelection().getStart();
		int endPos = textArea.getSelection().getEnd();

		text = filterOutIllegalChars(text);
		if (isLegal(text)) {
			replaceText(startPos, endPos, text);
			textArea.replaceSelection(text);
			textArea.requestFollowCaret();
		}
	}

	private void onKeyTyped(KeyEvent event) {
		int startPos = textArea.getSelection().getStart();
		textArea.noteAt(startPos).ifPresent(note -> {
			int offset = textArea.getOffsetWithinNote(note, startPos);

			String character = event.getCharacter();
			switch (character) {
			case "-":
				if (!KeyEventUtils.isAnyModifierDown(event)) {
					deleteSelectionAndExecute(new AddNewSyllableCommand(note, offset));
					textArea.moveTo(startPos + 1);
				}
				event.consume();
				return;
			case " ":
				if (!KeyEventUtils.isAnyModifierDown(event)) {
					deleteSelectionAndExecute(new AddNewWordCommand(note, offset));
					textArea.moveTo(startPos + 1);
				}
				event.consume();
				return;
			}
			insertText(event.getCharacter());
		});
		event.consume();
	}

	private static boolean isLegal(String text) {
		return text.codePoints().allMatch(LyricsEditorController::isLegal);
	}

	private static boolean isLegal(int codePoint) {
		return !Character.isISOControl(codePoint);
	}

	private static String filterOutIllegalChars(String text) {
		return text.codePoints().filter(LyricsEditorController::isLegal)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	private void removeText(boolean beforeCaret) {
		int startPos = textArea.getSelection().getStart();
		int endPos = textArea.getSelection().getEnd();
		if (startPos == endPos) {
			if (beforeCaret) {
				--startPos;
			} else {
				++endPos;
			}
		}
		replaceText(startPos, endPos, "");
	}

	private void replaceText(int startPos, int endPos, String newText) {
		textArea.noteAt(startPos).ifPresent(firstNote -> {
			int firstOffset = textArea.getOffsetWithinNote(firstNote, startPos);
			if (newText != null && newText.length() > 0) {
				deleteRangeAndExecute(startPos, endPos, new InsertTextCommand(firstNote,
						firstOffset, NoteTextArea.denormalize(newText)));
			} else {
				deleteRangeAndExecute(startPos, endPos, (String) null);
			}
		});
	}

	private void deleteSelectionAndExecute(Command cmd) {
		deleteRangeAndExecute(textArea.getSelection().getStart(), textArea.getSelection().getEnd(),
				cmd);
	}

	private void deleteSelectionAndExecute(String title) {
		deleteRangeAndExecute(textArea.getSelection().getStart(), textArea.getSelection().getEnd(),
				null, title);
	}

	private void deleteRangeAndExecute(int startPos, int endPos, Command cmd) {
		deleteRangeAndExecute(startPos, endPos, cmd, cmd.getTitle());
	}

	private void deleteRangeAndExecute(int startPos, int endPos, String title) {
		deleteRangeAndExecute(startPos, endPos, null, title);
	}

	private void deleteRangeAndExecute(int startPos, int endPos, Command cmd, String title) {
		Note first = textArea.noteAt(startPos).orElse(null);
		Note last = textArea.noteAt(endPos).orElse(null);
		if (first != null && last != null) {
			int firstOffset = textArea.getOffsetWithinNote(first, startPos);
			int lastOffset = textArea.getOffsetWithinNote(last, endPos);

			Command deleteCmd = new DeleteTextCommand(first, firstOffset, last, lastOffset);
			Command finalCmd;
			if (cmd != null) {
				finalCmd = new CommandComposite(cmd.getTitle()) {
					@Override
					protected void buildSubCommands() {
						addSubCommand(deleteCmd);
						addSubCommand(cmd);
					}
				};
			} else {
				finalCmd = deleteCmd;
			}
			if (title != null) {
				finalCmd.setTitle(title);
			}
			appContext.commandContext.execute(new ChangePostStateCommandDecorator(finalCmd, c -> {
				appContext.selectionContext.getSelection().selectOnly(first);
			}));
		}
	}

	private boolean wasActionFired(KarediActions action, KeyEvent event) {
		return appContext.actionContext.getAction(action).wasFired(event);
	}

	private KarediActions getFiredAction(KeyEvent event) {
		KarediActions[] legalActions = { KarediActions.INSERT_MINUS, KarediActions.INSERT_SPACE,
				KarediActions.SAVE, KarediActions.RELOAD, KarediActions.EDIT_LYRICS,
				KarediActions.UNDO, KarediActions.REDO };
		for (int i = 0; i < legalActions.length; ++i) {
			if (wasActionFired(legalActions[i], event)) {
				return legalActions[i];
			}
		}
		return null;
	}

	private void executeAction(KarediActions action) {
		appContext.actionContext.execute(action);
	}

	private void onKeyPressed(KeyEvent event) {
		KarediActions action = getFiredAction(event);
		if (action != null) {
			executeAction(action);
			event.consume();
			return;
		}

		if (KeyCodeCombinations.CTRL_X.match(event)) {
			deleteSelectionAndExecute("Cut text");
			return; // do not consume
		}

		if (KeyCodeCombinations.CTRL_V.match(event)) {
			paste();
			event.consume();
			return;
		}

		if (wasActionFired(KarediActions.TOGGLE_LINEBREAK, event)) {
			textArea.selectNone();
			executeAction(KarediActions.TOGGLE_LINEBREAK);
			event.consume();
			return;
		}

		if (event.getCode().equals(KeyCode.BACK_SPACE)) {
			removeText(true);
			return; // do not consume
		}

		if (event.getCode().equals(KeyCode.DELETE)) {
			removeText(false);
			return; // do not consume
		}

		if (!leaveDefaultBehaviour(event.getCode())) {
			event.consume();
		}
	}

	private boolean leaveDefaultBehaviour(KeyCode code) {
		switch (code) {
		// Navigation
		case LEFT:
		case KP_LEFT:
		case RIGHT:
		case KP_RIGHT:
		case DOWN:
		case KP_DOWN:
		case UP:
		case KP_UP:
		case HOME:
		case END:
		case PAGE_DOWN:
		case PAGE_UP:
		case A:
			// Copying
		case C:
		case INSERT:
		case COPY:
			return true;
		default:
			return false;
		}
	}

	private void paste() {
		String copiedText = Clipboard.getSystemClipboard().getString();
		if (copiedText != null) {
			String filteredText = filterOutIllegalChars(copiedText.replaceAll("\\R", " "));
			int startPos = textArea.getSelection().getStart();
			textArea.noteAt(startPos).ifPresent(note -> {
				String[] parts = splitTextIntoParts(filteredText);
				for (int i = 0; i < parts.length; ++i) {
					parts[i] = NoteTextArea.denormalize(parts[i]);
				}
				deleteSelectionAndExecute(new InsertTextCommand(note,
						textArea.getOffsetWithinNote(note, startPos), parts));
				textArea.selectRange(startPos + filteredText.length(),
						startPos + filteredText.length());
			});
		}
	}

	private String[] splitTextIntoParts(String text) {
		List<String> parts = new ArrayList<>();
		Matcher matcher = Pattern.compile("[ -]").matcher(text);
		int beginIndex = 0;
		while (matcher.find()) {
			parts.add(text.substring(beginIndex, matcher.start()));
			if (matcher.group().equals(" ")) {
				beginIndex = matcher.start();
			} else {
				beginIndex = matcher.end();
			}

		}
		parts.add(text.substring(beginIndex));
		return parts.toArray(new String[0]);
	}

	private void scheduleLyricsUpdate() {
		textArea.invalidate();
		lyricsUpdateTimer.cancel();
		lyricsUpdateTimer = new Timer(true);
		lyricsUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					synchronizer.freeze();
					boolean changed = textArea.setTrack(appContext.activeSongContext.getActiveTrack());
					if (changed) {
						if (!textArea.isFocused()) {
							synchronizer.updateTextSelection();
						}
					}
					textArea.applyHighlightning();
					synchronizer.unfreeze();
				});
			}

			@Override
			public boolean cancel() {
				return super.cancel();
			}
		}, TIME_LIMIT_MS);
	}

	private static class NoteTextArea extends CodeArea {
		private static final String MINUS = "\u2043";
		private static final String SPACE = "\u00B7";

		private SongTrack track;
		private int lastKwEnd = 0;

		private ReadOnlyBooleanWrapper invalid = new ReadOnlyBooleanWrapper();

		private void invalidate() {
			invalid.set(true);
		}

		private ReadOnlyBooleanProperty invalidProperty() {
			return invalid.getReadOnlyProperty();
		}

		private boolean isInvalid() {
			return invalid.get();
		}

		private IndexRange noteRange(Note note) {
			int paragraphIndex = getParagraph(note);
			return new IndexRange(getAbsolutePosition(paragraphIndex, getColumn(note, false)),
					getAbsolutePosition(paragraphIndex, getColumn(note, true)));
		}

		private void selectNone() {
			selectRange(getAnchor(), getAnchor());
		}

		private int getParagraph(Note note) {
			return track.indexOf(note.getLine());
		}

		private int getOffsetWithinNote(Note note, int pos) {
			int offset = pos - noteRange(note).getStart();
			if (LyricsHelper.startsNewWord(note.getLyrics())) {
				++offset;
			}
			offset = Math.max(0, offset);
			offset = Math.min(note.getLyrics().length(), offset);
			return offset;
		}

		private int getColumn(Note note, boolean include) {
			int syllableIndex = note.getLine().indexOf(note);
			String lineText = getParagraph(getParagraph(note)).getText();
			int i = 0;
			Matcher matcher = Pattern.compile("[ -]").matcher(lineText);
			while (syllableIndex > 0 && matcher.find()) {
				syllableIndex--;
				i = matcher.end();
			}
			if (include) {
				if (matcher.find()) {
					return matcher.start();
				} else {
					return lineText.length();
				}
			}
			return i;
		}

		private Optional<Note> noteAt(int pos) {
			return noteAt(getRow(pos), getColumn(pos));
		}

		private int getRow(int position) {
			return (int) getText().substring(0, position).chars().filter(ch -> ch == '\n').count();
		}

		private int getColumn(int position) {
			return position - getAbsolutePosition(getRow(position), 0);
		}

		private Optional<Note> noteAt(int row, int col) {
			String prefix = getParagraph(row).getText().substring(0, col);
			int index = (int) prefix.chars().filter(ch -> (ch == '-' || ch == ' ')).count();
			try {
				return Optional.of(track.getLine(row).get(index));
			} catch (IndexOutOfBoundsException | NullPointerException e) {
				return Optional.empty();
			}

		}

		private boolean setText(String text) {
			if (!text.equals(getText())) {
				int caretPos = getCaretPosition();
				if (caretPos < text.length()
						&& text.substring(0, caretPos).equals(getText().substring(0, caretPos))) {
					// Replacing with empty String first is necessary, otherwise
					// some illogical exceptions are sometimes thrown without a
					// reasonable reason
					// TODO research the cause further, probably an internal bug
					// of the library
					replaceText(caretPos, getLength(), "");
					replaceText(caretPos, getLength(), text.substring(caretPos));
					moveTo(caretPos);
				} else {
					replaceText(""); // same
					replaceText(text);
					moveTo(caretPos);
					Platform.runLater(() -> requestFollowCaret());
				}
				invalid.set(false);
				return true;
			} else {
				invalid.set(false);
				return false;
			}
		}

		@Override
		public void moveTo(int caretPosition) {
			super.moveTo(normalizeCaretPosition(caretPosition));
		}

		@Override
		public void selectRange(int anchor, int caretPosition) {
			super.selectRange(normalizeCaretPosition(anchor), normalizeCaretPosition(caretPosition));
		}

		private int normalizeCaretPosition(int caretPosition) {
			return Math.max(0, Math.min(caretPosition, getLength()));
		}

		private boolean setTrack(SongTrack track) {
			this.track = track;
			return setText(trackText(track));
		}

		private String trackText(SongTrack track) {
			if (track != null) {
				StringBuilder sb = new StringBuilder();
				track.getLines().forEach(line -> {
					sb.append(lineText(line));
					sb.append("\n");
				});
				if (track.size() > 0) {
					sb.deleteCharAt(sb.length() - 1);
				}
				return sb.toString();
			} else {
				return "";
			}
		}

		private String lineText(SongLine line) {
			String lyrics = line.getNotes().stream().map(this::noteText)
					.collect(Collectors.joining());
			return lyrics;
		}

		private String noteText(Note note) {
			String lyrics = note.getLyrics();
			boolean addSpace = false;
			if (LyricsHelper.startsNewWord(lyrics)) {
				lyrics = lyrics.substring(1);
				if (!note.isFirstInLine()) {
					addSpace = true;
				}
			}
			lyrics = normalize(lyrics);
			if (note.isFirstInLine()) {
				return lyrics;
			}
			if (addSpace) {
				return " " + lyrics;
			}
			return "-" + lyrics;
		}

		private StyleSpans<Collection<String>> computeHighlighting(String text) {
			lastKwEnd = 0;
			StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

			if (track != null) {
				track.getNotes().stream().filter(note -> note.getType() != Type.NORMAL)
						.forEach(note -> {
							int start = getAbsolutePosition(getParagraph(note),
									getColumn(note, false));
							int end = getAbsolutePosition(getParagraph(note),
									getColumn(note, true));
							spansBuilder.add(Collections.emptyList(), start - lastKwEnd);
							spansBuilder.add(Collections.singleton(styleClass(note.getType())),
									end - start);
							lastKwEnd = end;
						});
			}

			spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
			return spansBuilder.create();
		}

		private void applyHighlightning() {
			setStyleSpans(0, computeHighlighting(getText()));
		}

		private static String denormalize(String text) {
			return text.replaceAll(SPACE, " ").replaceAll(MINUS, "-");
		}

		private static String normalize(String text) {
			return text.replaceAll(" ", SPACE).replaceAll("-", MINUS);
		}

		private static String styleClass(Type type) {
			switch (type) {
			case FREESTYLE:
				return "freestyle-lyrics";
			case GOLDEN:
				return "golden-lyrics";
			case RAP:
				return "rap-lyrics";
			case GOLDEN_RAP:
				return "golden-rap-lyrics";
			default:
				return "";
			}
		}
	}

	private static class TextAndNoteSelectionSynchronizer {
		private NoteTextArea area;
		private NoteSelection selection;
		private boolean updateSelectionFromCaretPosition = true;
		private InvalidationListener caretPositionInvalidationListener = obs -> onCaretPositionInvalidated();
		private InvalidationListener selectionInvalidationListener = obs -> updateTextSelection();

		private ChangeListener<? super Boolean> textAreaInvalidChangeListener = this::onTextAreaInvalidChanged;
		private boolean addedTextAreaInvalidListener = false;
		private BiConsumer<Note, Note> preSelectionChangeHandler;

		private TextAndNoteSelectionSynchronizer(NoteTextArea area, NoteSelection selection,
				BiConsumer<Note, Note> preSelectionChangeHandler) {
			this.area = area;
			this.selection = selection;
			this.preSelectionChangeHandler = preSelectionChangeHandler;
			area.focusedProperty().addListener(obs -> onFocusInvalidated());
			onFocusInvalidated();
		}

		private void freeze() {
			updateSelectionFromCaretPosition = false;
		}

		private void unfreeze() {
			updateSelectionFromCaretPosition = true;
		}

		private void updateTextSelection() {
			int caretParagraph = area.getCurrentParagraph();
			int caretColumn = area.getCaretColumn();
			try {
				area.selectRange(
						selection.getFirst().map(note -> area.getParagraph(note))
								.orElse(caretParagraph),
						selection.getFirst().map(note -> area.getColumn(note, false))
								.orElse(caretColumn),
						selection.getLast().map(note -> area.getParagraph(note))
								.orElse(caretParagraph),
						selection.getLast().map(note -> area.getColumn(note, true))
								.orElse(caretColumn));
				area.requestFollowCaret();
			} catch (IndexOutOfBoundsException e) {
				// ignore, lyrics update's not been executed yet
			}
		}

		private void onCaretPositionInvalidated() {
			if (updateSelectionFromCaretPosition) {
				if (area.isInvalid()) {
					if (!addedTextAreaInvalidListener) {
						area.invalidProperty().addListener(textAreaInvalidChangeListener);
						addedTextAreaInvalidListener = true;
					}
				} else {
					updateSelectionFromCaretPosition();
				}
			}
		}

		private void updateSelectionFromCaretPosition() {
			int start = area.getSelection().getStart();
			int end = area.getSelection().getEnd();
			area.noteAt(start).ifPresent(firstNote -> {
				// Selection has at least one note so lastNote should never be
				// null
				Note lastNote = area.noteAt(end).get();
				preSelectionChangeHandler.accept(firstNote, lastNote);
				selection.selectRangeInclusive(firstNote, lastNote);
			});
		}

		private void onFocusInvalidated() {
			if (area.isFocused()) {
				selection.removeListener(selectionInvalidationListener);
				area.caretPositionProperty().addListener(caretPositionInvalidationListener);
			} else {
				area.caretPositionProperty().removeListener(caretPositionInvalidationListener);
				selection.addListener(selectionInvalidationListener);
			}
		}

		private void onTextAreaInvalidChanged(Observable obs, boolean wasInvalid,
				boolean isInvalid) {
			if (!isInvalid) {
				area.invalidProperty().removeListener(textAreaInvalidChangeListener);
				addedTextAreaInvalidListener = false;
				updateSelectionFromCaretPosition();
			}
		}
	}

	private class InsertTextAction extends KarediAction {
		private String text;

		private InsertTextAction(String text) {
			this.text = text;
			setDisabledCondition(
					textArea.focusedProperty().not().or(selection.sizeProperty().isEqualTo(0)));
		}

		@Override
		protected void onAction(ActionEvent event) {
			insertText(text);
		}
	}
}
