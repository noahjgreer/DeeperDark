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

public static class NetherFortressGenerator.BridgeSmallCrossing
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 7;
    private static final int SIZE_Y = 9;
    private static final int SIZE_Z = 7;

    public NetherFortressGenerator.BridgeSmallCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.BridgeSmallCrossing(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 2, 0, false);
        this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, 2, false);
        this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, 2, false);
    }

    public static @Nullable NetherFortressGenerator.BridgeSmallCrossing create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 9, 7, orientation);
        if (!NetherFortressGenerator.BridgeSmallCrossing.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.BridgeSmallCrossing(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        this.fillWithOutline(world, chunkBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);
        for (int i = 0; i <= 6; ++i) {
            for (int j = 0; j <= 6; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
            }
        }
    }
}
