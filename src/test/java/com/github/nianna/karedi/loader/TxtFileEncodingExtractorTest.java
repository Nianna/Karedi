package com.github.nianna.karedi.loader;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TxtFileEncodingExtractorTest {

    private final TxtFileEncodingExtractor extractor = new TxtFileEncodingExtractor();

    @Test
    public void shouldExtractCp1250Encoding() {
        String inputLine = " #ENCODING:CP1250   ";

        Optional<Charset> charset = extractor.tryExtract(inputLine);

        Assert.assertTrue(charset.isPresent());
        Assert.assertEquals(Charset.forName("windows-1250"), charset.get());
    }

    @Test
    public void shouldExtractCp1252Encoding() {
        String inputLine = " #ENCODING:CP1252   ";

        Optional<Charset> charset = extractor.tryExtract(inputLine);

        Assert.assertTrue(charset.isPresent());
        Assert.assertEquals(Charset.forName("windows-1252"), charset.get());
    }

    @Test
    public void shouldExtractUtf8Encoding() {
        String inputLine = " #ENCODING:UTF8   ";

        Optional<Charset> charset = extractor.tryExtract(inputLine);

        Assert.assertTrue(charset.isPresent());
        Assert.assertEquals(StandardCharsets.UTF_8, charset.get());
    }

    @Test
    public void shouldNotExtractIfPrefixInvalid() {
        String inputLine = " #ENCODINGS:CP1250   ";

        Optional<Charset> charset = extractor.tryExtract(inputLine);

        Assert.assertTrue(charset.isEmpty());
    }

    @Test
    public void shouldNotExtractIfCodeInvalid() {
        String inputLine = " #ENCODING:WINDOWS-1250   ";

        Optional<Charset> charset = extractor.tryExtract(inputLine);

        Assert.assertTrue(charset.isEmpty());
    }

    @Test
    public void shouldNotExtractIfLineIsNull() {
        Optional<Charset> charset = extractor.tryExtract(null);

        Assert.assertTrue(charset.isEmpty());
    }

}