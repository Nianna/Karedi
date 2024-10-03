package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.tag.TagKey;

public class InconsistentTagsProblem extends TagProblem {

    public InconsistentTagsProblem(TagKey key, TagKey otherKey) {
        super(Severity.ERROR, I18N.get("problem.tag.inconsistent.title", key, otherKey), key, otherKey);
        setDescription(I18N.get("problem.tag.inconsistent.description"));
    }

}
