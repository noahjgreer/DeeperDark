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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public static class EntityArgumentType.Serializer
implements ArgumentSerializer<EntityArgumentType, Properties> {
    private static final byte SINGLE_FLAG = 1;
    private static final byte PLAYERS_ONLY_FLAG = 2;

    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        int i = 0;
        if (properties.single) {
            i |= 1;
        }
        if (properties.playersOnly) {
            i |= 2;
        }
        packetByteBuf.writeByte(i);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        return new Properties((b & 1) != 0, (b & 2) != 0);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("amount", properties.single ? "single" : "multiple");
        jsonObject.addProperty("type", properties.playersOnly ? "players" : "entities");
    }

    @Override
    public Properties getArgumentTypeProperties(EntityArgumentType entityArgumentType) {
        return new Properties(entityArgumentType.singleTarget, entityArgumentType.playersOnly);
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<EntityArgumentType> {
        final boolean single;
        final boolean playersOnly;

        Properties(boolean single, boolean playersOnly) {
            this.single = single;
            this.playersOnly = playersOnly;
        }

        @Override
        public EntityArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new EntityArgumentType(this.single, this.playersOnly);
        }

        @Override
        public ArgumentSerializer<EntityArgumentType, ?> getSerializer() {
            return Serializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}
