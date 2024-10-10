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

    public boolean supports(String tagKey) {
        return TagKey.optionalValueOf(tagKey)
                .filter(recognizedKey -> FormatSpecificationSupportedTags.isSupported(this, recognizedKey))
                .isPresent();
    }

    @Override
    public String toString() {
        return version;
    }
}
