/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import org.jspecify.annotations.Nullable;

public static final class RegistryEntryList.Direct<T>
extends RegistryEntryList.ListBacked<T> {
    static final RegistryEntryList.Direct<?> EMPTY = new RegistryEntryList.Direct(List.of());
    private final List<RegistryEntry<T>> entries;
    private @Nullable Set<RegistryEntry<T>> entrySet;

    RegistryEntryList.Direct(List<RegistryEntry<T>> entries) {
        this.entries = entries;
    }

    @Override
    protected List<RegistryEntry<T>> getEntries() {
        return this.entries;
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
        return Either.right(this.entries);
    }

    @Override
    public Optional<TagKey<T>> getTagKey() {
        return Optional.empty();
    }

    @Override
    public boolean contains(RegistryEntry<T> entry) {
        if (this.entrySet == null) {
            this.entrySet = Set.copyOf(this.entries);
        }
        return this.entrySet.contains(entry);
    }

    public String toString() {
        return "DirectSet[" + String.valueOf(this.entries) + "]";
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistryEntryList.Direct)) return false;
        RegistryEntryList.Direct direct = (RegistryEntryList.Direct)o;
        if (!this.entries.equals(direct.entries)) return false;
        return true;
    }

    public int hashCode() {
        return this.entries.hashCode();
    }
}
