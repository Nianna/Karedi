package com.github.nianna.karedi.txt.loader;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class TxtFileLoaderTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    TxtFileEncodingExtractor fileEncodingExtractor = new TxtFileEncodingExtractor();

    TxtFileLoader txtFileLoader = new TxtFileLoader(fileEncodingExtractor);

    @Test
    public void shouldLoadValidCp1250FilesIfEncodingIsSpecified() throws IOException {
        List<String> expectedLines = List.of(
                "ęąćśż",
                "ŃŚŁ"
        );
        File inputFile = prepareFile("cp1250.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    @Test
    public void shouldLoadValidCP1252FilesIfEncodingIsSpecified() throws IOException {
        List<String> expectedLines = List.of(
                "áÑñ",
                "äßÜ"
        );
        File inputFile = prepareFile("cp1252.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    @Test
    public void shouldLoadFilesWithUtf8EncodingIgnoringUnknownCharsIfNoEncodingSpecified() throws IOException {
        List<String> expectedLines = List.of(
                "朿",
                "ь",
                "ascii"
        );
        File inputFile = prepareFile("cp1250_without_encoding_definition.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    @Test
    public void shouldLoadFilesWithUtf8EncodingIgnoringUnknownCharsIfWrongEncodingSpecified() throws IOException {
        List<String> expectedLines = List.of(
                "ęąĆśż",
                "áÑñ",
                "äßÜ",
                "漢字",
                "한자"
        );
        File inputFile = prepareFile("utf8_with_cp1250_encoding.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    @Test
    public void shouldLoadValidUtf8Files() throws IOException {
        List<String> expectedLines = List.of(
                "ęąĆśż",
                "áÑñ",
                "äßÜ",
                "漢字",
                "한자"
        );
        File inputFile = prepareFile("utf8.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    @Test
    public void shouldLoadValidUtf8FilesWithBom() throws IOException {
        List<String> expectedLines = List.of(
                "ęąĆśż",
                "áÑñ",
                "äßÜ",
                "漢字",
                "한자"
        );
        File inputFile = prepareFile("utf8_with_bom.txt");

        Optional<List<String>> loadedLines = txtFileLoader.loadFileLines(inputFile.toPath());

        Assert.assertTrue(loadedLines.isPresent());
        Assert.assertEquals(expectedLines, loadedLines.get());
    }

    private File prepareFile(String resourceFileName) throws IOException {
        File inputFile = tmpFolder.newFile("input.txt");
        byte[] bytes = getClass().getResourceAsStream(resourceFileName).readAllBytes();
        Files.write(inputFile.toPath(), bytes);
        return inputFile;
    }

}