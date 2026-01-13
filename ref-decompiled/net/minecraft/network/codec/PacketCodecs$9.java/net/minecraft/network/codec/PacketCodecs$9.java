/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.9
implements PacketCodec<B, V> {
    final /* synthetic */ PacketCodec field_60516;
    final /* synthetic */ Codec field_60517;
    final /* synthetic */ DynamicOps field_60518;

    PacketCodecs.9(PacketCodec packetCodec, Codec codec, DynamicOps dynamicOps) {
        this.field_60516 = packetCodec;
        this.field_60517 = codec;
        this.field_60518 = dynamicOps;
    }

    @Override
    public V decode(B byteBuf) {
        Object object = this.field_60516.decode(byteBuf);
        return this.field_60517.parse(this.field_60518, object).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + String.valueOf(object)));
    }

    @Override
    public void encode(B byteBuf, V object) {
        Object object2 = this.field_60517.encodeStart(this.field_60518, object).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(object)));
        this.field_60516.encode(byteBuf, object2);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((B)((ByteBuf)object), (V)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
