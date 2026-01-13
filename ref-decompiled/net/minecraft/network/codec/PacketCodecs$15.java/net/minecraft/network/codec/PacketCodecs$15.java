/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.15
implements PacketCodec<ByteBuf, Integer> {
    PacketCodecs.15() {
    }

    @Override
    public Integer decode(ByteBuf byteBuf) {
        return PacketByteBuf.readSyncId(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, Integer integer) {
        PacketByteBuf.writeSyncId(byteBuf, integer);
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
