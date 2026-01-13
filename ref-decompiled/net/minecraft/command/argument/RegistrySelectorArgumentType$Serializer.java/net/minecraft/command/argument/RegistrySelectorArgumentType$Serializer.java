/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistrySelectorArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public static class RegistrySelectorArgumentType.Serializer<T>
implements ArgumentSerializer<RegistrySelectorArgumentType<T>, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeRegistryKey(properties.registryRef);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        return new Properties(packetByteBuf.readRegistryRefKey());
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
    }

    @Override
    public Properties getArgumentTypeProperties(RegistrySelectorArgumentType<T> registrySelectorArgumentType) {
        return new Properties(registrySelectorArgumentType.registryRef);
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<RegistrySelectorArgumentType<T>> {
        final RegistryKey<? extends Registry<T>> registryRef;

        Properties(RegistryKey<? extends Registry<T>> registryRef) {
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
}
