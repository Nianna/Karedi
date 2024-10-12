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

    public boolean supports(String tagKey) {
        return TagKey.optionalValueOf(tagKey)
                .filter(this::supports)
                .isPresent();
    }

    public boolean supports(TagKey tagKey) {
        return FormatSpecificationSupportedTags.isSupported(this, tagKey);
    }

    @Override
    public String toString() {
        return version;
    }
}
