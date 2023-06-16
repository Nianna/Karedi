package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.command.Command;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;

/**
 * This class is a workaround for bug in ListView/VirtualFlow.
 *
 * If the ListView contains more items than the list height's in pixels then automatic scroll to the trailing items
 * causes only one item to be rendered in the view. Fortunately manual scrolling fixes this issue.
 */
public class HistoryListViewSkin extends ListViewSkin<Command> {

    private static final double FAKE_SCROLL_DELTA = 100;

    public HistoryListViewSkin(ListView<Command> listView) {
        super(listView);
    }

    public void fixDisplay() {
        if (super.getVirtualFlow().getCellCount() >= (int) getVirtualFlow().getHeight()) {
            super.getVirtualFlow().scrollPixels(-FAKE_SCROLL_DELTA);
            super.getVirtualFlow().scrollPixels(FAKE_SCROLL_DELTA);
        }
    }
}
