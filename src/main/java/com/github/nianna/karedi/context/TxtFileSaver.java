package main.java.com.github.nianna.karedi.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TxtFileSaver {
	private String encoding = "UTF-8";

	public void save(File file, String content) throws FileNotFoundException {
		save(file, Arrays.asList(content));
	}

	public void save(File file, Iterable<String> content) throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(file, encoding)) {
			content.forEach(out::println);
		} catch (UnsupportedEncodingException e) {
			// should never happen
			e.printStackTrace();
		}
	}
}
