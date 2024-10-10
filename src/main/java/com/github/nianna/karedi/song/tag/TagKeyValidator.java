package com.github.nianna.karedi.song.tag;

import com.github.nianna.karedi.problem.TagProblem;
import com.github.nianna.karedi.problem.UnsupportedTagProblem;

import java.util.Optional;

public abstract class TagKeyValidator {

    private TagKeyValidator() {

    }

    public static Optional<TagProblem> validate(String tagKey, FormatSpecification formatSpecification) {
        if (formatSpecification == null) {
            return Optional.empty();
        }

        if (!formatSpecification.supports(tagKey)) {
            return Optional.of(new UnsupportedTagProblem(tagKey));
        }
        return Optional.empty();
    }

}
