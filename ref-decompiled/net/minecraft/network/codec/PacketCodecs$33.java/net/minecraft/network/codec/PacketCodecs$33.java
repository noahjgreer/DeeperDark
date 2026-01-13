/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.33
implements PacketCodec<ByteBuf, Long> {
    PacketCodecs.33() {
    }

    @Override
    public Long decode(ByteBuf byteBuf) {
        return byteBuf.readLong();
    }

    @Override
    public void encode(ByteBuf byteBuf, Long long_) {
        byteBuf.writeLong(long_.longValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Long)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
