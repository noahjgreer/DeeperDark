/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.23
implements PacketCodec<ByteBuf, Short> {
    PacketCodecs.23() {
    }

    @Override
    public Short decode(ByteBuf byteBuf) {
        return byteBuf.readShort();
    }

    @Override
    public void encode(ByteBuf byteBuf, Short short_) {
        byteBuf.writeShort((int)short_.shortValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Short)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
