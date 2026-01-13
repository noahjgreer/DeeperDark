/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

static class PacketCodecs.18
implements PacketCodec<B, M> {
    final /* synthetic */ int field_57049;
    final /* synthetic */ PacketCodec field_61042;
    final /* synthetic */ PacketCodec field_61043;
    final /* synthetic */ IntFunction field_57052;

    PacketCodecs.18(int i, PacketCodec packetCodec, PacketCodec packetCodec2, IntFunction intFunction) {
        this.field_57049 = i;
        this.field_61042 = packetCodec;
        this.field_61043 = packetCodec2;
        this.field_57052 = intFunction;
    }

    @Override
    public void encode(B byteBuf, M map) {
        PacketCodecs.writeCollectionSize(byteBuf, map.size(), this.field_57049);
        map.forEach((object, object2) -> {
            this.field_61042.encode(byteBuf, object);
            this.field_61043.encode(byteBuf, object2);
        });
    }

    @Override
    public M decode(B byteBuf) {
        int i = PacketCodecs.readCollectionSize(byteBuf, this.field_57049);
        Map map = (Map)this.field_57052.apply(Math.min(i, 65536));
        for (int j = 0; j < i; ++j) {
            Object object = this.field_61042.decode(byteBuf);
            Object object2 = this.field_61043.decode(byteBuf);
            map.put(object, object2);
        }
        return map;
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((B)((ByteBuf)object), (M)((Map)object2));
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
