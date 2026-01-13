/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
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

public static class StrongholdGenerator.PrisonHall
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 9;
    protected static final int SIZE_Y = 5;
    protected static final int SIZE_Z = 11;

    public StrongholdGenerator.PrisonHall(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_PRISON_HALL, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
    }

    public StrongholdGenerator.PrisonHall(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_PRISON_HALL, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
    }

    public static @Nullable StrongholdGenerator.PrisonHall create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 9, 5, 11, orientation);
        if (!StrongholdGenerator.PrisonHall.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.PrisonHall(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 8, 4, 10, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
        this.fillWithOutline(world, chunkBox, 1, 1, 10, 3, 3, 10, AIR, AIR, false);
        this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 3, 1, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 1, 3, 4, 3, 3, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 1, 7, 4, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 1, 9, 4, 3, 9, false, random, STONE_BRICK_RANDOMIZER);
        for (int i = 1; i <= 3; ++i) {
            this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 4, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true)).with(PaneBlock.EAST, true), 4, i, 5, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, i, 6, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 5, i, 5, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 6, i, 5, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true), 7, i, 5, chunkBox);
        }
        this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 2, chunkBox);
        this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true), 4, 3, 8, chunkBox);
        BlockState blockState = (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.FACING, Direction.WEST)).with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
        this.addBlock(world, blockState, 4, 1, 2, chunkBox);
        this.addBlock(world, blockState2, 4, 2, 2, chunkBox);
        this.addBlock(world, blockState, 4, 1, 8, chunkBox);
        this.addBlock(world, blockState2, 4, 2, 8, chunkBox);
    }
}
