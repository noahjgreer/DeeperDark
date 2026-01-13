/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

static class PacketCodecs.17
implements PacketCodec<B, C> {
    final /* synthetic */ int field_60491;
    final /* synthetic */ IntFunction field_60494;
    final /* synthetic */ PacketCodec field_61041;

    PacketCodecs.17(int i, IntFunction intFunction, PacketCodec packetCodec) {
        this.field_60491 = i;
        this.field_60494 = intFunction;
        this.field_61041 = packetCodec;
    }

    @Override
    public C decode(B byteBuf) {
        int i = PacketCodecs.readCollectionSize(byteBuf, this.field_60491);
        Collection collection = (Collection)this.field_60494.apply(Math.min(i, 65536));
        for (int j = 0; j < i; ++j) {
            collection.add(this.field_61041.decode(byteBuf));
        }
        return collection;
    }

    @Override
    public void encode(B byteBuf, C collection) {
        PacketCodecs.writeCollectionSize(byteBuf, collection.size(), this.field_60491);
        for (Object object : collection) {
            this.field_61041.encode(byteBuf, object);
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((B)((ByteBuf)object), (C)((Collection)object2));
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
