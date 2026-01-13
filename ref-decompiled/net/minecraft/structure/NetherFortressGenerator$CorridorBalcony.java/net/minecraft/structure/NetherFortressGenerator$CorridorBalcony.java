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

public static class NetherFortressGenerator.CorridorBalcony
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 9;
    private static final int SIZE_Y = 7;
    private static final int SIZE_Z = 9;

    public NetherFortressGenerator.CorridorBalcony(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public NetherFortressGenerator.CorridorBalcony(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        int i = 1;
        Direction direction = this.getFacing();
        if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 5;
        }
        this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, i, random.nextInt(8) > 0);
        this.fillSEOpening((NetherFortressGenerator.Start)start, holder, random, 0, i, random.nextInt(8) > 0);
    }

    public static @Nullable NetherFortressGenerator.CorridorBalcony create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -3, 0, 0, 9, 7, 9, orientation);
        if (!NetherFortressGenerator.CorridorBalcony.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.CorridorBalcony(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, true)).with(FenceBlock.SOUTH, true), 0, 3, 8, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.SOUTH, true), 8, 3, 8, chunkBox);
        this.fillWithOutline(world, chunkBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);
        for (int i = 0; i <= 5; ++i) {
            for (int j = 0; j <= 8; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, chunkBox);
            }
        }
    }
}
