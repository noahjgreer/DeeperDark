/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.command.argument;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public static interface RegistryEntryPredicateArgumentType.EntryPredicate<T>
extends Predicate<RegistryEntry<T>> {
    public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry();

    public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> var1);

    public String asString();
}
