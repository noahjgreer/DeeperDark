/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public final class RegistryKeyArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<RegistryKeyArgumentType<T>> {
    final RegistryKey<? extends Registry<T>> registryRef;

    RegistryKeyArgumentType.Serializer.Properties(RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
    }

    @Override
    public RegistryKeyArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
        return new RegistryKeyArgumentType(this.registryRef);
    }

    @Override
    public ArgumentSerializer<RegistryKeyArgumentType<T>, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
