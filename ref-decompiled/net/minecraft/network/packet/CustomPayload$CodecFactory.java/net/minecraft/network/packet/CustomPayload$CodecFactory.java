/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public static interface CustomPayload.CodecFactory<B extends PacketByteBuf> {
    public PacketCodec<B, ? extends CustomPayload> create(Identifier var1);
}
