/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 */
package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public record ChunkBiomeDataS2CPacket.Serialized(ChunkPos pos, byte[] buffer) {
    public ChunkBiomeDataS2CPacket.Serialized(WorldChunk chunk) {
        this(chunk.getPos(), new byte[ChunkBiomeDataS2CPacket.Serialized.getTotalPacketSize(chunk)]);
        ChunkBiomeDataS2CPacket.Serialized.write(new PacketByteBuf(this.toWritingBuf()), chunk);
    }

    public ChunkBiomeDataS2CPacket.Serialized(PacketByteBuf buf) {
        this(buf.readChunkPos(), buf.readByteArray(0x200000));
    }

    private static int getTotalPacketSize(WorldChunk chunk) {
        int i = 0;
        for (ChunkSection chunkSection : chunk.getSectionArray()) {
            i += chunkSection.getBiomeContainer().getPacketSize();
        }
        return i;
    }

    public PacketByteBuf toReadingBuf() {
        return new PacketByteBuf(Unpooled.wrappedBuffer((byte[])this.buffer));
    }

    private ByteBuf toWritingBuf() {
        ByteBuf byteBuf = Unpooled.wrappedBuffer((byte[])this.buffer);
        byteBuf.writerIndex(0);
        return byteBuf;
    }

    public static void write(PacketByteBuf buf, WorldChunk chunk) {
        for (ChunkSection chunkSection : chunk.getSectionArray()) {
            chunkSection.getBiomeContainer().writePacket(buf);
        }
        if (buf.writerIndex() != buf.capacity()) {
            throw new IllegalStateException("Didn't fill biome buffer: expected " + buf.capacity() + " bytes, got " + buf.writerIndex());
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeChunkPos(this.pos);
        buf.writeByteArray(this.buffer);
    }
}
