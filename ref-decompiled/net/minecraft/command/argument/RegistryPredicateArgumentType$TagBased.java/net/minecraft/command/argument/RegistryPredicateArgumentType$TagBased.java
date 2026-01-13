/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 */
package net.minecraft.command.argument;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

record RegistryPredicateArgumentType.TagBased<T>(TagKey<T> key) implements RegistryPredicateArgumentType.RegistryPredicate<T>
{
    @Override
    public Either<RegistryKey<T>, TagKey<T>> getKey() {
        return Either.right(this.key);
    }

    @Override
    public <E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.key.tryCast(registryRef).map(RegistryPredicateArgumentType.TagBased::new);
    }

    @Override
    public boolean test(RegistryEntry<T> registryEntry) {
        return registryEntry.isIn(this.key);
    }

    @Override
    public String asString() {
        return "#" + String.valueOf(this.key.id());
    }

    @Override
    public /* synthetic */ boolean test(Object entry) {
        return this.test((RegistryEntry)entry);
    }
}
