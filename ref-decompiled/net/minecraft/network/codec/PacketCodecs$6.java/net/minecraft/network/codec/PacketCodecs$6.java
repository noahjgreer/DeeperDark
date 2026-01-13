/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.StringEncoding;

static class PacketCodecs.6
implements PacketCodec<ByteBuf, String> {
    final /* synthetic */ int field_57069;

    PacketCodecs.6(int i) {
        this.field_57069 = i;
    }

    @Override
    public String decode(ByteBuf byteBuf) {
        return StringEncoding.decode(byteBuf, this.field_57069);
    }

    @Override
    public void encode(ByteBuf byteBuf, String string) {
        StringEncoding.encode(byteBuf, string, this.field_57069);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (String)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
