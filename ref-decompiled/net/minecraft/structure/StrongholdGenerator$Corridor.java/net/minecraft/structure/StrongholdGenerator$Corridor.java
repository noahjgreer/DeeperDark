/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
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

public static class StrongholdGenerator.Corridor
extends StrongholdGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 5;
    private static final int SIZE_Z = 7;
    private final boolean leftExitExists;
    private final boolean rightExitExists;

    public StrongholdGenerator.Corridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_CORRIDOR, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
        this.leftExitExists = random.nextInt(2) == 0;
        this.rightExitExists = random.nextInt(2) == 0;
    }

    public StrongholdGenerator.Corridor(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_CORRIDOR, nbt);
        this.leftExitExists = nbt.getBoolean("Left", false);
        this.rightExitExists = nbt.getBoolean("Right", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Left", this.leftExitExists);
        nbt.putBoolean("Right", this.rightExitExists);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
        if (this.leftExitExists) {
            this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 2);
        }
        if (this.rightExitExists) {
            this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 2);
        }
    }

    public static @Nullable StrongholdGenerator.Corridor create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
        if (!StrongholdGenerator.Corridor.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.Corridor(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
        this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
        BlockState blockState = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST);
        BlockState blockState2 = (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST);
        this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1f, 1, 2, 1, blockState);
        this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1f, 3, 2, 1, blockState2);
        this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1f, 1, 2, 5, blockState);
        this.addBlockWithRandomThreshold(world, chunkBox, random, 0.1f, 3, 2, 5, blockState2);
        if (this.leftExitExists) {
            this.fillWithOutline(world, chunkBox, 0, 1, 2, 0, 3, 4, AIR, AIR, false);
        }
        if (this.rightExitExists) {
            this.fillWithOutline(world, chunkBox, 4, 1, 2, 4, 3, 4, AIR, AIR, false);
        }
    }
}
