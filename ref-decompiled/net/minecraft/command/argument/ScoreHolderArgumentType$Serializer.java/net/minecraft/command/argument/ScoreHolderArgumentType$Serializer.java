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
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public static class ScoreHolderArgumentType.Serializer
implements ArgumentSerializer<ScoreHolderArgumentType, Properties> {
    private static final byte MULTIPLE_FLAG = 1;

    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        int i = 0;
        if (properties.multiple) {
            i |= 1;
        }
        packetByteBuf.writeByte(i);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        boolean bl = (b & 1) != 0;
        return new Properties(bl);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("amount", properties.multiple ? "multiple" : "single");
    }

    @Override
    public Properties getArgumentTypeProperties(ScoreHolderArgumentType scoreHolderArgumentType) {
        return new Properties(scoreHolderArgumentType.multiple);
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<ScoreHolderArgumentType> {
        final boolean multiple;

        Properties(boolean multiple) {
            this.multiple = multiple;
        }

        @Override
        public ScoreHolderArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new ScoreHolderArgumentType(this.multiple);
        }

        @Override
        public ArgumentSerializer<ScoreHolderArgumentType, ?> getSerializer() {
            return Serializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}
