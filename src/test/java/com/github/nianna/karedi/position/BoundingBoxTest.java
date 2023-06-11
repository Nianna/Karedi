package com.github.nianna.karedi.position;

import com.github.nianna.karedi.region.Bounded;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoundingBoxTest {
	BoundingBox<IntBounded> testBox;
	Bounded<Integer> testBounded;

	@BeforeEach
	public void setUp() {
		// testBox = new BoundingBox();
	}

	@Test
	public void boxPropertiesShouldBeCorrectlyBoundToConstructorParameters() {
		ReadOnlyObjectWrapper<Integer> minX = new ReadOnlyObjectWrapper<Integer>();
		ReadOnlyObjectWrapper<Integer> minY = new ReadOnlyObjectWrapper<Integer>();
		ReadOnlyObjectWrapper<Integer> maxX = new ReadOnlyObjectWrapper<Integer>();
		ReadOnlyObjectWrapper<Integer> maxY = new ReadOnlyObjectWrapper<Integer>();
		Bounded<Integer> bounded = BoundingBox.boundsOf(minX, maxX, minY, maxY);
		Random generator = new Random();
		minX.set(generator.nextInt());
		minY.set(generator.nextInt());
		maxX.set(generator.nextInt());
		maxY.set(generator.nextInt());
		assertEquals(minX.get(), bounded.lowerXBoundProperty().get());
		assertEquals(minY.get(), bounded.lowerYBoundProperty().get());
		assertEquals(maxX.get(), bounded.upperXBoundProperty().get());
		assertEquals(maxY.get(), bounded.upperYBoundProperty().get());
	}

	@Test
	public void emptyBoundingBoxShouldHaveNullBounds() {
		// BoundingBox box ;
	}

}