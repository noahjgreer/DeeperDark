/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.2
implements PacketCodec<ByteBuf, Double> {
    PacketCodecs.2() {
    }

    @Override
    public Double decode(ByteBuf byteBuf) {
        return byteBuf.readDouble();
    }

    @Override
    public void encode(ByteBuf byteBuf, Double double_) {
        byteBuf.writeDouble(double_.doubleValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Double)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
