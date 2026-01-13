/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.predicate.NumberRange;

static class NumberRange.Bounds.1
implements PacketCodec<B, NumberRange.Bounds<T>> {
    private static final int MIN_PRESENT_FLAG = 1;
    private static final int MAX_PRESENT_FLAG = 2;
    final /* synthetic */ PacketCodec field_56290;

    NumberRange.Bounds.1(PacketCodec packetCodec) {
        this.field_56290 = packetCodec;
    }

    @Override
    public NumberRange.Bounds<T> decode(B byteBuf) {
        byte b = byteBuf.readByte();
        Optional optional = (b & 1) != 0 ? Optional.of((Number)this.field_56290.decode(byteBuf)) : Optional.empty();
        Optional optional2 = (b & 2) != 0 ? Optional.of((Number)this.field_56290.decode(byteBuf)) : Optional.empty();
        return new NumberRange.Bounds(optional, optional2);
    }

    @Override
    public void encode(B byteBuf, NumberRange.Bounds<T> bounds) {
        Optional<Number> optional = bounds.min();
        Optional<Number> optional2 = bounds.max();
        byteBuf.writeByte((optional.isPresent() ? 1 : 0) | (optional2.isPresent() ? 2 : 0));
        optional.ifPresent(min -> this.field_56290.encode(byteBuf, min));
        optional2.ifPresent(max -> this.field_56290.encode(byteBuf, max));
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((Object)((ByteBuf)object), (NumberRange.Bounds)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
