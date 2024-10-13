package com.github.nianna.karedi.song.tag;

import java.util.Set;

class FormatSpecificationMultiValuedTags {

    private static final Set<TagKey> MULTI_VALUED_KEYS = Set.of(
            TagKey.CREATOR,
            TagKey.EDITION,
            TagKey.GENRE,
            TagKey.LANGUAGE,
            TagKey.TAGS
    );

    private FormatSpecificationMultiValuedTags() {

    }

    static boolean isSupported(FormatSpecification formatSpecification, TagKey tagKey) {
        return formatSpecification == FormatSpecification.V_1_1_0 && MULTI_VALUED_KEYS.contains(tagKey);
    }
}
