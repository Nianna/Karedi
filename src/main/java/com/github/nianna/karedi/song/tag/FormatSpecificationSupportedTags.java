package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.util.CollectionsUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class FormatSpecificationSupportedTags {

    private static final Set<TagKey> V_1_0_0_SUPPORTED_KEYS = Set.of(
            TagKey.VERSION,
            TagKey.TITLE,
            TagKey.ARTIST,
            TagKey.MP3,
            TagKey.BPM,
            TagKey.GAP,
            TagKey.COVER,
            TagKey.BACKGROUND,
            TagKey.VIDEO,
            TagKey.VIDEOGAP,
            TagKey.GENRE,
            TagKey.EDITION,
            TagKey.CREATOR,
            TagKey.LANGUAGE,
            TagKey.YEAR,
            TagKey.START,
            TagKey.END,
            TagKey.PREVIEWSTART,
            TagKey.MEDLEYSTARTBEAT,
            TagKey.MEDLEYENDBEAT,
            TagKey.CALCMEDLEY,
            TagKey.P1,
            TagKey.P2,
            TagKey.COMMENT
    );

    private static final Set<TagKey> V_1_1_0_SUPPORTED_KEYS = CollectionsUtils.join(
            V_1_0_0_SUPPORTED_KEYS,
            Set.of(TagKey.AUDIO, TagKey.VOCALS, TagKey.INSTRUMENTAL, TagKey.TAGS, TagKey.PROVIDEDBY)
    );

    private static final Set<TagKey> V_1_2_0_SUPPORTED_KEYS = CollectionsUtils.join(
            V_1_1_0_SUPPORTED_KEYS,
            Set.of(TagKey.AUDIOURL, TagKey.VIDEOURL, TagKey.COVERURL, TagKey.BACKGROUNDURL)
    );

    private static final Map<FormatSpecification, Set<String>> SUPPORTED_KEYS = Map.of(
            FormatSpecification.V_1_0_0, enumToString(V_1_0_0_SUPPORTED_KEYS),
            FormatSpecification.V_1_1_0, enumToString(V_1_1_0_SUPPORTED_KEYS),
            FormatSpecification.V_1_2_0, enumToString(V_1_2_0_SUPPORTED_KEYS)
    );

    private FormatSpecificationSupportedTags() {

    }

    private static Set<String> enumToString(Set<TagKey> tagKeySet) {
        return tagKeySet.stream()
                .map(Enum::toString)
                .collect(Collectors.toSet());
    }

    static boolean isSupported(FormatSpecification formatSpecification, String tagKey) {
        return formatSpecification == null || SUPPORTED_KEYS.get(formatSpecification).contains(tagKey);
    }
}
