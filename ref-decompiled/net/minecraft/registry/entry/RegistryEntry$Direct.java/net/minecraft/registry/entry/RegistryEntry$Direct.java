/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record RegistryEntry.Direct<T>(T value) implements RegistryEntry<T>
{
    @Override
    public boolean hasKeyAndValue() {
        return true;
    }

    @Override
    public boolean matchesId(Identifier id) {
        return false;
    }

    @Override
    public boolean matchesKey(RegistryKey<T> key) {
        return false;
    }

    @Override
    public boolean isIn(TagKey<T> tag) {
        return false;
    }

    @Override
    public boolean matches(RegistryEntry<T> entry) {
        return this.value.equals(entry.value());
    }

    @Override
    public boolean matches(Predicate<RegistryKey<T>> predicate) {
        return false;
    }

    @Override
    public Either<RegistryKey<T>, T> getKeyOrValue() {
        return Either.right(this.value);
    }

    @Override
    public Optional<RegistryKey<T>> getKey() {
        return Optional.empty();
    }

    @Override
    public RegistryEntry.Type getType() {
        return RegistryEntry.Type.DIRECT;
    }

    @Override
    public String toString() {
        return "Direct{" + String.valueOf(this.value) + "}";
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        return true;
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return Stream.of(new TagKey[0]);
    }
}
