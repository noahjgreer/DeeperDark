/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util.math;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.ImmutableBlockBox;

class ImmutableBlockBox.1
implements PacketCodec<ByteBuf, ImmutableBlockBox> {
    ImmutableBlockBox.1() {
    }

    @Override
    public ImmutableBlockBox decode(ByteBuf byteBuf) {
        return new ImmutableBlockBox(PacketByteBuf.readBlockPos(byteBuf), PacketByteBuf.readBlockPos(byteBuf));
    }

    @Override
    public void encode(ByteBuf byteBuf, ImmutableBlockBox immutableBlockBox) {
        PacketByteBuf.writeBlockPos(byteBuf, immutableBlockBox.min());
        PacketByteBuf.writeBlockPos(byteBuf, immutableBlockBox.max());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (ImmutableBlockBox)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
