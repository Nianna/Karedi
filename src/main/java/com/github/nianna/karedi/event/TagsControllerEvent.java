package com.github.nianna.karedi.event;

import java.util.List;

public class TagsControllerEvent extends ControllerEvent {

    private final List<String> affectedKeys;

    public TagsControllerEvent(List<String> affectedKeys) {
        super(FOCUS_TAGS_TABLE);
        this.affectedKeys = affectedKeys;
    }

    public List<String> getAffectedKeys() {
        return affectedKeys;
    }
}
