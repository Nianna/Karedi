package main.java.com.github.nianna.karedi.control;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class HCenterPane extends Pane {

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		for (Node node : getChildren()) {
			double translateX = (getWidth() - node.prefWidth(getHeight())) / 2;
			translateX = Math.max(translateX, getBoundsInParent().getMinX());
			node.setTranslateX(translateX);
		}
	}
}
