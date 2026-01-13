/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.16
implements PacketCodec<B, Optional<V>> {
    final /* synthetic */ PacketCodec field_60489;

    PacketCodecs.16(PacketCodec packetCodec) {
        this.field_60489 = packetCodec;
    }

    @Override
    public Optional<V> decode(B byteBuf) {
        if (byteBuf.readBoolean()) {
            return Optional.of(this.field_60489.decode(byteBuf));
        }
        return Optional.empty();
    }

    @Override
    public void encode(B byteBuf, Optional<V> optional) {
        if (optional.isPresent()) {
            byteBuf.writeBoolean(true);
            this.field_60489.encode(byteBuf, optional.get());
        } else {
            byteBuf.writeBoolean(false);
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((Object)((ByteBuf)object), (Optional)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
