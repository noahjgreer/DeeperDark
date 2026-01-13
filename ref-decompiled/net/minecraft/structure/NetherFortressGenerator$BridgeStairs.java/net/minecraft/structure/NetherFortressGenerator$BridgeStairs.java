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

public static class NetherFortressGenerator.BridgeStairs
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 7;
    private static final int SIZE_Y = 11;
    private static final int SIZE_Z = 7;

    public NetherFortressGenerator.BridgeStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.BridgeStairs(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 6, 2, false);
    }

    public static @Nullable NetherFortressGenerator.BridgeStairs create(StructurePiecesHolder holder, int x, int y, int z, int chainlength, Direction orientation) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 11, 7, orientation);
        if (!NetherFortressGenerator.BridgeStairs.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.BridgeStairs(chainlength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        this.fillWithOutline(world, chunkBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
        this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
        this.fillWithOutline(world, chunkBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
        for (int i = 0; i <= 6; ++i) {
            for (int j = 0; j <= 6; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
            }
        }
    }
}
