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
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public static class TimeArgumentType.Serializer
implements ArgumentSerializer<TimeArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(properties.minimum);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        int i = packetByteBuf.readInt();
        return new Properties(i);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("min", (Number)properties.minimum);
    }

    @Override
    public Properties getArgumentTypeProperties(TimeArgumentType timeArgumentType) {
        return new Properties(timeArgumentType.minimum);
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<TimeArgumentType> {
        final int minimum;

        Properties(int minimum) {
            this.minimum = minimum;
        }

        @Override
        public TimeArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return TimeArgumentType.time(this.minimum);
        }

        @Override
        public ArgumentSerializer<TimeArgumentType, ?> getSerializer() {
            return Serializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}
