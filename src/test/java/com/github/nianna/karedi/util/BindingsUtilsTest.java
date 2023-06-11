package com.github.nianna.karedi.util;


import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BindingsUtilsTest {
	ObservableList<Integer> list = FXCollections.observableArrayList();

	@BeforeEach
	public void setUp() {
		list.clear();
		list.addAll(1, 2, 3);
	}

	@Test
	public void valueAtIndexBiggerThanListSizeIsNull() {
		assertNull(BindingsUtils.valueAt(list, list.size()).get());
	}

	@Test
	public void valueAtNegativeIndexIsNull() {
		assertNull(BindingsUtils.valueAt(list, -1).get());
	}

	@Test
	public void valueAtASpecifiedPositionIsSameAsInTheList() {
		assertSame(list.get(2), BindingsUtils.valueAt(list, 2).get());
	}

	@Test
	public void valueAtIndexThatBecomesOutOfBoundsChangesToNull() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, list.size() - 1);
		assertNotNull(valueAt.get());
		list.remove(list.size() - 1);
		assertNull(valueAt.get());
	}

	@Test
	public void valueAtIndexThatFallsIntoBoundsStopsBeingNull() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, list.size());
		assertNull(valueAt.get());
		list.add(4);
		assertNotNull(valueAt.get());
	}

	@Test
	public void valueAtIndexInBoundsUpdatesCorrectly() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, 0);
		Integer newVal = list.get(0) * 2;
		list.set(0, newVal);
		assertEquals(newVal, valueAt.get());
	}

	@Test
	public void sizeOfStoresTheCorrectSize() {
		assertEquals(list.size(), BindingsUtils.sizeOf(list).get());
	}

	@Test
	public void sizeOfUpdatesCorrectly() {
		IntegerBinding sizeOf = BindingsUtils.sizeOf(list);
		list.addAll(4, 5, 6, 7);
		assertEquals(list.size(), sizeOf.get());
	}

	@Test
	public void isEmptyIsFalseForNonEmptyLists() {
		assertEquals(list.isEmpty(), BindingsUtils.isEmpty(list).get());
	}

	@Test
	public void isEmptyIsTrueForEmptyLists() {
		assertTrue(BindingsUtils.isEmpty(FXCollections.observableArrayList()).get());
	}

	@Test
	public void isEmptyBecomesTrueAfterClearingTheList() {
		BooleanBinding isEmpty = BindingsUtils.isEmpty(list);
		list.clear();
		assertTrue(isEmpty.get());
	}

	@Test
	public void isEmptyBecomesFalseAfterAddingAnElementToTheList() {
		ObservableList<Integer> emptyList = FXCollections.observableArrayList();
		BooleanBinding isEmpty = BindingsUtils.isEmpty(emptyList);
		emptyList.add(20);
		assertFalse(isEmpty.get());
	}

}
