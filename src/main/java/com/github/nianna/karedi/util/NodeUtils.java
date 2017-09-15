package main.java.com.github.nianna.karedi.util;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import main.java.com.github.nianna.karedi.region.Direction;

public final class NodeUtils {
	private NodeUtils() {
	}

	public static void addOnMouseClicked(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseClicked(HandlerUtils.combineHandlers(node.getOnMouseClicked(), handler));
	}

	public static void addOnMouseDragged(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseDragged(HandlerUtils.combineHandlers(node.getOnMouseDragged(), handler));
	}

	public static void addOnMouseEntered(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseEntered(HandlerUtils.combineHandlers(node.getOnMouseEntered(), handler));
	}

	public static void addOnMouseExited(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseExited(HandlerUtils.combineHandlers(node.getOnMouseExited(), handler));
	}

	public static void addOnMouseMoved(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseMoved(HandlerUtils.combineHandlers(node.getOnMouseMoved(), handler));
	}

	public static void addOnMousePressed(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMousePressed(HandlerUtils.combineHandlers(node.getOnMousePressed(), handler));
	}

	public static void addOnMouseReleased(Node node, EventHandler<? super MouseEvent> handler) {
		node.setOnMouseReleased(HandlerUtils.combineHandlers(node.getOnMouseReleased(), handler));
	}

	public static DragHelper makeDraggable(Node node) {
		DragHelper helper = new DragHelper(node);

		addOnMousePressed(node, helper::onMousePressed);
		addOnMouseReleased(node, helper::onMouseReleased);
		addOnMouseDragged(node, helper::onMouseDragged);

		return helper;
	}

	public static ResizeHelper makeResizable(Node node, Insets margins) {
		ResizeHelper helper = new ResizeHelper(node, margins);

		addOnMousePressed(node, helper::onMousePressed);
		addOnMouseMoved(node, helper::onMouseMoved);
		addOnMouseReleased(node, helper::onMouseReleased);

		return helper;
	}

	public static CutHelper makeCuttable(Node node) {
		CutHelper helper = new CutHelper(node);

		addOnMouseMoved(node, helper::onMouseMoved);
		addOnMouseClicked(node, helper::onMouseClicked);

		return helper;
	}

	public static DragSelectionHelper enableRectangleSelection(Node node) {
		DragSelectionHelper helper = new DragSelectionHelper();

		addOnMousePressed(node, helper::onMousePressed);
		addOnMouseDragged(node, helper::onMouseDragged);
		addOnMouseReleased(node, helper::onMouseReleased);

		return helper;
	}

	public static class DragHelper {
		private Node node;
		private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
		private Point3D startPoint;
		private Point3D lastPoint;
		private Point3D curPoint;

		private DragHelper(Node node) {
			this.node = node;
		}

		public boolean isActive() {
			return active.get();
		}

		public ReadOnlyBooleanProperty activeProperty() {
			return active.getReadOnlyProperty();
		}

		private void onMousePressed(MouseEvent event) {
			node.setCursor(Cursor.MOVE);
			startPoint = new Point3D(event.getX(), event.getY(), event.getZ());
			curPoint = startPoint;
			active.set(true);
			event.consume();
		}

		private void onMouseReleased(MouseEvent event) {
			if (isActive()) {
				node.setCursor(Cursor.DEFAULT);
				startPoint = null;
				lastPoint = null;
				active.set(false);
				event.consume();
			}
		}

		private void onMouseDragged(MouseEvent event) {
			lastPoint = curPoint;
			curPoint = new Point3D(event.getX(), event.getY(), event.getZ());
		}

		public Point3D getStartPoint() {
			return startPoint;
		}

		public Point3D getLastPoint() {
			return lastPoint;
		}

		public Point3D getCurPoint() {
			return curPoint;
		}

		public Point3D getSceneStartPoint() {
			return node.localToScene(startPoint);
		}

		public Point3D getSceneLastPoint() {
			return node.localToScene(lastPoint);
		}

		public Point3D getSceneCurPoint() {
			return node.localToScene(curPoint);
		}
	}

	public static class ResizeHelper {
		private Insets margins;
		private Direction direction;
		private Node node;
		private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();

		private ResizeHelper(Node node, Insets margins) {
			this.node = node;
			setMargins(margins);
		}

		private void onMouseReleased(MouseEvent event) {
			if (isActive()) {
				deactivate();
				event.consume();
			}
		}

