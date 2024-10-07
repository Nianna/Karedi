package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;

public class TagForNonexistentTrackProblem extends TagProblem {

    public TagForNonexistentTrackProblem(String key) {
        super(Severity.WARNING, I18N.get("problem.tag.nonexistent_track.title", key), key);
    }

}
