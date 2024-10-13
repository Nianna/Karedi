package com.github.nianna.karedi.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CollectionsUtils {

    private CollectionsUtils() {

    }

    public static <T> Set<T> join(Set<T> first, Set<T> second){
        return Stream.of(first, second)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }
}
