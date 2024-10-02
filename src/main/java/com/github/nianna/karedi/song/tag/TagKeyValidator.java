package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.problem.TagProblem;
import com.github.nianna.karedi.problem.UnsupportedTagProblem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class TagKeyValidator {

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

    private TagKeyValidator() {

    }

    public static Optional<TagProblem> validate(String tag, FormatSpecification formatSpecification) {
        if (SUPPORTED_KEYS.get(formatSpecification) == null) {
            return Optional.empty();
        }

        boolean isKeySupported = TagKey.optionalValueOf(tag)
                .map(SUPPORTED_KEYS.get(formatSpecification)::contains)
                .orElse(false);

        if (!isKeySupported) {
            return Optional.of(new UnsupportedTagProblem(tag));
        }
        return Optional.empty();
    }

}
