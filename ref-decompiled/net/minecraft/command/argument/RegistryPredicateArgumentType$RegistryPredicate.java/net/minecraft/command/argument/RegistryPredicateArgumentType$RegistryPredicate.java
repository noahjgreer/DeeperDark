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
import net.minecraft.registry.tag.TagKey;

public static interface RegistryPredicateArgumentType.RegistryPredicate<T>
extends Predicate<RegistryEntry<T>> {
    public Either<RegistryKey<T>, TagKey<T>> getKey();

    public <E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> var1);

    public String asString();
}
