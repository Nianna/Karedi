package com.github.nianna.karedi.txt.clipboard;

import java.util.List;
import java.util.stream.Stream;

public class DummyClipboardHelper extends ClipboardHelper {

    private List<String> content = List.of();

    @Override
    public Stream<String> readLines() {
        return content.stream();
    }

    @Override
    public void writeLines(Stream<String> lines) {
        content = lines.toList();
    }
}
