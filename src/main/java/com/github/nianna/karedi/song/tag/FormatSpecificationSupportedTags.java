package com.github.nianna.karedi.song.tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

class FormatSpecificationSupportedTags {

    private static final List<TagKey> V_1_0_0_SUPPORTED_KEYS = List.of(
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

    private static final List<TagKey> V_1_1_0_SUPPORTED_KEYS = Stream.concat(
            V_1_0_0_SUPPORTED_KEYS.stream(),
            Stream.of(TagKey.AUDIO, TagKey.VOCALS, TagKey.INSTRUMENTAL, TagKey.TAGS, TagKey.PROVIDEDBY)
    ).toList();

    private static final Map<FormatSpecification, List<TagKey>> SUPPORTED_KEYS = Map.of(
            FormatSpecification.V_1_0_0, V_1_0_0_SUPPORTED_KEYS,
            FormatSpecification.V_1_1_0, V_1_1_0_SUPPORTED_KEYS
    );

    private FormatSpecificationSupportedTags() {

    }

    static boolean isSupported(FormatSpecification formatSpecification, TagKey tagKey) {
        return SUPPORTED_KEYS.get(formatSpecification).contains(tagKey);
    }
}
