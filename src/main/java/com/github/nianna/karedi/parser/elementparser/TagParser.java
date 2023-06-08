package com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;

import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * The default parser of the {@link TagElement}.
 * <p>
 * This element is represented by a single line which consists of two non-empty
 * strings separated by a colon, e.g. {@code "#TITLE:Be happy"}. They denote the
 * tag's key and value respectively.
 */
public class TagParser extends SongElementParser {

	private static final String TAG_PATTERN = "#(.+?):(.+)";

	public TagParser() {
		super(TAG_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher matcher) {
		String tagKey = matcher.group(1).toUpperCase();
		String tagValue = matcher.group(2);
		return new TagElement(tagKey, tagValue);
	}

}
