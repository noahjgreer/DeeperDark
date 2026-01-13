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
import net.minecraft.block.StairsBlock;
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

public static class NetherFortressGenerator.CorridorStairs
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 14;
    private static final int SIZE_Z = 10;

    public NetherFortressGenerator.CorridorStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.CorridorStairs(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 0, true);
    }

    public static @Nullable NetherFortressGenerator.CorridorStairs create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 14, 10, orientation);
        if (!NetherFortressGenerator.CorridorStairs.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.CorridorStairs(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState blockState = (BlockState)Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        for (int i = 0; i <= 9; ++i) {
            int j = Math.max(1, 7 - i);
            int k = Math.min(Math.max(j + 5, 14 - i), 13);
            int l = i;
            this.fillWithOutline(world, chunkBox, 0, 0, l, 4, j, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 1, j + 1, l, 3, k - 1, l, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
            if (i <= 6) {
                this.addBlock(world, blockState, 1, j + 1, l, chunkBox);
                this.addBlock(world, blockState, 2, j + 1, l, chunkBox);
                this.addBlock(world, blockState, 3, j + 1, l, chunkBox);
            }
            this.fillWithOutline(world, chunkBox, 0, k, l, 4, k, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 0, j + 1, l, 0, k - 1, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 4, j + 1, l, 4, k - 1, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            if ((i & 1) == 0) {
                this.fillWithOutline(world, chunkBox, 0, j + 2, l, 0, j + 3, l, blockState2, blockState2, false);
                this.fillWithOutline(world, chunkBox, 4, j + 2, l, 4, j + 3, l, blockState2, blockState2, false);
            }
            for (int m = 0; m <= 4; ++m) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, chunkBox);
            }
        }
    }
}
