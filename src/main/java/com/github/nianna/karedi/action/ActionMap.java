package com.github.nianna.karedi.action;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;

public class ActionMap {
	private Map<String, KarediAction> actionMap = new HashMap<>();

	private KarediAction dummyAction;

	public ActionMap() {
		dummyAction = new KarediAction() {
			@Override
			protected void onAction(ActionEvent event) {
			}
		};
		dummyAction.setDisabled(true);
	}

	public KarediAction get(String key) {
		return actionMap.getOrDefault(key, dummyAction);
	}

	public void put(String key, KarediAction value) {
		actionMap.put(key, value);
	}

}
