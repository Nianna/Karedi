package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.tag.TagKey;

public class MandatoryTagMissingProblem extends TagProblem {

    public MandatoryTagMissingProblem(TagKey key) {
        super(Severity.ERROR, I18N.get("problem.tag.mandatory_missing.title", key), key);
    }

}
