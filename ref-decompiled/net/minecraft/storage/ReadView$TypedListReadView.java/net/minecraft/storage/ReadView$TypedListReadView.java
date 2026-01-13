/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import java.util.stream.Stream;

public static interface ReadView.TypedListReadView<T>
extends Iterable<T> {
    public boolean isEmpty();

    public Stream<T> stream();
}
