package com.github.nianna.karedi.context;

import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.actions.ActionHelper;

public class ActionContext {

    private final ActionHelper actionHelper;

    public ActionContext(AppContext appContext) {
        this.actionHelper = new ActionHelper(appContext);
    }

    public void initActions() {
        actionHelper.initActions();
    }

    public void addAction(KarediActions key, KarediAction action) {
        actionHelper.add(key, action);
    }

    public KarediAction getAction(KarediActions key) {
        return actionHelper.get(key);
    }

    public void execute(KarediActions action) {
        actionHelper.execute(action);
    }

    public boolean canExecute(KarediActions action) {
        return actionHelper.canExecute(action);
    }

}
