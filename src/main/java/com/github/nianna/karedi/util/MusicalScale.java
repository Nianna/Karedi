package com.github.nianna.karedi.util;

import java.util.Arrays;

public final class MusicalScale {
	public static final int MAX_TONE = 67;
	public static final int MIN_TONE = -60;
	public static final int INTERVAL_BETWEEN_SAME_TONES = 12;

	public enum Note {
		C("C"),
		C_SHARP("C#"),
		D("D"),
		D_SHARP("D#"),
		E("E"),
		F("F"),
		F_SHARP("F#"),
		G("G"),
		G_SHARP("G#"),
		A("A"),
		A_SHARP("A#"),
		H("H");

		private String string;

		Note(String string) {
			this.string = string;
		}

		@Override
		public String toString() {
			return string;
		}

		public static Boolean isSharp(Note note) {
			switch (note) {
			case C_SHARP:
			case D_SHARP:
			case F_SHARP:
			case G_SHARP:
			case A_SHARP:
				return true;
			default:
				return false;
			}
		}
	}

	private MusicalScale() {
	}

	public static Note getNote(int height) {
		return Arrays.asList(Note.values()).get(Math.floorMod(height, Note.values().length));
	}

	public static int getIndex(Note note) {
		return Arrays.asList(Note.values()).indexOf(note);
	}

	public static boolean isToneValid(int tone) {
		return tone >= MIN_TONE && tone <= MAX_TONE;
	}

}
