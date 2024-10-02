package com.github.nianna.karedi.problem;

import com.github.nianna.karedi.I18N;

public class UnsupportedTagProblem extends TagProblem {

    public UnsupportedTagProblem(String key) {
        super(Severity.ERROR, I18N.get("problem.tag.unsupported.title", key), key);
    }

}
