package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;

public class DuetTagNotForDuetProblem extends TagProblem {

    public DuetTagNotForDuetProblem(String key) {
        super(Severity.WARNING, I18N.get("problem.tag.duet_not_for_duet.title", key), key);
    }

}
