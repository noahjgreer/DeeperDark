/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

class PacketCodecs.32
implements PacketCodec<ByteBuf, Integer> {
    PacketCodecs.32() {
    }

    @Override
    public Integer decode(ByteBuf byteBuf) {
        return VarInts.read(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, Integer integer) {
        VarInts.write(byteBuf, integer);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Integer)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
