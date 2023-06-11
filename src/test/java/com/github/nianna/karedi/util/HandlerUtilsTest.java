package com.github.nianna.karedi.util;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HandlerUtilsTest {
	private static EventHandler<MouseEvent> handler1;
	private static EventHandler<MouseEvent> handler1consuming;
	private static EventHandler<MouseEvent> handler2;

	private static boolean firstExecuted;
	private static boolean secondExecuted;
	private static int lastExecuted;
	private static MouseEvent dummyEvent;

	@BeforeAll
	public static void classSetUp() {
		handler1 = (event) -> {
			firstExecuted = true;
			lastExecuted = 1;
		};
		handler1consuming = (event) -> {
			handler1.handle(event);
			event.consume();
		};
		handler2 = (event) -> {
			secondExecuted = true;
			lastExecuted = 2;
		};
	}

	@BeforeEach
	public void setUp() {
		firstExecuted = false;
		secondExecuted = false;
		lastExecuted = 0;
		dummyEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
				true, true, true, true, true, true, true, true, true, true, null);
	}

	@Test
	public void firstHandlerIsExecutedIfEventIsNotConsumed() {
		EventHandler<MouseEvent> handler = HandlerUtils.combineHandlers(handler2, handler1);
		handler.handle(dummyEvent);
		assertTrue(firstExecuted);
	}

	@Test
	public void secondHandlerIsExecutedIfEventIsNotConsumed() {
		EventHandler<MouseEvent> handler = HandlerUtils.combineHandlers(handler2, handler1);
		handler.handle(dummyEvent);
		assertTrue(secondExecuted);
	}

	@Test
	public void handlersAreExecutedInCorrectOrderIfEventIsNotConsumed() {
		EventHandler<MouseEvent> handler = HandlerUtils.combineHandlers(handler2, handler1);
		handler.handle(dummyEvent);
		Assertions.assertEquals(1, lastExecuted);
	}

	@Test
	public void secondHandlerIsNotExecutedIfEventWasConsumedByFirst() {
		EventHandler<MouseEvent> handler = HandlerUtils.combineHandlers(handler1consuming,
				handler2);
		handler.handle(dummyEvent);
		Assertions.assertFalse(secondExecuted);
	}

}
