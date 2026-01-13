/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jspecify.annotations.Nullable;

public static class NetherFortressGenerator.BridgePlatform
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 7;
    private static final int SIZE_Y = 8;
    private static final int SIZE_Z = 9;
    private boolean hasBlazeSpawner;

    public NetherFortressGenerator.BridgePlatform(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.BridgePlatform(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, nbt);
        this.hasBlazeSpawner = nbt.getBoolean("Mob", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Mob", this.hasBlazeSpawner);
    }

    public static @Nullable NetherFortressGenerator.BridgePlatform create(StructurePiecesHolder holder, int x, int y, int z, int chainLength, Direction orientation) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 8, 9, orientation);
        if (!NetherFortressGenerator.BridgePlatform.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.BridgePlatform(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos.Mutable blockPos;
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 1, 6, 3, chunkBox);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 5, 6, 3, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.NORTH, true), 0, 6, 3, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.NORTH, true), 6, 6, 3, chunkBox);
        this.fillWithOutline(world, chunkBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 6, 8, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 6, 6, 8, chunkBox);
        this.fillWithOutline(world, chunkBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 1, 7, 8, chunkBox);
        this.fillWithOutline(world, chunkBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 5, 7, 8, chunkBox);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true), 2, 8, 8, chunkBox);
        this.addBlock(world, blockState, 3, 8, 8, chunkBox);
        this.addBlock(world, (BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true), 4, 8, 8, chunkBox);
        if (!this.hasBlazeSpawner && chunkBox.contains(blockPos = this.offsetPos(3, 5, 5))) {
            this.hasBlazeSpawner = true;
            world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
                mobSpawnerBlockEntity.setEntityType(EntityType.BLAZE, random);
            }
        }
        for (int i = 0; i <= 6; ++i) {
            for (int j = 0; j <= 6; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
            }
        }
    }
}
