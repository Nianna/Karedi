package com.github.nianna.karedi.audio;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileLoader {
	private final File file;

	public FileLoader(File file) {
		this.file = file;
	}

	public byte[] load() throws FileNotFoundException, IOException {
		byte[] cache = null;
		try (FileInputStream fi = new FileInputStream(file);
				BufferedInputStream bfi = new BufferedInputStream(fi);
				ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = bfi.read(buffer)) > -1) {
				bout.write(buffer, 0, bytesRead);
			}
			cache = bout.toByteArray();
		}
		return cache;
	}
}
