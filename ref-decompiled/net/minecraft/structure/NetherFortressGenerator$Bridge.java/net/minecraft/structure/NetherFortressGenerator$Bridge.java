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

public static class NetherFortressGenerator.Bridge
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 10;
    private static final int SIZE_Z = 19;

    public NetherFortressGenerator.Bridge(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.Bridge(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 3, false);
    }

    public static @Nullable NetherFortressGenerator.Bridge create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 19, orientation);
        if (!NetherFortressGenerator.Bridge.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.Bridge(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        for (int i = 0; i <= 4; ++i) {
            for (int j = 0; j <= 2; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, chunkBox);
            }
        }
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        BlockState blockState2 = (BlockState)blockState.with(FenceBlock.EAST, true);
        BlockState blockState3 = (BlockState)blockState.with(FenceBlock.WEST, true);
        this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
        this.fillWithOutline(world, chunkBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
        this.fillWithOutline(world, chunkBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
        this.fillWithOutline(world, chunkBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
    }
}
