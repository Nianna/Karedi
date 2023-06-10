package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;

abstract class ClipboardAction extends ContextfulKarediAction {

    ClipboardAction(AppContext appContext) {
        super(appContext);
    }

    protected Song buildSongFromClipboard() {
        return appContext.loadFromClipboard();
    }
}
