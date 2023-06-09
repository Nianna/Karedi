package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.I18N;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

class TxtFileLoader {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final String UTF8_BOM = "\uFEFF";

    private static final Logger LOGGER = Logger.getLogger(TxtFileLoader.class.getName());

    private final TxtFileEncodingExtractor fileEncodingExtractor;

    TxtFileLoader(TxtFileEncodingExtractor fileEncodingExtractor) {
        this.fileEncodingExtractor = fileEncodingExtractor;
    }

    Optional<List<String>> loadFileLines(Path path) {
        try {
            Charset charset = extractCharsetFromFile(path).orElse(DEFAULT_CHARSET);
            CharsetDecoder decoder = createDecoder(charset, CodingErrorAction.REPORT);
            return Optional.ofNullable(decodeFile(path, decoder))
                    .or(() -> readFileUsingFallbackDecoder(path));
        } catch (UncheckedIOException e) {
            LOGGER.severe(I18N.get("loader.open_file.fail"));
            return Optional.empty();
        }
    }

    private Optional<Charset> extractCharsetFromFile(Path path) {
        CharsetDecoder decoder = createDecoder(DEFAULT_CHARSET, CodingErrorAction.IGNORE);
        return decodeFile(path, decoder, this::findEncoding);
    }

    private static CharsetDecoder createDecoder(Charset charset, CodingErrorAction action) {
        return charset.newDecoder()
                .onMalformedInput(action)
                .onUnmappableCharacter(action);
    }

    private Optional<List<String>> readFileUsingFallbackDecoder(Path path) {
        LOGGER.warning(I18N.get("loader.invalid_encoding"));
        CharsetDecoder decoder = createDecoder(DEFAULT_CHARSET, CodingErrorAction.IGNORE);
        return Optional.ofNullable(decodeFile(path, decoder));
    }

    private List<String> decodeFile(Path path, CharsetDecoder decoder) {
        return decodeFile(path, decoder, this::prepareForLoading);
    }

    private List<String> prepareForLoading(Stream<String> inputLines) {
        return inputLines
                .map(this::preprocess)
                .filter(line -> !fileEncodingExtractor.isEncodingDefinition(line))
                .toList();
    }

    private Optional<Charset> findEncoding(Stream<String> inputLines) {
        return inputLines
                .map(fileEncodingExtractor::tryExtract)
                .flatMap(Optional::stream)
                .findFirst();
    }

    private <V> V decodeFile(Path path, CharsetDecoder decoder, Function<Stream<String>, V> inputHandler) {
        try (Reader r = Channels.newReader(FileChannel.open(path), decoder, -1);
             BufferedReader br = new BufferedReader(r)) {
            return inputHandler.apply(br.lines());
        } catch (UncheckedIOException e) {
            // decoding with provided charset failed
            return null;
        } catch (IOException e) {
            // problem opening or reading file
            throw new UncheckedIOException(e);
        }
    }

    private String preprocess(String line) {
        return line.replace(UTF8_BOM, "");
    }

}
