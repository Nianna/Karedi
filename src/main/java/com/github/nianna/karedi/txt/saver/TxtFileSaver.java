package com.github.nianna.karedi.txt.saver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.stream.Stream;

class TxtFileSaver {

	private static final String DEFAULT_ENCODING = "UTF-8";

	void save(File file, Stream<String> content) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(file, DEFAULT_ENCODING)) {
			content.forEach(out::println);
		} catch (UnsupportedEncodingException e) {
			// should never happen
			e.printStackTrace();
		}
	}
}
