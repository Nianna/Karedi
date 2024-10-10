package com.github.nianna.karedi.action;

public enum KarediActions {
	// File
	NEW,
	LOAD,
	RELOAD,
	SAVE,
	SAVE_AS,
	IMPORT_AUDIO,
	EXPORT_AS_SINGLEPLAYER,
	EXPORT_AS_DUET,
	EXIT,

	// EDIT
	COPY,
	PASTE,
	CUT,
	SET_TONES,
	SET_SYNCHRO,
	SET_LYRICS,
	SET_TONES_AND_SYNCHRO,
	SET_TONES_AND_LYRICS,
	SET_SYNCHRO_AND_LYRICS,
	SET_TONES_SYNCHRO_AND_LYRICS,

	ADD_NOTE,
	ADD_NOTE_BEFORE,
	ADD_TRACK,
	MARK_AS_GOLDEN,
	MARK_AS_FREESTYLE,
	MARK_AS_RAP, 
	DELETE_SELECTION,
	DELETE_SELECTION_HARD,
	DELETE_LYRICS,
	DELETE_TRACK,

	RESET_TRACK_COLORS,

	SHOW_PREFERENCES,

	// History
	UNDO,
	REDO,

	// Selection
	INCREASE_SELECTION,
	DECREASE_SELECTION,
	SELECT_NEXT,
	SELECT_PREVIOUS,
	CLEAR_SELECTION,
	SELECT_VISIBLE,
	SELECT_ALL,

	// View
	VIEW_NEXT_LINE,
	VIEW_PREVIOUS_LINE,
	VIEW_NEXT_TRACK,
	VIEW_PREVIOUS_TRACK,
	VIEW_MEDLEY,

	// Play
	PLAY_SELECTION_AUDIO_MIDI,
	PLAY_SELECTION_MIDI,
	PLAY_SELECTION_AUDIO,
	PLAY_VISIBLE_AUDIO_MIDI,
	PLAY_VISIBLE_AUDIO,
	PLAY_VISIBLE_MIDI,
	PLAY_ALL_MIDI,
	PLAY_ALL_AUDIO,
	PLAY_ALL_AUDIO_MIDI,
	PLAY_TO_THE_END_MIDI,
	PLAY_TO_THE_END_AUDIO,
	PLAY_TO_THE_END_AUDIO_MIDI,
	PLAY_MEDLEY_AUDIO_MIDI,
	PLAY_MEDLEY_AUDIO,
	PLAY_MEDLEY_MIDI,

	PLAY_BEFORE_SELECTION,
	PLAY_AFTER_SELECTION,

	STOP_PLAYBACK,

	TOGGLE_TICKS,

	TOGGLE_LINEBREAK,

	SPLIT_SELECTION,
	JOIN_SELECTION,
	MOVE_SELECTION_UP,
	MOVE_SELECTION_DOWN,
	MOVE_SELECTION_LEFT,
	MOVE_SELECTION_RIGHT,
	SHORTEN_LEFT_SIDE,
	SHORTEN_RIGHT_SIDE,
	LENGTHEN_RIGHT_SIDE,
	LENGTHEN_LEFT_SIDE,

	EDIT_LYRICS,
	ROLL_LYRICS_LEFT,
	ROLL_LYRICS_RIGHT,
	INSERT_SPACE,
	INSERT_MINUS,

	MOVE_VISIBLE_AREA_RIGHT,
	MOVE_VISIBLE_AREA_LEFT,
	MOVE_VISIBLE_AREA_UP,
	MOVE_VISIBLE_AREA_DOWN,

	FIT_TO_VISIBLE,
	FIT_TO_SELECTION,
	FIT_VERTICALLY,
	FIT_HORIZONTALLY,

	TOGGLE_PIANO,
	SWITCH_MODE,

	// TAGS
	MULTIPLY_BPM_BY_TWO,
	DIVIDE_BPM_BY_TWO,
	EDIT_BPM,

	EDIT_MEDLEY,
	MEDLEY_FROM_VISIBLE,
	MEDLEY_FROM_SELECTION,
	MEDLEY_SET_START,
	MEDLEY_SET_END,

	CONVERT_VERSION_TO_NONE,
	CONVERT_VERSION_TO_1_1_0,
	CONVERT_VERSION_TO_1_0_0,

	RENAME,
	ADD_TAG,
	TAP_NOTES,
	WRITE_TONES,

	SET_START_TAG,
	SET_END_TAG,
	SET_GAP_TAG,

	// DEBUG
	RESET_SEQUENCER,

}
