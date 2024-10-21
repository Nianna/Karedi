package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.util.CollectionsUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class FormatSpecificationMandatoryTags {

    private static final Set<TagKey> COMMON_MANDATORY_KEYS = Set.of(
            TagKey.ARTIST,
            TagKey.TITLE,
            TagKey.BPM,
            TagKey.MP3
    );

    private static final Set<TagKey> V_1_0_0_MANDATORY_KEYS = CollectionsUtils.join(
            COMMON_MANDATORY_KEYS, Set.of(TagKey.VERSION)
    );

    private static final Set<TagKey> V_1_1_0_MANDATORY_KEYS = CollectionsUtils.join(
            V_1_0_0_MANDATORY_KEYS,
            Set.of(TagKey.AUDIO)
    );

    private static final Map<FormatSpecification, Set<TagKey>> MANDATORY_KEYS = Map.of(
            FormatSpecification.V_1_0_0, V_1_0_0_MANDATORY_KEYS,
            FormatSpecification.V_1_1_0, V_1_1_0_MANDATORY_KEYS,
            FormatSpecification.V_1_2_0, V_1_1_0_MANDATORY_KEYS
    );


    private FormatSpecificationMandatoryTags() {

    }

    public static Set<TagKey> forFormat(FormatSpecification formatSpecification) {
        return Optional.ofNullable(formatSpecification)
                .map(MANDATORY_KEYS::get)
                .orElse(COMMON_MANDATORY_KEYS);
    }


}
