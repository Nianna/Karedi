package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.action.ActionMap;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.command.MergeNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.tag.TagKey;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;

public class ActionHelper {
    public ActionMap actionMap = new ActionMap();

    private final AppContext appContext;

    public ActionHelper(AppContext appContext) {
        this.appContext = appContext;
    }

    public void add(KarediActions key, KarediAction action) {
        actionMap.put(key.toString(), action);
    }

    public void execute(KarediActions action) {
        if (canExecute(action)) {
            get(action).handle(null);
        }
    }

    public boolean canExecute(KarediActions action) {
        return !get(action).isDisabled();
    }

    public KarediAction get(KarediActions key) {
        return actionMap.get(key.toString());
    }

    public void addActions() {
        addFileActions();
        addEditActions();
        addPlayActions();
        addSelectionActions();
        addTagsActions();
        addViewActions();
        addHelpActions();
    }

    private void addFileActions() {
        add(KarediActions.NEW, new NewSongAction(appContext));
        add(KarediActions.LOAD, new LoadSongAction(appContext));
        add(KarediActions.RELOAD, new ReloadSongAction(appContext));
        add(KarediActions.SAVE, new SaveSongAction(appContext));
        add(KarediActions.SAVE_AS, new SaveSongAsAction(appContext));
        add(KarediActions.IMPORT_AUDIO, new ImportAudioAction(appContext));
        add(KarediActions.EXPORT_AS_SINGLEPLAYER, new ExportTracksAction(appContext, 1));
        add(KarediActions.EXPORT_AS_DUET, new ExportTracksAction(appContext, 2));
        add(KarediActions.EXIT, new ExitAction());
    }

    private void addEditActions() {
        add(KarediActions.UNDO, new UndoAction(appContext));
        add(KarediActions.REDO, new RedoAction(appContext));

        add(KarediActions.MOVE_SELECTION_UP, new MoveSelectionAction(appContext, Direction.UP));
        add(KarediActions.MOVE_SELECTION_DOWN, new MoveSelectionAction(appContext, Direction.DOWN));
        add(KarediActions.MOVE_SELECTION_LEFT, new MoveSelectionAction(appContext, Direction.LEFT));
        add(KarediActions.MOVE_SELECTION_RIGHT, new MoveSelectionAction(appContext, Direction.RIGHT));

        add(KarediActions.SHORTEN_LEFT_SIDE, new ResizeAction(appContext, Direction.LEFT, -1));
        add(KarediActions.SHORTEN_RIGHT_SIDE, new ResizeAction(appContext, Direction.RIGHT, -1));
        add(KarediActions.LENGTHEN_LEFT_SIDE, new ResizeAction(appContext, Direction.LEFT, 1));
        add(KarediActions.LENGTHEN_RIGHT_SIDE, new ResizeAction(appContext, Direction.RIGHT, 1));

        add(KarediActions.CUT, new CutSelectionAction(appContext));
        add(KarediActions.COPY, new CopySelectionAction(appContext));
        add(KarediActions.PASTE, new PasteAction(appContext));
        add(KarediActions.SET_TONES, new MergeAction(appContext, MergeNotesCommand.MergeMode.TONES));
        add(KarediActions.SET_SYNCHRO, new MergeAction(appContext, MergeNotesCommand.MergeMode.SYNCHRO));
        add(KarediActions.SET_LYRICS, new MergeAction(appContext, MergeNotesCommand.MergeMode.LYRICS));
        add(KarediActions.SET_TONES_AND_SYNCHRO, new MergeAction(appContext, MergeNotesCommand.MergeMode.TONES_SYNCHRO));
        add(KarediActions.SET_TONES_AND_LYRICS, new MergeAction(appContext, MergeNotesCommand.MergeMode.TONES_LYRICS));
        add(KarediActions.SET_SYNCHRO_AND_LYRICS, new MergeAction(appContext, MergeNotesCommand.MergeMode.SYNCHRO_LYRICS));
        add(KarediActions.SET_TONES_SYNCHRO_AND_LYRICS,
                new MergeAction(appContext, MergeNotesCommand.MergeMode.TONES_SYNCHRO_LYRICS));

        add(KarediActions.ADD_NOTE, new AddNoteAction(appContext));
        add(KarediActions.ADD_TRACK, new AddTrackAction(appContext));
        add(KarediActions.DELETE_SELECTION, new DeleteSelectionAction(appContext, true));
        add(KarediActions.DELETE_LYRICS, new DeleteSelectionLyricsAction(appContext));
        add(KarediActions.DELETE_SELECTION_HARD, new DeleteSelectionAction(appContext, false));
        add(KarediActions.DELETE_TRACK, new DeleteTrackAction(appContext));
        add(KarediActions.TOGGLE_LINEBREAK, new ToggleLineBreakAction(appContext));
        add(KarediActions.SPLIT_SELECTION, new SplitSelectionAction(appContext));
        add(KarediActions.JOIN_SELECTION, new JoinSelectionAction(appContext));
        add(KarediActions.MARK_AS_FREESTYLE, new ChangeSelectionTypeAction(appContext, Note.Type.FREESTYLE));
        add(KarediActions.MARK_AS_GOLDEN, new ChangeSelectionTypeAction(appContext, Note.Type.GOLDEN));
        add(KarediActions.MARK_AS_RAP, new ChangeSelectionTypeAction(appContext, Note.Type.RAP));

        add(KarediActions.ROLL_LYRICS_LEFT, new RollLyricsLeftAction(appContext));
        add(KarediActions.ROLL_LYRICS_RIGHT, new RollLyricsRightAction(appContext));

        add(KarediActions.RESET_TRACK_COLORS, new ResetTrackColorsAction(appContext));

        add(KarediActions.SHOW_PREFERENCES, new ShowPreferencesAction());
    }

