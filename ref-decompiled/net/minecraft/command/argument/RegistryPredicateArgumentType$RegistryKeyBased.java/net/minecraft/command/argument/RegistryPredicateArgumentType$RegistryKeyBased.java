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

record RegistryPredicateArgumentType.RegistryKeyBased<T>(RegistryKey<T> key) implements RegistryPredicateArgumentType.RegistryPredicate<T>
{
    @Override
    public Either<RegistryKey<T>, TagKey<T>> getKey() {
        return Either.left(this.key);
    }

    @Override
    public <E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
        return this.key.tryCast(registryRef).map(RegistryPredicateArgumentType.RegistryKeyBased::new);
    }

    @Override
    public boolean test(RegistryEntry<T> registryEntry) {
        return registryEntry.matchesKey(this.key);
    }

    @Override
    public String asString() {
        return this.key.getValue().toString();
    }

    @Override
    public /* synthetic */ boolean test(Object entry) {
        return this.test((RegistryEntry)entry);
    }
}
