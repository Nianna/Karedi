package com.github.nianna.karedi.song.tag;

import java.util.List;

class FormatSpecificationMultiValuedTags {

    private static final List<TagKey> MULTI_VALUED_KEYS = List.of(
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
