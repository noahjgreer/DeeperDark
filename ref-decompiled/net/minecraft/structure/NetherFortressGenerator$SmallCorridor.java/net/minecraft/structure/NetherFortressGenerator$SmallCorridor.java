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

public static class NetherFortressGenerator.SmallCorridor
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 7;
    private static final int SIZE_Z = 5;

    public NetherFortressGenerator.SmallCorridor(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.SmallCorridor(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((NetherFortressGenerator.Start)start, holder, random, 1, 0, true);
    }

    public static @Nullable NetherFortressGenerator.SmallCorridor create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
        if (!NetherFortressGenerator.SmallCorridor.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.SmallCorridor(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        for (int i = 0; i <= 4; ++i) {
            for (int j = 0; j <= 4; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
            }
        }
    }
}
