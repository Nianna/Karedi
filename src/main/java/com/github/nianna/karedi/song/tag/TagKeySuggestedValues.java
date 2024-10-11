package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.util.Language;

import java.util.Arrays;
import java.util.List;

class TagKeySuggestedValues {

    private TagKeySuggestedValues() {

    }

    static List<?> forKey(TagKey tagKey) {
        return switch (tagKey) {
            case VERSION -> Arrays.asList(FormatSpecification.values());
            case LANGUAGE -> Arrays.stream(Language.values())
                    .filter(lang -> lang != Language.ESPANOL)
                    .toList();
            default -> List.of();
        };
    }
}
