package com.github.nianna.karedi.song.tag;

import java.util.Locale;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class Tag {
	private final String key;
	private ReadOnlyStringWrapper value = new ReadOnlyStringWrapper();

	public Tag(String key) {
		this.key = key.trim().toUpperCase(Locale.ROOT);
	}

	public Tag(String key, String value) {
		this(key);
		setValue(value);
	}

	public Tag(TagKey key, String value) {
		this(key.toString());
		setValue(value);
	}

	public String getKey() {
		return key;
	}

	public ReadOnlyStringProperty valueProperty() {
		return value.getReadOnlyProperty();
	}

	public final String getValue() {
		return value.get();
	}

	public final void setValue(String value) {
		this.value.set(value);
	}
}
