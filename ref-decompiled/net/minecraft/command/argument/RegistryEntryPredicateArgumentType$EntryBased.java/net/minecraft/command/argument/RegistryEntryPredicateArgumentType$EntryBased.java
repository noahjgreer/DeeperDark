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

record RegistryEntryPredicateArgumentType.EntryBased<T>(RegistryEntry.Reference<T> value) implements RegistryEntryPredicateArgumentType.EntryPredicate<T>
{
    @Override
    public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry() {
        return Either.left(this.value);
    }

    @Override
    public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.value.registryKey().isOf(registryRef) ? Optional.of(this) : Optional.empty();
    }

    @Override
    public boolean test(RegistryEntry<T> registryEntry) {
        return registryEntry.equals(this.value);
    }

    @Override
    public String asString() {
        return this.value.registryKey().getValue().toString();
    }

    @Override
    public /* synthetic */ boolean test(Object entry) {
        return this.test((RegistryEntry)entry);
    }
}
