/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.command.argument;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

record RegistryEntryPredicateArgumentType.TagBased<T>(RegistryEntryList.Named<T> tag) implements RegistryEntryPredicateArgumentType.EntryPredicate<T>
{
    @Override
    public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry() {
        return Either.right(this.tag);
    }

    @Override
    public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.tag.getTag().isOf(registryRef) ? Optional.of(this) : Optional.empty();
    }

    @Override
    public boolean test(RegistryEntry<T> registryEntry) {
        return this.tag.contains(registryEntry);
    }

    @Override
    public String asString() {
        return "#" + String.valueOf(this.tag.getTag().id());
    }

    @Override
    public /* synthetic */ boolean test(Object entry) {
        return this.test((RegistryEntry)entry);
    }
}
