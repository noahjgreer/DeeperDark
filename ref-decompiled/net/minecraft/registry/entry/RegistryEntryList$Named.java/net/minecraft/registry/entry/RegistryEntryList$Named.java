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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import org.jspecify.annotations.Nullable;

public static class RegistryEntryList.Named<T>
extends RegistryEntryList.ListBacked<T> {
    private final RegistryEntryOwner<T> owner;
    private final TagKey<T> tag;
    private @Nullable List<RegistryEntry<T>> entries;

    RegistryEntryList.Named(RegistryEntryOwner<T> owner, TagKey<T> tag) {
        this.owner = owner;
        this.tag = tag;
    }

    void setEntries(List<RegistryEntry<T>> entries) {
        this.entries = List.copyOf(entries);
    }

    public TagKey<T> getTag() {
        return this.tag;
    }

    @Override
    protected List<RegistryEntry<T>> getEntries() {
        if (this.entries == null) {
            throw new IllegalStateException("Trying to access unbound tag '" + String.valueOf(this.tag) + "' from registry " + String.valueOf(this.owner));
        }
        return this.entries;
    }

    @Override
    public boolean isBound() {
        return this.entries != null;
    }

    @Override
    public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
        return Either.left(this.tag);
    }

    @Override
    public Optional<TagKey<T>> getTagKey() {
        return Optional.of(this.tag);
    }

    @Override
    public boolean contains(RegistryEntry<T> entry) {
        return entry.isIn(this.tag);
    }

    public String toString() {
        return "NamedSet(" + String.valueOf(this.tag) + ")[" + String.valueOf(this.entries) + "]";
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        return this.owner.ownerEquals(owner);
    }
}
