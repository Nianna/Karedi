package test.java.com.github.nianna.karedi.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.com.github.nianna.karedi.util.BindingsUtils;

public class BindingsUtilsTest {
	ObservableList<Integer> list = FXCollections.observableArrayList();

	@Before
	public void setUp() {
		list.clear();
		list.addAll(1, 2, 3);
	}

	@Test
	public void valueAtIndexBiggerThanListSizeIsNull() {
		Assert.assertNull(BindingsUtils.valueAt(list, list.size()).get());
	}

	@Test
	public void valueAtNegativeIndexIsNull() {
		Assert.assertNull(BindingsUtils.valueAt(list, -1).get());
	}

	@Test
	public void valueAtASpecifiedPositionIsSameAsInTheList() {
		Assert.assertSame(list.get(2), BindingsUtils.valueAt(list, 2).get());
	}

	@Test
	public void valueAtIndexThatBecomesOutOfBoundsChangesToNull() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, list.size() - 1);
		Assert.assertNotNull(valueAt.get());
		list.remove(list.size() - 1);
		Assert.assertNull(valueAt.get());
	}

	@Test
	public void valueAtIndexThatFallsIntoBoundsStopsBeingNull() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, list.size());
		Assert.assertNull(valueAt.get());
		list.add(4);
		Assert.assertNotNull(valueAt.get());
	}

	@Test
	public void valueAtIndexInBoundsUpdatesCorrectly() {
		ObjectBinding<Integer> valueAt = BindingsUtils.valueAt(list, 0);
		Integer newVal = list.get(0) * 2;
		list.set(0, newVal);
		Assert.assertEquals(newVal, valueAt.get());
	}

	@Test
	public void sizeOfStoresTheCorrectSize() {
		Assert.assertEquals(list.size(), BindingsUtils.sizeOf(list).get());
	}

	@Test
	public void sizeOfUpdatesCorrectly() {
		IntegerBinding sizeOf = BindingsUtils.sizeOf(list);
		list.addAll(4, 5, 6, 7);
		Assert.assertEquals(list.size(), sizeOf.get());
	}

	@Test
	public void isEmptyIsFalseForNonEmptyLists() {
		Assert.assertEquals(list.isEmpty(), BindingsUtils.isEmpty(list).get());
	}

	@Test
	public void isEmptyIsTrueForEmptyLists() {
		Assert.assertTrue(BindingsUtils.isEmpty(FXCollections.observableArrayList()).get());
	}

	@Test
	public void isEmptyBecomesTrueAfterClearingTheList() {
		BooleanBinding isEmpty = BindingsUtils.isEmpty(list);
		list.clear();
		Assert.assertTrue(isEmpty.get());
	}

	@Test
	public void isEmptyBecomesFalseAfterAddingAnElementToTheList() {
		ObservableList<Integer> emptyList = FXCollections.observableArrayList();
		BooleanBinding isEmpty = BindingsUtils.isEmpty(emptyList);
		emptyList.add(20);
		Assert.assertFalse(isEmpty.get());
	}

}
