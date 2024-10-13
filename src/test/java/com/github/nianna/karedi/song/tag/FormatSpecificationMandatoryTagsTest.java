package com.github.nianna.karedi.song.tag;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FormatSpecificationMandatoryTagsTest {

    @Test
    void shouldReturnMandatoryKeysForNullFormat() {
        Set<TagKey> result = FormatSpecificationMandatoryTags.forFormat(null);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(Set.of(TagKey.ARTIST, TagKey.TITLE, TagKey.BPM, TagKey.MP3)));
    }

    @Test
    void shouldReturnMandatoryKeysForV1_0_0Format() {
        Set<TagKey> result = FormatSpecificationMandatoryTags.forFormat(FormatSpecification.V_1_0_0);
        assertEquals(5, result.size());
        assertTrue(result.containsAll(Set.of(TagKey.ARTIST, TagKey.TITLE, TagKey.BPM, TagKey.MP3, TagKey.VERSION)));
    }

    @Test
    void shouldReturnMandatoryKeysForV1_1_0Format() {
        Set<TagKey> result = FormatSpecificationMandatoryTags.forFormat(FormatSpecification.V_1_1_0);
        assertEquals(6, result.size());
        assertTrue(result.containsAll(Set.of(TagKey.ARTIST, TagKey.TITLE, TagKey.BPM, TagKey.MP3, TagKey.VERSION,
                TagKey.AUDIO)));
    }

}