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

class PacketCodecs.4
implements PacketCodec<ByteBuf, byte[]> {
    PacketCodecs.4() {
    }

    @Override
    public byte[] decode(ByteBuf buf) {
        return PacketByteBuf.readByteArray(buf);
    }

    @Override
    public void encode(ByteBuf buf, byte[] value) {
        PacketByteBuf.writeByteArray(buf, value);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (byte[])object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
