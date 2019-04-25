package main.java.com.github.nianna.karedi.display;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import javafx.util.StringConverter;
import main.java.com.github.nianna.karedi.control.Grid;
import main.java.com.github.nianna.karedi.util.MathUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainChart extends Region {

    private static final int MAX_MINOR_VERTICAL_LINES = 160;

    private final Line verticalZeroLine = new Line();
    private final Line horizontalZeroLine = new Line();

    private final Grid grid = new Grid();

    private final Group plotArea = new Group() {
        @Override
        public void requestLayout() {
        } // suppress layout requests
    };
    private final Group notesNodes = new Group();
    private final Group extraNodes = new Group();
    private final Rectangle plotAreaClip = new Rectangle();

    private final Pane content = new Pane() {
        @Override
        protected void layoutChildren() {
            final double top = snappedTopInset();
            final double left = snappedLeftInset();
            final double bottom = snappedBottomInset();
            final double right = snappedRightInset();
            final double width = getWidth();
            final double height = getHeight();
            final double contentWidth = snapSize(width - (left + right));
            final double contentHeight = snapSize(height - (top + bottom));
            layoutChart(snapPosition(top), snapPosition(left), contentWidth, contentHeight);
        }
    };
    private NumberAxis yAxis;
    private NumberAxis xAxis;
    private NumberAxis tAxis;

    private ReadOnlyDoubleWrapper xUnitLength = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper yUnitLength = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper tUnitLength = new ReadOnlyDoubleWrapper();

    public MainChart() {
        this(new NumberAxis(0, 20, 4), new NumberAxis(-8, 8, 1), new NumberAxis(0, 10, 1));
    }

    public MainChart(NumberAxis xAxis, NumberAxis yAxis, NumberAxis tAxis) {
        this.yAxis = yAxis;
        this.xAxis = xAxis;
        this.tAxis = tAxis;
        configureAxes();

        content.getChildren().addAll(plotArea, yAxis, xAxis, tAxis);
        content.getStyleClass().add("chart-content");
        getChildren().addAll(content);

        plotArea.setAutoSizeChildren(false);
        notesNodes.setAutoSizeChildren(false);
        plotAreaClip.setSmooth(false);
        plotArea.setClip(plotAreaClip);
        plotArea.getChildren().addAll(grid, verticalZeroLine, horizontalZeroLine, extraNodes,
                notesNodes);
        notesNodes.getStyleClass().setAll("plot-content");
        verticalZeroLine.getStyleClass().setAll("chart-vertical-zero-line");
        horizontalZeroLine.getStyleClass().setAll("chart-horizontal-zero-line");
        notesNodes.setManaged(false);
        extraNodes.setManaged(false);
        plotArea.setManaged(false);

        setXAxisTickLabelFormatter();
        setTAxisTickLabelFormatter();

        content.setManaged(false);
    }

    private void setXAxisTickLabelFormatter() {
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.intValue() == xAxis.getLowerBound()) {
                    return object.intValue() + " ";
                }
                if (object.intValue() == xAxis.getUpperBound()) {
                    return " " + object.intValue();
                }
                if (object.intValue() % 4 == 0) {
                    return (object.intValue() + "");
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
    }

    private void setTAxisTickLabelFormatter() {
        tAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return (MathUtils.roundTo(object.doubleValue(), 3) + "s ");
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
    }

    private void configureAxes() {
        xAxis.setSide(Side.BOTTOM);
        yAxis.setSide(Side.LEFT);
        tAxis.setSide(Side.TOP);

        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        tAxis.setAnimated(false);

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        tAxis.setAutoRanging(false);

        xAxis.setMinorTickCount(1);
        xAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        tAxis.setMinorTickCount(8);
        tAxis.setTickUnit(0.5);
        tAxis.setMinorTickVisible(false);
    }

    private void layoutAxes(double top, double left, double width, double height) {

        double xAxisHeight;
        double xAxisWidth;
        double yAxisWidth;
        double yAxisHeight;
        double tAxisWidth;
        double tAxisHeight;
        double newYAxisWidth;

        do {
            yAxisWidth = yAxis.prefWidth(height);
            xAxisWidth = width - yAxisWidth;
            xAxisHeight = xAxis.prefHeight(xAxisWidth);
            tAxisWidth = xAxisWidth;
            tAxisHeight = tAxis.prefHeight(tAxisWidth);
            yAxisHeight = height - tAxisHeight - xAxisHeight;
            newYAxisWidth = yAxis.prefWidth(yAxisHeight);
        } while (newYAxisWidth != yAxisWidth);

        xAxisWidth = Math.ceil(xAxisWidth);
        xAxisHeight = Math.ceil(xAxisHeight);
        yAxisWidth = Math.ceil(yAxisWidth);
        yAxisHeight = Math.ceil(yAxisHeight);
        tAxisWidth = Math.ceil(tAxisWidth);
        tAxisHeight = Math.ceil(tAxisHeight);

        xAxis.resizeRelocate(left + yAxisWidth, top + tAxisHeight + yAxisHeight, xAxisWidth,
                xAxisHeight);
        yAxis.resizeRelocate(left + 1, top + tAxisHeight, yAxisWidth, yAxisHeight);
        tAxis.resizeRelocate(left + yAxisWidth, top + 1, tAxisWidth, tAxisHeight);

        refreshAxesLayout();
    }

    private void refreshAxesLayout() {
        xAxis.requestAxisLayout();
        xAxis.layout();
        yAxis.requestAxisLayout();
        yAxis.layout();
        tAxis.requestAxisLayout();
        tAxis.layout();
    }

    private void layoutChart(double top, double left, double width, double height) {
        layoutAxes(top, left, width, height);

        xUnitLength.set(xAxis.getWidth() / (xAxis.getUpperBound() - xAxis.getLowerBound()));
        yUnitLength.set(yAxis.getHeight() / (yAxis.getUpperBound() - yAxis.getLowerBound()));
        tUnitLength.set(tAxis.getWidth() / (tAxis.getUpperBound() - tAxis.getLowerBound()));

        left += yAxis.getWidth();
        top += tAxis.getHeight();

        layoutGrid(left, top);
        layoutPlotAreaClip(left, top);
        layoutNotesNodes(left, top);
        layoutExtraNodes(left, top);
        layoutVerticalZeroLine(left, top);
        layoutHorizontalZeroLine(left, top);
    }

    private void layoutHorizontalZeroLine(double left, double top) {
        double yAxisZero = yAxis.getDisplayPosition(0);
        horizontalZeroLine.setStartX(left);
        horizontalZeroLine.setStartY(top + yAxisZero + 0.5);
        horizontalZeroLine.setEndX(left + xAxis.getWidth());
        horizontalZeroLine.setEndY(top + yAxisZero + 0.5);
        horizontalZeroLine.setVisible(true);
    }

    private void layoutVerticalZeroLine(double left, double top) {
        double xAxisZero = xAxis.getDisplayPosition(0);
        verticalZeroLine.setStartX(left + xAxisZero + 0.5);
        verticalZeroLine.setStartY(top);
        verticalZeroLine.setEndX(left + xAxisZero + 0.5);
        verticalZeroLine.setEndY(top + yAxis.getHeight());
        verticalZeroLine.setVisible(true);
    }

    private void layoutExtraNodes(double left, double top) {
        extraNodes.setLayoutX(left);
        extraNodes.setLayoutY(top);
        extraNodes.setTranslateX(tAxis.getDisplayPosition(0));
        extraNodes.requestLayout();
    }

    private void layoutNotesNodes(double left, double top) {
        notesNodes.setLayoutX(left);
        notesNodes.setLayoutY(top);
        notesNodes.setTranslateX(xAxis.getDisplayPosition(0));
        notesNodes.setTranslateY(yAxis.getDisplayPosition(0));
        notesNodes.requestLayout();
    }

    private void layoutPlotAreaClip(double left, double top) {
        plotAreaClip.setX(left);
        plotAreaClip.setY(top);
        plotAreaClip.setWidth(xAxis.getWidth() + 1);
        plotAreaClip.setHeight(yAxis.getHeight() + 1);
    }

    private void layoutGrid(double left, double top) {
        grid.setTranslateX(left);
        grid.setTranslateY(top);
        drawVerticalLines();
        drawHorizontalLines();
    }

    private void drawVerticalLines() {
        Stream<Axis.TickMark<Number>> verticalMarksStream = xAxis.getTickMarks().stream();
        if (xAxis.getTickMarks().size() > MAX_MINOR_VERTICAL_LINES) {
            verticalMarksStream = verticalMarksStream.filter(tick -> tick.getValue().intValue() % 4 == 0);
        }
        List<Pair<Integer, Double>> verticalMarks = verticalMarksStream.map(tick -> new Pair<>(tick.getValue().intValue(), xAxis.getDisplayPosition(tick.getValue())))
                .collect(Collectors.toList());
        grid.drawVerticalLines(verticalMarks, yAxis.getHeight());
    }

    private void drawHorizontalLines() {
        List<Double> horizontalMarks = yAxis.getTickMarks()
                .subList(1, yAxis.getTickMarks().size() - 1).stream()
                .mapToDouble(tick -> yAxis.getDisplayPosition(tick.getValue())).boxed()
                .collect(Collectors.toList());
        grid.drawHorizontalLines(horizontalMarks, xAxis.getWidth());
    }

    @Override
    protected void layoutChildren() {
        final double top = snappedTopInset();
        final double left = snappedLeftInset();
        final double bottom = snappedBottomInset();
        final double right = snappedRightInset();
        final double width = getWidth();
        final double height = getHeight();
        content.resizeRelocate(left, top, width - left - right, height - top - bottom);
    }

    public NumberAxis getYAxis() {
        return yAxis;
    }

    public NumberAxis getXAxis() {
        return xAxis;
    }

    public NumberAxis getTAxis() {
        return tAxis;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 100;
    }

    @Override
    protected double computeMinWidth(double height) {
        return 100;
    }

    public ReadOnlyDoubleProperty yUnitLengthProperty() {
        return yUnitLength.getReadOnlyProperty();
    }

    public final Double getYUnitLength() {
        return yUnitLength.get();
    }

    public ReadOnlyDoubleProperty xUnitLengthProperty() {
        return xUnitLength.getReadOnlyProperty();
    }

    public final Double getXUnitLength() {
        return xUnitLength.get();
    }

    public ReadOnlyDoubleProperty tUnitLengthProperty() {
        return tUnitLength.getReadOnlyProperty();
    }

    public final Double getTUnitLength() {
        return tUnitLength.get();
    }

    public ObservableList<Node> getChartChildren() {
        return notesNodes.getChildren();
    }

    public void addToPlotArea(Node node) {
        extraNodes.getChildren().add(node);
    }

    public ObservableList<Node> getPlotAreaChildren() {
        return extraNodes.getChildren();
    }
}
