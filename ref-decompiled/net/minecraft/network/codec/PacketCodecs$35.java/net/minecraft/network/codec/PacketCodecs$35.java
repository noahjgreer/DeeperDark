/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.35
implements PacketCodec<ByteBuf, Float> {
    PacketCodecs.35() {
    }

    @Override
    public Float decode(ByteBuf byteBuf) {
        return Float.valueOf(byteBuf.readFloat());
    }

    @Override
    public void encode(ByteBuf byteBuf, Float float_) {
        byteBuf.writeFloat(float_.floatValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Float)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
