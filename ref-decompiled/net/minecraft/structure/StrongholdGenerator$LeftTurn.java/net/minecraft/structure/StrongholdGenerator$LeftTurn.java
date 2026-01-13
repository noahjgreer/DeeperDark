/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

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

public static class StrongholdGenerator.LeftTurn
extends StrongholdGenerator.Turn {
    public StrongholdGenerator.LeftTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_LEFT_TURN, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
    }

    public StrongholdGenerator.LeftTurn(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_LEFT_TURN, nbt);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        Direction direction = this.getFacing();
        if (direction == Direction.NORTH || direction == Direction.EAST) {
            this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
        } else {
            this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
        }
    }

    public static @Nullable StrongholdGenerator.LeftTurn create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
        if (!StrongholdGenerator.LeftTurn.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.LeftTurn(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 4, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
        Direction direction = this.getFacing();
        if (direction == Direction.NORTH || direction == Direction.EAST) {
            this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 3, 3, AIR, AIR, false);
        } else {
            this.fillWithOutline(world, chunkBox, 4, 1, 1, 4, 3, 3, AIR, AIR, false);
        }
    }
}
