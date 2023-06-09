package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.context.AppContext;

public abstract class ContextfulKarediAction extends KarediAction {

    protected final AppContext appContext;

    ContextfulKarediAction(AppContext appContext) {
        this.appContext = appContext;
    }
}
