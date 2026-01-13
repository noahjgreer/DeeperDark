/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
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

public static class StrongholdGenerator.Stairs
extends StrongholdGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 11;
    private static final int SIZE_Z = 8;

    public StrongholdGenerator.Stairs(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_STAIRS, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
    }

    public StrongholdGenerator.Stairs(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_STAIRS, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
    }

    public static @Nullable StrongholdGenerator.Stairs create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 8, orientation);
        if (!StrongholdGenerator.Stairs.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.Stairs(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 10, 7, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 7, 0);
        this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 7);
        BlockState blockState = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
        for (int i = 0; i < 6; ++i) {
            this.addBlock(world, blockState, 1, 6 - i, 1 + i, chunkBox);
            this.addBlock(world, blockState, 2, 6 - i, 1 + i, chunkBox);
            this.addBlock(world, blockState, 3, 6 - i, 1 + i, chunkBox);
            if (i >= 5) continue;
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, chunkBox);
        }
    }
}
