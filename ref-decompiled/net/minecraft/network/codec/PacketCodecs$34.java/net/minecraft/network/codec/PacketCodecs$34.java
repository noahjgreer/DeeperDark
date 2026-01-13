/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarLongs;

class PacketCodecs.34
implements PacketCodec<ByteBuf, Long> {
    PacketCodecs.34() {
    }

    @Override
    public Long decode(ByteBuf byteBuf) {
        return VarLongs.read(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, Long long_) {
        VarLongs.write(byteBuf, long_);
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
