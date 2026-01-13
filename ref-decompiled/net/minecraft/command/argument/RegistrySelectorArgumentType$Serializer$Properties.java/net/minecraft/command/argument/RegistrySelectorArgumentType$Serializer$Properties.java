/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistrySelectorArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public final class RegistrySelectorArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<RegistrySelectorArgumentType<T>> {
    final RegistryKey<? extends Registry<T>> registryRef;

    RegistrySelectorArgumentType.Serializer.Properties(RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
    }

    @Override
    public RegistrySelectorArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
        return new RegistrySelectorArgumentType(commandRegistryAccess, this.registryRef);
    }

    @Override
    public ArgumentSerializer<RegistrySelectorArgumentType<T>, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
