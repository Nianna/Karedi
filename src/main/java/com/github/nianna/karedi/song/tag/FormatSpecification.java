package com.github.nianna.karedi.song.tag;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public enum FormatSpecification {
    V_1_0_0("1.0.0"),
    V_1_1_0("1.1.0"),
    V_1_2_0("1.2.0");

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
        return FormatSpecificationSupportedTags.isSupported(formatSpecification, tagKey);
    }

    public static boolean supports(FormatSpecification formatSpecification, TagKey tagKey) {
        return supports(formatSpecification, tagKey.toString());
    }

    public static Set<TagKey> mandatoryTags(FormatSpecification formatSpecification) {
        return FormatSpecificationMandatoryTags.forFormat(formatSpecification);
    }

    @Override
    public String toString() {
        return version;
    }

    public boolean isAtLeast(FormatSpecification formatSpecification) {
        return compareTo(formatSpecification) >= 0;
    }

}