    private void addPlayActions() {
        add(KarediActions.PLAY_SELECTION_AUDIO, new PlaySelectionAction(appContext, Player.Mode.AUDIO_ONLY));
        add(KarediActions.PLAY_SELECTION_MIDI, new PlaySelectionAction(appContext, Player.Mode.MIDI_ONLY));
        add(KarediActions.PLAY_SELECTION_AUDIO_MIDI, new PlaySelectionAction(appContext, Player.Mode.AUDIO_MIDI));
        add(KarediActions.PLAY_VISIBLE_AUDIO, new PlayRangeAction(appContext, Player.Mode.AUDIO_ONLY,
                appContext.visibleAreaContext.lowerXBoundProperty(), appContext.visibleAreaContext.upperXBoundProperty()));
        add(KarediActions.PLAY_VISIBLE_MIDI, new PlayRangeAction(appContext, Player.Mode.MIDI_ONLY,
                appContext.visibleAreaContext.lowerXBoundProperty(), appContext.visibleAreaContext.upperXBoundProperty()));
        add(KarediActions.PLAY_VISIBLE_AUDIO_MIDI, new PlayRangeAction(appContext, Player.Mode.AUDIO_MIDI,
                appContext.visibleAreaContext.lowerXBoundProperty(), appContext.visibleAreaContext.upperXBoundProperty()));
        add(KarediActions.PLAY_ALL_AUDIO,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.AUDIO_ONLY,
                        appContext.beatRangeContext.minBeatProperty(),
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );
        add(KarediActions.PLAY_ALL_MIDI,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.MIDI_ONLY,
                        appContext.beatRangeContext.minBeatProperty(),
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );
        add(KarediActions.PLAY_ALL_AUDIO_MIDI,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.AUDIO_MIDI,
                        appContext.beatRangeContext.minBeatProperty(),
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );

        IntegerBinding playToTheEndStartBeat = Bindings.createIntegerBinding(() -> {
            if (appContext.visibleAreaContext.isMarkerVisible()) {
                return appContext.getMarkerBeat();
            } else {
                return appContext.visibleAreaContext.getLowerXBound();
            }
        }, appContext.markerBeatProperty(), appContext.visibleAreaContext.lowerXBoundProperty());
        add(KarediActions.PLAY_TO_THE_END_AUDIO,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.AUDIO_ONLY,
                        playToTheEndStartBeat,
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );
        add(KarediActions.PLAY_TO_THE_END_MIDI,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.MIDI_ONLY,
                        playToTheEndStartBeat,
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );
        add(KarediActions.PLAY_TO_THE_END_AUDIO_MIDI,
                new PlayRangeAction(
                        appContext,
                        Player.Mode.AUDIO_MIDI,
                        playToTheEndStartBeat,
                        appContext.beatRangeContext.maxBeatProperty()
                )
        );
        add(KarediActions.PLAY_MEDLEY_AUDIO, new PlayMedleyAction(appContext, Player.Mode.AUDIO_ONLY));
        add(KarediActions.PLAY_MEDLEY_AUDIO_MIDI, new PlayMedleyAction(appContext, Player.Mode.AUDIO_MIDI));
        add(KarediActions.PLAY_MEDLEY_MIDI, new PlayMedleyAction(appContext, Player.Mode.MIDI_ONLY));
        add(KarediActions.PLAY_BEFORE_SELECTION, new PlayAuxiliaryNoteBeforeSelectionAction(appContext));
        add(KarediActions.PLAY_AFTER_SELECTION, new PlayAuxiliaryNoteAfterSelectionAction(appContext));
        add(KarediActions.STOP_PLAYBACK, new StopPlaybackAction(appContext));
        add(KarediActions.TOGGLE_TICKS, new ToggleTicksAction(appContext));
    }

