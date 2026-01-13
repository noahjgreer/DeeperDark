/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryPredicateArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public final class RegistryPredicateArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<RegistryPredicateArgumentType<T>> {
    final RegistryKey<? extends Registry<T>> registryRef;

    RegistryPredicateArgumentType.Serializer.Properties(RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
    }

    @Override
    public RegistryPredicateArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
        return new RegistryPredicateArgumentType(this.registryRef);
    }

    @Override
    public ArgumentSerializer<RegistryPredicateArgumentType<T>, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
