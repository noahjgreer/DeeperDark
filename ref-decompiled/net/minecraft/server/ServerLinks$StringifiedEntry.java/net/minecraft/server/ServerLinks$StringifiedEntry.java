/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;

public record ServerLinks.StringifiedEntry(Either<ServerLinks.Known, Text> type, String link) {
    public static final PacketCodec<ByteBuf, ServerLinks.StringifiedEntry> CODEC = PacketCodec.tuple(TYPE_CODEC, ServerLinks.StringifiedEntry::type, PacketCodecs.STRING, ServerLinks.StringifiedEntry::link, ServerLinks.StringifiedEntry::new);
}
