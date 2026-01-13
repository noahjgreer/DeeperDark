/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.12
implements PacketCodec<ByteBuf, Byte> {
    PacketCodecs.12() {
    }

    @Override
    public Byte decode(ByteBuf byteBuf) {
        return byteBuf.readByte();
    }

    @Override
    public void encode(ByteBuf byteBuf, Byte byte_) {
        byteBuf.writeByte((int)byte_.byteValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Byte)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
