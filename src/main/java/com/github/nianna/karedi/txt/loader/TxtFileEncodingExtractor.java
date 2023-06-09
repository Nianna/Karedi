package com.github.nianna.karedi.txt.loader;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

class TxtFileEncodingExtractor {

    private static final String ENCODING_DEFINITION_PREFIX = "#ENCODING:";

    private static final Map<String, Charset> CHARSET_MAPPINGS = Map.of(
            "CP1250", Charset.forName("windows-1250"),
            "CP1252", Charset.forName("windows-1252"),
            "UTF8", StandardCharsets.UTF_8
    );

    Optional<Charset> tryExtract(String fileLine) {
        return Optional.ofNullable(fileLine)
                .map(this::normalize)
                .filter(this::isEncodingDefinition)
                .map(line -> line.substring(ENCODING_DEFINITION_PREFIX.length()))
                .map(CHARSET_MAPPINGS::get);
    }

    boolean isEncodingDefinition(String fileLine) {
        return nonNull(fileLine) && normalize(fileLine).startsWith(ENCODING_DEFINITION_PREFIX);
    }

    private String normalize(String fileLine) {
        return Optional.ofNullable(fileLine)
                .map(String::strip)
                .map(String::toUpperCase)
                .orElse(null);
    }
}
