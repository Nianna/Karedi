package com.github.nianna.karedi.song;

import com.github.nianna.karedi.problem.InconsistentTagsProblem;
import com.github.nianna.karedi.problem.TagProblem;
import com.github.nianna.karedi.song.tag.TagKey;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class DuplicatedTagsConsistencyValidator {

    private static final Map<TagKey, TagKey> TAG_DUPLICATES = Map.of(
            TagKey.MP3, TagKey.AUDIO,
            TagKey.AUDIO, TagKey.MP3,
            TagKey.P1, TagKey.DUETSINGERP1,
            TagKey.DUETSINGERP1, TagKey.P1,
            TagKey.P2, TagKey.DUETSINGERP2,
            TagKey.DUETSINGERP2, TagKey.P2
    );

    private DuplicatedTagsConsistencyValidator() {

    }

    public static Optional<TagProblem> validate(Song song, TagKey key) {
        if (TAG_DUPLICATES.containsKey(key)) {
            String tagValue = song.getTagValue(key).orElseThrow();
            String similarTagValue = song.getTagValue(TAG_DUPLICATES.get(key)).orElse(null);
            if (nonNull(similarTagValue) && !tagValue.equals(similarTagValue)) {
                return Optional.of(new InconsistentTagsProblem(key, TAG_DUPLICATES.get(key)));
            }
        }
        return Optional.empty();
    }

}
