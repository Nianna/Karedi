package com.github.nianna.karedi.context;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.syllabizer.Syllabizer;
import com.github.nianna.karedi.syllabizer.SyllabizerFactory;
import com.github.nianna.karedi.util.Language;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SyllabizerContext {

    private static final Map<Language, Syllabizer> SYLLABIZERS = new HashMap<>();

    private final ReadOnlyObjectProperty<Song> activeSongProperty;

    SyllabizerContext(ActiveSongContext activeSongContext) {
        this.activeSongProperty = activeSongContext.activeSongProperty();
        SyllabizerFactory.supportedLanguages()
                .forEach(language -> SYLLABIZERS.put(language, SyllabizerFactory.createFor(language)));
    }

    public Optional<Syllabizer> findSyllabizer() {
        return Optional.of(activeSongProperty.get())
                .flatMap(song -> song.getTagValue(TagKey.LANGUAGE))
                .flatMap(Language::parse)
                .map(SYLLABIZERS::get);
    }
}
