package com.github.nianna.karedi.txt.clipboard;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClipboardHelper {

    public Stream<String> readLines() {
        return Stream.ofNullable(Clipboard.getSystemClipboard())
                .map(Clipboard::getString)
                .map(content -> content.split("\\R"))
                .flatMap(Stream::of);
    }

    public void writeLines(Stream<String> lines) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        String result = lines
                .collect(Collectors.joining(System.lineSeparator()));
        content.putString(result);
        clipboard.setContent(content);
    }
}
