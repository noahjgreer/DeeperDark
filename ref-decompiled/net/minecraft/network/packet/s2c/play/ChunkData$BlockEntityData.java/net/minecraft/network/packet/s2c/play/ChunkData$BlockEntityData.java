/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jspecify.annotations.Nullable;

static class ChunkData.BlockEntityData {
    public static final PacketCodec<RegistryByteBuf, ChunkData.BlockEntityData> PACKET_CODEC = PacketCodec.of(ChunkData.BlockEntityData::write, ChunkData.BlockEntityData::new);
    public static final PacketCodec<RegistryByteBuf, List<ChunkData.BlockEntityData>> LIST_PACKET_CODEC = PACKET_CODEC.collect(PacketCodecs.toList());
    final int localXz;
    final int y;
    final BlockEntityType<?> type;
    final @Nullable NbtCompound nbt;

    private ChunkData.BlockEntityData(int localXz, int y, BlockEntityType<?> type, @Nullable NbtCompound nbt) {
        this.localXz = localXz;
        this.y = y;
        this.type = type;
        this.nbt = nbt;
    }

    private ChunkData.BlockEntityData(RegistryByteBuf buf) {
        this.localXz = buf.readByte();
        this.y = buf.readShort();
        this.type = (BlockEntityType)PacketCodecs.registryValue(RegistryKeys.BLOCK_ENTITY_TYPE).decode(buf);
        this.nbt = buf.readNbt();
    }

    private void write(RegistryByteBuf buf) {
        buf.writeByte(this.localXz);
        buf.writeShort(this.y);
        PacketCodecs.registryValue(RegistryKeys.BLOCK_ENTITY_TYPE).encode(buf, this.type);
        buf.writeNbt(this.nbt);
    }

    static ChunkData.BlockEntityData of(BlockEntity blockEntity) {
        NbtCompound nbtCompound = blockEntity.toInitialChunkDataNbt(blockEntity.getWorld().getRegistryManager());
        BlockPos blockPos = blockEntity.getPos();
        int i = ChunkSectionPos.getLocalCoord(blockPos.getX()) << 4 | ChunkSectionPos.getLocalCoord(blockPos.getZ());
        return new ChunkData.BlockEntityData(i, blockPos.getY(), blockEntity.getType(), nbtCompound.isEmpty() ? null : nbtCompound);
    }
}
