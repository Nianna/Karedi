package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;

abstract class TagAction extends ContextfulKarediAction {

    TagAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeSongIsNull);
    }

}
