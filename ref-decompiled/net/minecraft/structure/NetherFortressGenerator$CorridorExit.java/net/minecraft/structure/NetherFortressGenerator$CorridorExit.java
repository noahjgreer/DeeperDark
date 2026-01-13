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
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
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

public static class NetherFortressGenerator.CorridorExit
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 13;
    private static final int SIZE_Y = 14;
    private static final int SIZE_Z = 13;

    public NetherFortressGenerator.CorridorExit(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.CorridorExit(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 5, 3, true);
    }

    public static @Nullable NetherFortressGenerator.CorridorExit create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
        if (!NetherFortressGenerator.CorridorExit.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.CorridorExit(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int j;
        int i;
        this.fillWithOutline(world, chunkBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        for (i = 1; i <= 11; i += 2) {
            this.fillWithOutline(world, chunkBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
            this.fillWithOutline(world, chunkBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, chunkBox);
            this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, chunkBox);
            if (i == 11) continue;
            this.addBlock(world, blockState, i + 1, 13, 0, chunkBox);
            this.addBlock(world, blockState, i + 1, 13, 12, chunkBox);
            this.addBlock(world, blockState2, 0, 13, i + 1, chunkBox);
            this.addBlock(world, blockState2, 12, 13, i + 1, chunkBox);
        }
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 0, 13, 0, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 0, 13, 12, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 12, 13, 12, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 12, 13, 0, chunkBox);
        for (i = 3; i <= 9; i += 2) {
            this.fillWithOutline(world, chunkBox, 1, 7, i, 1, 8, i, (BlockState)blockState2.with(FenceBlock.WEST, true), (BlockState)blockState2.with(FenceBlock.WEST, true), false);
            this.fillWithOutline(world, chunkBox, 11, 7, i, 11, 8, i, (BlockState)blockState2.with(FenceBlock.EAST, true), (BlockState)blockState2.with(FenceBlock.EAST, true), false);
        }
        this.fillWithOutline(world, chunkBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        for (i = 4; i <= 8; ++i) {
            for (j = 0; j <= 2; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 12 - j, chunkBox);
            }
        }
        for (i = 0; i <= 2; ++i) {
            for (j = 4; j <= 8; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - i, -1, j, chunkBox);
            }
        }
        this.fillWithOutline(world, chunkBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, chunkBox);
        this.addBlock(world, Blocks.LAVA.getDefaultState(), 6, 5, 6, chunkBox);
        BlockPos.Mutable blockPos = this.offsetPos(6, 5, 6);
        if (chunkBox.contains(blockPos)) {
            world.scheduleFluidTick(blockPos, Fluids.LAVA, 0);
        }
    }
}
