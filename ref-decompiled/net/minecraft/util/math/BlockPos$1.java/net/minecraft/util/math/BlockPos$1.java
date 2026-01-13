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
import net.minecraft.util.math.BlockPos;

class BlockPos.1
implements PacketCodec<ByteBuf, BlockPos> {
    BlockPos.1() {
    }

    @Override
    public BlockPos decode(ByteBuf byteBuf) {
        return PacketByteBuf.readBlockPos(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, BlockPos blockPos) {
        PacketByteBuf.writeBlockPos(byteBuf, blockPos);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (BlockPos)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
