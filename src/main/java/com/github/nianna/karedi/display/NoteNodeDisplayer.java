package main.java.com.github.nianna.karedi.display;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import main.java.com.github.nianna.karedi.song.Note.Type;

public class NoteNodeDisplayer extends Pane {
	private static final double DIMENSION_CHANGE_THRESHOLD = 2.5;
	private static final int BORDER_GLOW_DEPTH = 40;
	private static final Color DEFAULT_COLOR = Color.BLACK;

	private static final double CUT_BAR_HEIGHT = 5;

	private static final String FREESTYLE_CLASS = "freestyle";
	private static final String UNDERLINED_CLASS = "underlined";

	private Rectangle cutBar = new Rectangle();
	private Region bar = new Region();
	private Text lyrics = new Text();
	private VBox noteBox = new VBox();

	private ObjectProperty<Color> color = new SimpleObjectProperty<>(DEFAULT_COLOR);

	private DropShadow borderGlow = new DropShadow();
	private SepiaTone selectedEffect = new SepiaTone();
	private GaussianBlur rapEffect = new GaussianBlur(5);

	private boolean isSelected;
	private boolean isStyled;
	private boolean isRap;

	private double lastRefreshHeight;
	private double lastRefreshWidth;

	NoteNodeDisplayer() {
		super();
		this.setFocusTraversable(true);
		this.setStyle("");

		color.addListener(obs -> repaintBar());
		barHeightProperty().addListener(this::onBarHeightInvalidated);
		bar.widthProperty().addListener(this::onBarWidthInvalidated);

		bar.setPadding(new Insets(5));
		repaintBar();

		lyrics.getStyleClass().add("note-lyrics");

		noteBox.getChildren().addAll(bar, lyrics);
		noteBox.setAlignment(Pos.BASELINE_CENTER);

		getChildren().addAll(noteBox, cutBar);

		disabledProperty().addListener(this::onDisabledChanged);
		styleBorderGlow();
		styleCutBar();
	}

	private void styleBorderGlow() {
		borderGlow.setWidth(BORDER_GLOW_DEPTH);
		borderGlow.setHeight(BORDER_GLOW_DEPTH);
	}

	private void styleCutBar() {
		cutBar.setFill(Color.TRANSPARENT);
		cutBar.widthProperty().bind(barWidthProperty());
		cutBar.setHeight(CUT_BAR_HEIGHT);
	}

	private void onDisabledChanged(Observable obs, Boolean wasDisabled, Boolean isDisabled) {
		if (isDisabled) {
			bar.setOpacity(0.6);
			lyrics.setOpacity(0.25);
		} else {
			bar.setOpacity(1);
			lyrics.setOpacity(1);
		}
	}

	private void onBarHeightInvalidated(Observable obs) {
		if (Math.abs(getBarHeight() - lastRefreshHeight) > DIMENSION_CHANGE_THRESHOLD) {
			refreshBar();
		}
	}

	private void onBarWidthInvalidated(Observable obs) {
		if (Math.abs(getBarWidth() - lastRefreshWidth) > DIMENSION_CHANGE_THRESHOLD) {
			refreshBar();
		}
	}

	private void refreshBar() {
		// Refresh background to force repainting (corner radii)
		Background background = bar.getBackground();
		bar.setBackground(null);
		bar.setBackground(background);
		lastRefreshHeight = getBarHeight();
		lastRefreshWidth = getBarWidth();
	}

