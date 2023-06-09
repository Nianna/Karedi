package com.github.nianna.karedi.txt.parser.element;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NoteElementType {

    FREESTYLE('F'),
    GOLDEN('*'),
    NORMAL(':'),
    RAP('R'),
    GOLDEN_RAP('G');

    private final char typeRepresentation;

    NoteElementType(char typeRepresentation) {
        this.typeRepresentation = typeRepresentation;
    }

    public static NoteElementType fromRepresentation(char typeRepresentation) {
        return Stream.of(values())
                .filter(type -> type.typeRepresentation == typeRepresentation)
                .findFirst()
                .orElse(NORMAL);
    }

    public static Set<Character> allTypeRepresentations() {
        return Stream.of(values())
                .map(NoteElementType::getTypeRepresentation)
                .collect(Collectors.toSet());
    }

    public char getTypeRepresentation() {
        return typeRepresentation;
    }

}
