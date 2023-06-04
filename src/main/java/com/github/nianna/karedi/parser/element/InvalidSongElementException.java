package com.github.nianna.karedi.parser.element;

public class InvalidSongElementException extends Exception {
	private static final long serialVersionUID = 183063255466120123L;
	private String line;

	public InvalidSongElementException(String line) {
		super("Invalid input line: " + line);
		this.line = line;
	}

	public String getLine() {
		return line;
	}
}
