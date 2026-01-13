/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.3
implements PacketCodec<ByteBuf, byte[]> {
    final /* synthetic */ int field_54514;

    PacketCodecs.3(int i) {
        this.field_54514 = i;
    }

    @Override
    public byte[] decode(ByteBuf buf) {
        return PacketByteBuf.readByteArray(buf, this.field_54514);
    }

    @Override
    public void encode(ByteBuf buf, byte[] value) {
        if (value.length > this.field_54514) {
            throw new EncoderException("ByteArray with size " + value.length + " is bigger than allowed " + this.field_54514);
        }
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
