/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureContext;
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

public static class StrongholdGenerator.SpiralStaircase
extends StrongholdGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 11;
    private static final int SIZE_Z = 5;
    private final boolean isStructureStart;

    public StrongholdGenerator.SpiralStaircase(StructurePieceType structurePieceType, int chainLength, int x, int z, Direction orientation) {
        super(structurePieceType, chainLength, StrongholdGenerator.SpiralStaircase.createBox(x, 64, z, orientation, 5, 11, 5));
        this.isStructureStart = true;
        this.setOrientation(orientation);
        this.entryDoor = StrongholdGenerator.Piece.EntranceType.OPENING;
    }

    public StrongholdGenerator.SpiralStaircase(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, chainLength, boundingBox);
        this.isStructureStart = false;
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
    }

    public StrongholdGenerator.SpiralStaircase(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
        this.isStructureStart = nbtCompound.getBoolean("Source", false);
    }

    public StrongholdGenerator.SpiralStaircase(NbtCompound nbt) {
        this(StructurePieceType.STRONGHOLD_SPIRAL_STAIRCASE, nbt);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Source", this.isStructureStart);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        if (this.isStructureStart) {
            activePieceType = StrongholdGenerator.FiveWayCrossing.class;
        }
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
    }

    public static @Nullable StrongholdGenerator.SpiralStaircase create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 5, orientation);
        if (!StrongholdGenerator.SpiralStaircase.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.SpiralStaircase(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 10, 4, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 7, 0);
        this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 4);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 6, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 5, 3, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 4, 3, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 3, 3, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 2, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, chunkBox);
        this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 1, 1, 3, chunkBox);
    }
}
