package com.github.nianna.karedi.song.tag;

import java.util.Arrays;
import java.util.Optional;

public enum FormatSpecification {
    V_1_0_0("1.0.0"),
    V_1_1_0("1.1.0");

    private final String version;

    FormatSpecification(String version) {
        this.version = version;
    }

    public static Optional<FormatSpecification> tryParse(String version) {
        return Arrays.stream(FormatSpecification.values())
                .filter(format -> format.version.equals(version))
                .findFirst();
    }

    public static boolean supportsMultipleValues(FormatSpecification formatSpecification, TagKey tagKey) {
        return FormatSpecificationMultiValuedTags.isSupported(formatSpecification, tagKey);
    }

    public static boolean supports(FormatSpecification formatSpecification, String tagKey) {
        return TagKey.optionalValueOf(tagKey)
                .filter(parsedKey -> FormatSpecification.supports(formatSpecification, parsedKey))
                .isPresent();
    }

    public static boolean supports(FormatSpecification formatSpecification, TagKey tagKey) {
        return FormatSpecificationSupportedTags.isSupported(formatSpecification, tagKey);
    }

    @Override
    public String toString() {
        return version;
    }
}
