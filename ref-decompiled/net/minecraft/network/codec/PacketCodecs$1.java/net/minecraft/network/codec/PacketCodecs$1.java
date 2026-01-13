/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.1
implements PacketCodec<ByteBuf, Boolean> {
    PacketCodecs.1() {
    }

    @Override
    public Boolean decode(ByteBuf byteBuf) {
        return byteBuf.readBoolean();
    }

    @Override
    public void encode(ByteBuf byteBuf, Boolean boolean_) {
        byteBuf.writeBoolean(boolean_.booleanValue());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Boolean)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
