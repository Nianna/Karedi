package com.github.nianna.karedi.context;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.syllabizer.Syllabizer;
import com.github.nianna.karedi.syllabizer.SyllabizerFactory;
import com.github.nianna.karedi.syllabizer.SyllabizerInitializationFailedException;
import com.github.nianna.karedi.util.Language;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class SyllabizerContext {

    private static final Logger LOGGER = Logger.getLogger(SyllabizerContext.class.getName());

    private static final ConcurrentMap<Language, Syllabizer> SYLLABIZERS = new ConcurrentHashMap<>();

    private final ReadOnlyObjectProperty<Song> activeSongProperty;

    SyllabizerContext(ActiveSongContext activeSongContext) {
        this.activeSongProperty = activeSongContext.activeSongProperty();
    }

    public Optional<Syllabizer> findSyllabizer() {
        return Optional.of(activeSongProperty.get())
                .flatMap(song -> song.getTagValue(TagKey.LANGUAGE))
                .map(value -> value.split(",")[0])
                .map(String::strip)
                .flatMap(Language::parse)
                .map(language -> SYLLABIZERS.computeIfAbsent(language, this::createSyllabizer));
    }

    private Syllabizer createSyllabizer(Language language) {
        try {
            return SyllabizerFactory.createFor(language);
        } catch (SyllabizerInitializationFailedException exception) {
            LOGGER.severe(I18N.get("syllabizer.init_failed"));
            return SyllabizerFactory.createNoopSyllabizer();
        }
    }
}
