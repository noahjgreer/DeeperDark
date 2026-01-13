/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.entry;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public static abstract class RegistryEntryList.ListBacked<T>
implements RegistryEntryList<T> {
    protected abstract List<RegistryEntry<T>> getEntries();

    @Override
    public int size() {
        return this.getEntries().size();
    }

    @Override
    public Spliterator<RegistryEntry<T>> spliterator() {
        return this.getEntries().spliterator();
    }

    @Override
    public Iterator<RegistryEntry<T>> iterator() {
        return this.getEntries().iterator();
    }

    @Override
    public Stream<RegistryEntry<T>> stream() {
        return this.getEntries().stream();
    }

    @Override
    public Optional<RegistryEntry<T>> getRandom(Random random) {
        return Util.getRandomOrEmpty(this.getEntries(), random);
    }

    @Override
    public RegistryEntry<T> get(int index) {
        return this.getEntries().get(index);
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        return true;
    }
}
