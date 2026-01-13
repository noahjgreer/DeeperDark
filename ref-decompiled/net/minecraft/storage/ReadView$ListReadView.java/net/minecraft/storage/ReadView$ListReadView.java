/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import java.util.stream.Stream;
import net.minecraft.storage.ReadView;

public static interface ReadView.ListReadView
extends Iterable<ReadView> {
    public boolean isEmpty();

    public Stream<ReadView> stream();
}
