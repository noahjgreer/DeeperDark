/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

static class PacketCodecs.21
implements PacketCodec<ByteBuf, T> {
    final /* synthetic */ IntFunction field_60500;
    final /* synthetic */ ToIntFunction field_60501;

    PacketCodecs.21(IntFunction intFunction, ToIntFunction toIntFunction) {
        this.field_60500 = intFunction;
        this.field_60501 = toIntFunction;
    }

    @Override
    public T decode(ByteBuf byteBuf) {
        int i = VarInts.read(byteBuf);
        return this.field_60500.apply(i);
    }

    @Override
    public void encode(ByteBuf byteBuf, T object) {
        int i = this.field_60501.applyAsInt(object);
        VarInts.write(byteBuf, i);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
