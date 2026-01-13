/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

class Uuids.1
implements PacketCodec<ByteBuf, UUID> {
    Uuids.1() {
    }

    @Override
    public UUID decode(ByteBuf byteBuf) {
        return PacketByteBuf.readUuid(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, UUID uUID) {
        PacketByteBuf.writeUuid(byteBuf, uUID);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (UUID)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
