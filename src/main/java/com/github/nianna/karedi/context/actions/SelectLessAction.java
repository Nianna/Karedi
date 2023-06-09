package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SelectLessAction extends ContextfulKarediAction {

    SelectLessAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmptyOrContainsOnlyOneElement();
    }

    @Override
    protected void onAction(ActionEvent event) {
        makeSelectionConsecutive();
        findLastSelectedNote().ifPresent(this::deselectNote);
    }
}
