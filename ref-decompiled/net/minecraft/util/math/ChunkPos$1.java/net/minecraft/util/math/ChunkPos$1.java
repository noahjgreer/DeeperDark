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
import net.minecraft.util.math.ChunkPos;

class ChunkPos.1
implements PacketCodec<ByteBuf, ChunkPos> {
    ChunkPos.1() {
    }

    @Override
    public ChunkPos decode(ByteBuf byteBuf) {
        return PacketByteBuf.readChunkPos(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, ChunkPos chunkPos) {
        PacketByteBuf.writeChunkPos(byteBuf, chunkPos);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (ChunkPos)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
