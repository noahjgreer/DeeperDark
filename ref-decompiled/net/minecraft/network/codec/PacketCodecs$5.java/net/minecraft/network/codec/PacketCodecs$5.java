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

class PacketCodecs.5
implements PacketCodec<ByteBuf, long[]> {
    PacketCodecs.5() {
    }

    @Override
    public long[] decode(ByteBuf buf) {
        return PacketByteBuf.readLongArray(buf);
    }

    @Override
    public void encode(ByteBuf buf, long[] values) {
        PacketByteBuf.writeLongArray(buf, values);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (long[])object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