	private void repaintBar() {
		Color color = getColor();
		if (isRap) {
			color = color.darker();
		}
		LinearGradient grad1 = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, color.deriveColor(0, 1.0, 0.6, 1.0)),
				new Stop(1, color.deriveColor(0, 1.0, 0.5, 1.0)));
		LinearGradient grad2 = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, color.deriveColor(0, 0.3, 1.0, 1.0)),
				new Stop(1, color.deriveColor(0, 0.9, 1.0, 1.0)));
		LinearGradient grad3 = new LinearGradient(0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
				new Stop(0, color.deriveColor(0, 0.8, 1.0, 1.0)),
				new Stop(1, color.deriveColor(0, 1.0, 0.9, 1.0)));
		BackgroundFill fill1 = new BackgroundFill(grad1, new CornerRadii(10), new Insets(0));
		BackgroundFill fill2 = new BackgroundFill(grad2, new CornerRadii(10), new Insets(1));
		BackgroundFill fill3 = new BackgroundFill(grad3, new CornerRadii(10), new Insets(2));
		Background background = new Background(fill1, fill2, fill3);
		bar.setBackground(background);
		lastRefreshHeight = getBarHeight();
		lastRefreshWidth = getBarWidth();
	}

	ObjectProperty<Color> colorProperty() {
		return color;
	}

	final Color getColor() {
		return colorProperty().get();
	}

	final void setColor(Color value) {
		colorProperty().set(value);
	}

	StringProperty textProperty() {
		return lyrics.textProperty();
	}

	final String getText() {
		return textProperty().get();
	}

	final void setText(String value) {
		textProperty().set(value);
	}

	@Override
	protected void layoutChildren() {
		cutBar.resizeRelocate(1, -getBarHeight() / 2 - cutBar.getHeight(), getBarWidth(),
				getBarHeight());
		noteBox.resizeRelocate(1, -getBarHeight() / 2, getBarWidth(), getBarHeight());
	}

	DoubleProperty barHeightProperty() {
		return bar.minHeightProperty();
	}

	final Double getBarHeight() {
		return barHeightProperty().get();
	}

	final void setBarHeight(double value) {
		barHeightProperty().set(value);
	}

	DoubleProperty barWidthProperty() {
		return bar.minWidthProperty();
	}

	final Double getBarWidth() {
		return barWidthProperty().get();
	}

	final void setBarWidth(double value) {
		barWidthProperty().set(value);
	}

	void select() {
		if (!isSelected) {
			if (isStyled) {
				borderGlow.setInput(selectedEffect);
			} else {
				if (isRap) {
					rapEffect.setInput(selectedEffect);
				} else {
					bar.setEffect(selectedEffect);
				}
			}
			isSelected = true;
		}
	}

	void deselect() {
		if (isSelected) {
			if (isStyled) {
				borderGlow.setInput(null);
			} else {
				if (isRap) {
					rapEffect.setInput(null);
				} else {
					bar.setEffect(null);
				}
			}
			isSelected = false;
		}
	}

	Node getBar() {
		return bar;
	}

	Node getCutBar() {
		return cutBar;
	}

	void breaksLine(boolean breaksLine) {
		if (breaksLine) {
			lyrics.getStyleClass().add(UNDERLINED_CLASS);
		} else {
			lyrics.getStyleClass().remove(UNDERLINED_CLASS);
		}
	}

	public void updateType(Type oldType, Type newType) {
		if (oldType != newType) {
			resetTypeEffect(oldType);
			setTypeEffect(newType);
			repaintBar();
		}
	}

	private void resetTypeEffect(Type type) {
		lyrics.getStyleClass().remove(FREESTYLE_CLASS);
		if (isSelected) {
			bar.setEffect(selectedEffect);
		} else {
			bar.setEffect(null);
		}
		isStyled = false;
		isRap = false;
	}

	private void setTypeEffect(Type type) {
		// Styles are set like this: rap -> golden/freestyle -> selected
		if (type != null) {
			switch (type) {
			case FREESTYLE:
				addFreestyleEffect();
				break;
			case GOLDEN:
				addGoldenEffect();
				break;
			case GOLDEN_RAP:
				addGoldenEffect();
				// fall through
			case RAP:
				addRapEffect();
				break;
			default:
			}
		}
	}

	private void addGoldenEffect() {
		borderGlow.setColor(Color.YELLOW);
		borderGlow.setInput(bar.getEffect());
		bar.setEffect(borderGlow);
		isStyled = true;
	}

	private void addFreestyleEffect() {
		borderGlow.setColor(Color.GRAY.darker());
		lyrics.getStyleClass().add(FREESTYLE_CLASS);
		borderGlow.setInput(bar.getEffect());
		bar.setEffect(borderGlow);
		isStyled = true;
	}

	private void addRapEffect() {
		rapEffect.setInput(bar.getEffect());
		bar.setEffect(rapEffect);
		isRap = true;
	}

}
