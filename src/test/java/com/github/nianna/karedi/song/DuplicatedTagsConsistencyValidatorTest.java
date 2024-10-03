package com.github.nianna.karedi.song;

import com.github.nianna.karedi.problem.TagProblem;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuplicatedTagsConsistencyValidatorTest {

    Song song = new Song();

    @ParameterizedTest
    @MethodSource("duplicateTagPairs")
    public void shouldGenerateErrorIfDuplicateTagsAreInconsistent(TagKey key, TagKey duplicatedKey) {
        song.addTag(new Tag(key, "foo"));
        song.addTag(new Tag(duplicatedKey, "bar"));

        Optional<TagProblem> firstResult = DuplicatedTagsConsistencyValidator.validate(song, key);
        Optional<TagProblem> secondResult = DuplicatedTagsConsistencyValidator.validate(song, duplicatedKey);

        assertTrue(firstResult.isPresent());
        assertTrue(secondResult.isPresent());

        assertIterableEquals(List.of(key.toString(), duplicatedKey.toString()), firstResult.orElseThrow().getElements());
        assertIterableEquals(List.of(duplicatedKey.toString(), key.toString()), secondResult.orElseThrow().getElements());
    }

    @ParameterizedTest
    @MethodSource("duplicateTagPairs")
    public void shouldNotGenerateErrorIfDuplicateTagsAreConsistent(TagKey key, TagKey duplicatedKey) {
        String commonValue = "foo";
        song.addTag(new Tag(key, commonValue));
        song.addTag(new Tag(duplicatedKey, commonValue));

        Optional<TagProblem> firstResult = DuplicatedTagsConsistencyValidator.validate(song, key);
        Optional<TagProblem> secondResult = DuplicatedTagsConsistencyValidator.validate(song, duplicatedKey);

        assertTrue(firstResult.isEmpty());
        assertTrue(secondResult.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("duplicateTags")
    public void shouldNotGenerateErrorIfDuplicateTagIsUndefined(TagKey key) {
        song.addTag(new Tag(key, "foo"));

        Optional<TagProblem> result = DuplicatedTagsConsistencyValidator.validate(song, key);

        assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> duplicateTagPairs() {
        return Stream.of(
                Arguments.of(TagKey.P1, TagKey.DUETSINGERP1),
                Arguments.of(TagKey.P2, TagKey.DUETSINGERP2),
                Arguments.of(TagKey.MP3, TagKey.AUDIO)
        );
    }

    private static Stream<Arguments> duplicateTags() {
        return Stream.of(
                Arguments.of(TagKey.P1),
                Arguments.of(TagKey.DUETSINGERP1),
                Arguments.of(TagKey.P2),
                Arguments.of(TagKey.DUETSINGERP2),
                Arguments.of(TagKey.MP3),
                Arguments.of(TagKey.AUDIO)
        );
    }

}