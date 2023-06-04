package main.java.com.github.nianna.karedi.display;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import main.java.com.github.nianna.karedi.Settings;
import main.java.com.github.nianna.karedi.song.Note.Type;

import static javafx.scene.effect.BlurType.ONE_PASS_BOX;

public class NoteNodeDisplayer extends Pane {
	private static final double DIMENSION_CHANGE_THRESHOLD = 2.5;
	private static final int BORDER_GLOW_DEPTH = 30;
	private static final Color DEFAULT_COLOR = Color.BLACK;
	private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

	private static final double CUT_BAR_HEIGHT = 5;

	private Rectangle cutBar = new Rectangle();
	private StackPane bar = new StackPane();
	private VBox barBackground = new VBox();
	private GridPane underBar = new GridPane();
	private Text lyrics = new Text();
	private Text length = new Text();
	private Text tone = new Text();
	private VBox noteBox = new VBox();

	private ObjectProperty<Color> color = new SimpleObjectProperty<>(DEFAULT_COLOR);
	private ObjectProperty<Color> fontColor = new SimpleObjectProperty<>(DEFAULT_FONT_COLOR);
	private FontPosture fontPosture = FontPosture.REGULAR;

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

		color.addListener(obs -> {
			repaintBar();
			repaintLyrics();
		});
		barHeightProperty().addListener(this::onBarHeightInvalidated);
		bar.widthProperty().addListener(this::onBarWidthInvalidated);

		repaintBar();

		length.getStyleClass().add("under-note-lyrics");
		tone.getStyleClass().add("under-note-lyrics");

		bar.getChildren().addAll(barBackground, lyrics);
		bar.setAlignment(Pos.CENTER);
		barBackground.maxWidthProperty().bind(bar.maxWidthProperty());
		barBackground.minWidthProperty().bind(bar.minWidthProperty());

		noteBox.getChildren().addAll(bar, underBar);
		underBar.add(tone, 0, 0);
		underBar.add(length, 1, 0);
		underBar.setHgap(5);
		underBar.setVisible(Settings.isDisplayNoteNodeUnderBarEnabled());
		GridPane.setHgrow(length, Priority.ALWAYS);
		GridPane.setHalignment(length, HPos.RIGHT);

		bar.heightProperty().addListener(this::onBarHeightChanged);

		getChildren().addAll(noteBox, cutBar);

		disabledProperty().addListener(this::onDisabledChanged);
		styleBorderGlow();
		styleCutBar();
		fontColor.addListener((observable, oldValue, newValue) -> repaintLyrics());
	}

	private void styleBorderGlow() {
		borderGlow.setWidth(BORDER_GLOW_DEPTH);
		borderGlow.setHeight(BORDER_GLOW_DEPTH);
		borderGlow.setSpread(0.6);
	}

	private void styleCutBar() {
		cutBar.setFill(Color.TRANSPARENT);
		cutBar.widthProperty().bind(barWidthProperty());
		cutBar.setHeight(CUT_BAR_HEIGHT);
	}

	private void onDisabledChanged(Observable obs, Boolean wasDisabled, Boolean isDisabled) {
		if (isDisabled) {
			barBackground.setOpacity(0.6);
			lyrics.setOpacity(0.25);
		} else {
			barBackground.setOpacity(1);
			lyrics.setOpacity(1);
		}
	}

	private void onBarHeightChanged(Observable obs, Number oldValue, Number newValue) {
		repaintLyrics();
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
		Background background = barBackground.getBackground();
		barBackground.setBackground(null);
		barBackground.setBackground(background);
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
		barBackground.setBackground(background);
		lastRefreshHeight = getBarHeight();
		lastRefreshWidth = getBarWidth();
	}

	private void repaintLyrics() {
		lyrics.setFont(
				Font.font(
						lyrics.getFont().getFamily(),
						FontWeight.BOLD,
						fontPosture,
						bar.getHeight() * 0.5
				)
		);
		lyrics.setFill(fontColor.get());
		Color outlineColor = isSelected ? getColor().grayscale() : getColor();
		lyrics.setEffect(new DropShadow(ONE_PASS_BOX, outlineColor, 2, 1, 0, 0));
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

	ObjectProperty<Color> fontColorProperty() {
		return fontColor;
	}

	StringProperty lyricsProperty() {
		return lyrics.textProperty();
	}

	final String getLyrics() {
		return lyricsProperty().get();
	}

	final void setLyrics(String value) {
		lyricsProperty().set(value);
	}

	StringProperty lengthProperty() {
		return length.textProperty();
	}

	StringProperty toneProperty() {
		return tone.textProperty();
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
					barBackground.setEffect(selectedEffect);
				}
			}
			isSelected = true;
			repaintLyrics();
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
					barBackground.setEffect(null);
				}
			}
			isSelected = false;
			repaintLyrics();
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
			lyrics.setUnderline(true);
		} else {
			lyrics.setUnderline(false);
		}
	}

	public void updateType(Type oldType, Type newType) {
		if (oldType != newType) {
			resetTypeEffect();
			setTypeEffect(newType);
			repaintBar();
			repaintLyrics();
		}
	}

	private void resetTypeEffect() {
		fontPosture = FontPosture.REGULAR;
		if (isSelected) {
			barBackground.setEffect(selectedEffect);
		} else {
			barBackground.setEffect(null);
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
		borderGlow.setInput(barBackground.getEffect());
		barBackground.setEffect(borderGlow);
		isStyled = true;
	}

	private void addFreestyleEffect() {
		borderGlow.setColor(Color.GRAY.darker());
		fontPosture = FontPosture.ITALIC;
		borderGlow.setInput(barBackground.getEffect());
		barBackground.setEffect(borderGlow);
		isStyled = true;
	}

	private void addRapEffect() {
		rapEffect.setInput(barBackground.getEffect());
		barBackground.setEffect(rapEffect);
		isRap = true;
	}

}
