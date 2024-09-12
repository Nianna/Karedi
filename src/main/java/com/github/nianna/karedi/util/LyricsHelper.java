package com.github.nianna.karedi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

public final class LyricsHelper {
	public static final char WORD_SEPARATOR = ' ';
	public static final char EMPTY_LYRICS = '~';

	private LyricsHelper() {
	}

	public static Pair<String, String> split(String lyrics) {
		List<String> result = split(lyrics, 2);
		return new Pair<>(result.get(0), result.get(1));
	}

	public static List<String> split(String lyrics, int numberOfParts) {
		List<String> result = new ArrayList<>();
		lyrics = normalize(lyrics);
		for (int i = 1; i < numberOfParts; i++) {
			int splitPoint = lyrics.lastIndexOf(WORD_SEPARATOR);
			if (splitPoint > 0) {
				result.add(0, lyrics.substring(splitPoint));
				lyrics = lyrics.substring(0, splitPoint);
			} else {
				break;
			}
		}
		result.add(0, lyrics);
		while (result.size() < numberOfParts) {
			result.add(String.valueOf(EMPTY_LYRICS));
		}
		return result;
	}

	public static boolean isSplittable(String lyrics) {
		lyrics = normalize(lyrics);
		return lyrics.lastIndexOf(WORD_SEPARATOR) > 0;
	}

	public static String join(List<String> args) {
		boolean removedEmptyLyrics = false;
		StringBuffer lyricsBuffer = new StringBuffer();
		if (args.size() > 0) {
			if (args.get(0).trim().equals(String.valueOf(EMPTY_LYRICS))) {
				removedEmptyLyrics = true;
			} else {
				lyricsBuffer.append(args.get(0));
			}
			for (int i = 1; i < args.size(); ++i) {
				String part = args.get(i);
				if (part.trim().length() > 0) {
					switch (part.trim().charAt(0)) {
					case EMPTY_LYRICS:
						removedEmptyLyrics = true;
						if (startsNewWord(part)) {
							lyricsBuffer.append(WORD_SEPARATOR);
							if (part.length() > 1) {
								lyricsBuffer.append(part.substring(2));
							}
						} else {
							lyricsBuffer.append(part.substring(1));
						}
						break;
					default:
						lyricsBuffer.append(args.get(i));
					}
				}
			}
		}
		String result = lyricsBuffer.toString();
		if ((result.equals("") || result.equals(" ")) && removedEmptyLyrics) {
			return result + EMPTY_LYRICS;
		} else {
			return result;
		}
	}

	public static String join(String... args) {
		return join(Arrays.asList(args));
	}

	public static boolean startsNewWord(String lyrics) {
		return lyrics.startsWith(String.valueOf(WORD_SEPARATOR));
	}

	public static String normalize(String lyrics) {
		boolean startsNewWord = startsNewWord(lyrics);
		lyrics = lyrics.trim().codePoints()
				.filter(ch -> !Character.isISOControl(ch))
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		lyrics = lyrics == "" ? String.valueOf(EMPTY_LYRICS) : lyrics;
		if (startsNewWord) {
			return " " + lyrics;
		} else {
			return lyrics;
		}
	}

	public static boolean containsLettersOrDigits(String lyrics) {
		return lyrics.trim().codePoints().filter(Character::isLetterOrDigit).count() > 0;
	}

	public static String defaultLyrics() {
		return String.valueOf(EMPTY_LYRICS);
	}
}
