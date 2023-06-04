package com.github.nianna.karedi.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuBuilder {
	private ContextMenu contextMenu = new ContextMenu();

	public MenuItem addItem(String title) {
		MenuItem menuItem = new MenuItem(title);
		contextMenu.getItems().add(menuItem);
		return menuItem;
	}

	public MenuItem addItem(String title, EventHandler<ActionEvent> handler) {
		MenuItem menuItem = addItem(title);
		menuItem.setOnAction(handler);
		return menuItem;
	}

	public MenuItem addItem(MenuItem menuItem) {
		contextMenu.getItems().add(menuItem);
		return menuItem;
	}

	public ContextMenu getResult() {
		return contextMenu;
	}

}
