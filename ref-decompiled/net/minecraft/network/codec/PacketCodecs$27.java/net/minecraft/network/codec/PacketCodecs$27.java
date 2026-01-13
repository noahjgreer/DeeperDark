/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.ColorHelper;

class PacketCodecs.27
implements PacketCodec<ByteBuf, Integer> {
    PacketCodecs.27() {
    }

    @Override
    public Integer decode(ByteBuf byteBuf) {
        return ColorHelper.getArgb(byteBuf.readByte() & 0xFF, byteBuf.readByte() & 0xFF, byteBuf.readByte() & 0xFF);
    }

    @Override
    public void encode(ByteBuf byteBuf, Integer integer) {
        byteBuf.writeByte(ColorHelper.getRed(integer));
        byteBuf.writeByte(ColorHelper.getGreen(integer));
        byteBuf.writeByte(ColorHelper.getBlue(integer));
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