		private void onMouseMoved(MouseEvent event) {
			if (getEventDirection(event) != null) {
				switch (getEventDirection(event)) {
				case UP:
					node.setCursor(Cursor.N_RESIZE);
					break;
				case DOWN:
					node.setCursor(Cursor.S_RESIZE);
					break;
				case LEFT:
					node.setCursor(Cursor.W_RESIZE);
					break;
				case RIGHT:
					node.setCursor(Cursor.W_RESIZE);
					break;
				}
				event.consume();
			} else {
				node.setCursor(Cursor.DEFAULT);
			}
		}

		private void onMousePressed(MouseEvent event) {
			direction = getEventDirection(event);
			if (direction != null) {
				active.set(true);
				event.consume();
			}
		}

		private Direction getEventDirection(MouseEvent event) {
			Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
			double eventX = event.getSceneX() - boundsInScene.getMinX();
			double eventY = event.getSceneY() - boundsInScene.getMinY();

			double height = boundsInScene.getMaxY() - boundsInScene.getMinY();
			double width = boundsInScene.getMaxX() - boundsInScene.getMinX();

			boolean inRangeY = event.getY() >= 0 && event.getY() <= height;
			boolean inRangeX = event.getX() >= 0 && event.getX() <= width;

			if (eventY < margins.getTop() && inRangeX) {
				return Direction.UP;
			}
			if (eventY > height - margins.getBottom() && inRangeX) {
				return Direction.DOWN;
			}
			if (eventX < margins.getLeft() && inRangeY) {
				return Direction.LEFT;
			}
			if (eventX > width - margins.getRight() && inRangeY) {
				return Direction.RIGHT;
			}

			return null;
		}

		public Direction getDirection() {
			return direction;
		}

		public boolean isActive() {
			return active.get();
		}

		public ReadOnlyBooleanProperty activeProperty() {
			return active.getReadOnlyProperty();
		}

		public void setMargins(Insets margins) {
			this.margins = margins;
		}

		public void deactivate() {
			if (isActive()) {
				node.setCursor(Cursor.DEFAULT);
				direction = null;
				active.set(false);
			}
		}
	}

	public static class CutHelper {
		private Node node;
		private ReadOnlyDoubleWrapper cutSceneX = new ReadOnlyDoubleWrapper();

		private CutHelper(Node node) {
			this.node = node;
		}

		private void onMouseMoved(MouseEvent event) {
			node.setCursor(Cursor.TEXT);
			event.consume();
		}

		private void onMouseClicked(MouseEvent event) {
			cutSceneX.set(event.getSceneX());
			event.consume();
		}

		public ReadOnlyDoubleProperty cutSceneXProperty() {
			return cutSceneX.getReadOnlyProperty();
		}

		public double getCutSceneX() {
			return cutSceneX.get();
		}

	}

	public static class DragSelectionHelper {
		private double firstX;
		private double firstY;
		private Rectangle selectionArea = new Rectangle();
		private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
		private boolean preActive = false;

		private DragSelectionHelper() {
			setActive(false);
			selectionArea.getStyleClass().add("selection-area");
		}

		private void onMousePressed(MouseEvent event) {
			firstX = event.getSceneX();
			firstY = event.getSceneY();
			updateSelectionArea(firstX, firstY, 0, 0);
			preActive = true;
			event.consume();
		}

		private void updateSelectionArea(double x, double y, double width, double height) {
			Point2D localCoords = selectionArea.sceneToLocal(x, y);
			selectionArea.setX(localCoords.getX());
			selectionArea.setY(localCoords.getY());
			selectionArea.setWidth(width);
			selectionArea.setHeight(height);
		}

		private void onMouseDragged(MouseEvent event) {
			if (preActive) {
				setActive(true);
				preActive = false;
			}
			if (isActive()) {
				double secondX = event.getSceneX();
				double secondY = event.getSceneY();

				updateSelectionArea(Math.min(firstX, secondX), Math.min(firstY, secondY),
						Math.abs(firstX - secondX), Math.abs(firstY - secondY));
			}
		}

		private void onMouseReleased(MouseEvent event) {
			preActive = false;
			if (isActive()) {
				setActive(false);
				event.consume();
			}
		}

		private void setActive(boolean active) {
			selectionArea.setVisible(active);
			this.active.set(active);
		}

		public boolean isActive() {
			return active.get();
		}

		public ReadOnlyBooleanProperty activeProperty() {
			return active.getReadOnlyProperty();
		}

		public Node getSelectionAreaNode() {
			return selectionArea;
		}

		public Point2D getUpperLeftCorner() {
			return selectionArea
					.localToScene(new Point2D(selectionArea.getX(), selectionArea.getY()));
		}

		public Point2D getBottomRightCorner() {
			return selectionArea
					.localToScene(new Point2D(selectionArea.getX() + selectionArea.getWidth(),
							selectionArea.getY() + selectionArea.getHeight()));
		}

	}

}
