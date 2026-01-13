/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.enums.SlabType;
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

public static class StrongholdGenerator.FiveWayCrossing
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 10;
    protected static final int SIZE_Y = 9;
    protected static final int SIZE_Z = 11;
    private final boolean lowerLeftExists;
    private final boolean upperLeftExists;
    private final boolean lowerRightExists;
    private final boolean upperRightExists;

    public StrongholdGenerator.FiveWayCrossing(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
        this.lowerLeftExists = random.nextBoolean();
        this.upperLeftExists = random.nextBoolean();
        this.lowerRightExists = random.nextBoolean();
        this.upperRightExists = random.nextInt(3) > 0;
    }

    public StrongholdGenerator.FiveWayCrossing(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_FIVE_WAY_CROSSING, nbt);
        this.lowerLeftExists = nbt.getBoolean("leftLow", false);
        this.upperLeftExists = nbt.getBoolean("leftHigh", false);
        this.lowerRightExists = nbt.getBoolean("rightLow", false);
        this.upperRightExists = nbt.getBoolean("rightHigh", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("leftLow", this.lowerLeftExists);
        nbt.putBoolean("leftHigh", this.upperLeftExists);
        nbt.putBoolean("rightLow", this.lowerRightExists);
        nbt.putBoolean("rightHigh", this.upperRightExists);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        int i = 3;
        int j = 5;
        Direction direction = this.getFacing();
        if (direction == Direction.WEST || direction == Direction.NORTH) {
            i = 8 - i;
            j = 8 - j;
        }
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 5, 1);
        if (this.lowerLeftExists) {
            this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, i, 1);
        }
        if (this.upperLeftExists) {
            this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, j, 7);
        }
        if (this.lowerRightExists) {
            this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, i, 1);
        }
        if (this.upperRightExists) {
            this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, j, 7);
        }
    }

    public static @Nullable StrongholdGenerator.FiveWayCrossing create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -3, 0, 10, 9, 11, orientation);
        if (!StrongholdGenerator.FiveWayCrossing.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.FiveWayCrossing(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 9, 8, 10, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 3, 0);
        if (this.lowerLeftExists) {
            this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 5, 3, AIR, AIR, false);
        }
        if (this.lowerRightExists) {
            this.fillWithOutline(world, chunkBox, 9, 3, 1, 9, 5, 3, AIR, AIR, false);
        }
        if (this.upperLeftExists) {
            this.fillWithOutline(world, chunkBox, 0, 5, 7, 0, 7, 9, AIR, AIR, false);
        }
        if (this.upperRightExists) {
            this.fillWithOutline(world, chunkBox, 9, 5, 7, 9, 7, 9, AIR, AIR, false);
        }
        this.fillWithOutline(world, chunkBox, 5, 1, 10, 7, 3, 10, AIR, AIR, false);
        this.fillWithOutline(world, chunkBox, 1, 2, 1, 8, 2, 6, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 1, 5, 4, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 8, 1, 5, 8, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 1, 4, 7, 3, 4, 9, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 1, 3, 5, 3, 3, 6, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 1, 7, 7, 1, 8, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), Blocks.SMOOTH_STONE_SLAB.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE), false);
        this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, chunkBox);
    }
}
