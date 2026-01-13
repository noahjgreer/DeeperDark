/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.19
implements PacketCodec<B, Either<L, R>> {
    final /* synthetic */ PacketCodec field_60495;
    final /* synthetic */ PacketCodec field_60496;

    PacketCodecs.19(PacketCodec packetCodec, PacketCodec packetCodec2) {
        this.field_60495 = packetCodec;
        this.field_60496 = packetCodec2;
    }

    @Override
    public Either<L, R> decode(B byteBuf) {
        if (byteBuf.readBoolean()) {
            return Either.left(this.field_60495.decode(byteBuf));
        }
        return Either.right(this.field_60496.decode(byteBuf));
    }

    @Override
    public void encode(B byteBuf, Either<L, R> either) {
        either.ifLeft(value -> {
            byteBuf.writeBoolean(true);
            this.field_60495.encode(byteBuf, value);
        }).ifRight(value -> {
            byteBuf.writeBoolean(false);
            this.field_60496.encode(byteBuf, value);
        });
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((Object)((ByteBuf)object), (Either)((Either)object2));
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