    private void addSelectionActions() {
        add(KarediActions.SELECT_PREVIOUS, new SelectPreviousAction(appContext));
        add(KarediActions.SELECT_NEXT, new SelectNextAction(appContext));
        add(KarediActions.DECREASE_SELECTION, new SelectLessAction(appContext));
        add(KarediActions.INCREASE_SELECTION, new SelectMoreAction(appContext));
        add(KarediActions.CLEAR_SELECTION, new SelectNoneAction(appContext));
        add(KarediActions.SELECT_VISIBLE, new SelectVisibleAction(appContext));
        add(KarediActions.SELECT_ALL, new SelectAllAction(appContext));
    }

    private void addViewActions() {
        add(KarediActions.VIEW_NEXT_LINE, new NextLineAction(appContext));
        add(KarediActions.VIEW_PREVIOUS_LINE, new PreviousLineAction(appContext));
        add(KarediActions.VIEW_NEXT_TRACK, new NextTrackAction(appContext));
        add(KarediActions.VIEW_PREVIOUS_TRACK, new PreviousTrackAction(appContext));

        add(KarediActions.FIT_TO_VISIBLE, new FitToVisibleAction(appContext, true, true));
        add(KarediActions.FIT_TO_SELECTION, new FitToSelectionAction(appContext));
        add(KarediActions.FIT_VERTICALLY, new FitToVisibleAction(appContext, true, false));
        add(KarediActions.FIT_HORIZONTALLY, new FitToVisibleAction(appContext, false, true));

        add(KarediActions.MOVE_VISIBLE_AREA_LEFT, new MoveVisibleAreaAction(appContext, Direction.LEFT, 1));
        add(KarediActions.MOVE_VISIBLE_AREA_RIGHT,
                new MoveVisibleAreaAction(appContext, Direction.RIGHT, 1));
        add(KarediActions.MOVE_VISIBLE_AREA_UP, new MoveVisibleAreaAction(appContext, Direction.UP, 1));
        add(KarediActions.MOVE_VISIBLE_AREA_DOWN, new MoveVisibleAreaAction(appContext, Direction.DOWN, 1));

        add(KarediActions.VIEW_MEDLEY, new ViewMedleyAction(appContext));
        add(KarediActions.SWITCH_MODE, new SwitchModeAction(appContext));
    }

    private void addTagsActions() {
        add(KarediActions.MULTIPLY_BPM_BY_TWO, new EditBpmAction(appContext, 2));
        add(KarediActions.DIVIDE_BPM_BY_TWO, new EditBpmAction(appContext, 0.5));
        add(KarediActions.EDIT_BPM, new EditBpmAction(appContext));

        add(KarediActions.MEDLEY_FROM_SELECTION, new SetMedleyFromSelectionAction(appContext, true, true));
        add(KarediActions.MEDLEY_SET_START, new SetMedleyFromSelectionAction(appContext, true, false));
        add(KarediActions.MEDLEY_SET_END, new SetMedleyFromSelectionAction(appContext, false, true));
        add(KarediActions.EDIT_MEDLEY, new EditMedleyAction(appContext));

        add(KarediActions.SET_START_TAG, new SetTagValueFromMarkerPositionAction(appContext, TagKey.START));
        add(KarediActions.SET_END_TAG, new SetTagValueFromMarkerPositionAction(appContext, TagKey.END));
        add(KarediActions.SET_GAP_TAG, new SetTagValueFromMarkerPositionAction(appContext, TagKey.GAP));

        add(KarediActions.RENAME, new RenameAction(appContext));
        add(KarediActions.ADD_TAG, new AddTagAction(appContext));
    }

    private void addHelpActions() {
        add(KarediActions.RESET_SEQUENCER, new ResetSequencerAction(appContext));
    }
}
