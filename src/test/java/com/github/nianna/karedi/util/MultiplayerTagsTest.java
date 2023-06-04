package com.github.nianna.karedi.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.util.MultiplayerTags;

public class MultiplayerTagsTest {

	@Test
	public void recognizesValidNameTagKeys() {
		Tag tag = new Tag("DUETSINGERP2", "");
		Assert.assertEquals(true, MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void otherTagKeysAreNotRecognizedAsNameTags() {
		Tag tag = new Tag("ARTIST", "");
		Assert.assertEquals(false, MultiplayerTags.isANameTag(tag));
	}

	@Test
	public void createsNameTagsForTracksWithCustomizedNames() {
		Assert.assertTrue(MultiplayerTags.nameTagForTrack(0, "Custom name").isPresent());
	}

	@Test
	public void createsCorrectNameTagsForTracksWithCustomizedNames() {
		String name = "Foo";
		Tag result = MultiplayerTags.nameTagForTrack(0, name).get();
		Assert.assertEquals("Wrong key", "DUETSINGERP1", result.getKey());
		Assert.assertEquals("Wrong value", name, result.getValue());
	}

	@Test
	public void doesNotCreateNameTagsForTracksWithDefaultNames() {
		// Track number n is meant for player (n+1)
		int index = 3;
		SongTrack track = new SongTrack(index + 1);
		Assert.assertFalse(MultiplayerTags.nameTagForTrack(index, track.getName()).isPresent());
	}

	@Test
	public void extractsCorrectPlayerNumbersFromValidNameTags() {
		Tag tag = new Tag("DUETSINGERP3", "");
		Optional<Integer> result = MultiplayerTags.getPlayerNumber(tag);
		Assert.assertTrue("Some number should have been extracted", result.isPresent());
		Assert.assertEquals("Wrong number", (Integer) 3, result.get());
	}

	@Test
	public void doesNotExtractPlayerNumbersFromOtherTags() {
		Tag tag = new Tag("ARTIST", "");
		Assert.assertFalse(MultiplayerTags.getPlayerNumber(tag).isPresent());
	}

	@Test
	public void extractsCorrectTrackNumbersFromValidNameTags() {
		Tag tag = new Tag("DUETSINGERP3", "");
		Optional<Integer> result = MultiplayerTags.getTrackNumber(tag);
		Assert.assertTrue("Some number should have been extracted", result.isPresent());
		Assert.assertEquals("Wrong number", (Integer) 2, result.get());
	}

	@Test
	public void doesNotExtractTrackNumbersFromOtherTags() {
		Tag tag = new Tag("ARTIST", "");
		Assert.assertFalse(MultiplayerTags.getTrackNumber(tag).isPresent());
	}

}
