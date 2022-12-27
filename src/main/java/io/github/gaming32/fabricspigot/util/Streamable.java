package io.github.gaming32.fabricspigot.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface Streamable<T> extends Iterable<T> {
    @NotNull
    Stream<T> stream();

    @NotNull
    @Override
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    @Override
    default Spliterator<T> spliterator() {
        return stream().spliterator();
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        stream().forEach(action);
    }
}
