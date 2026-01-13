/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
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

public static class StrongholdGenerator.ChestCorridor
extends StrongholdGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 5;
    private static final int SIZE_Z = 7;
    private boolean chestGenerated;

    public StrongholdGenerator.ChestCorridor(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
    }

    public StrongholdGenerator.ChestCorridor(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, nbt);
        this.chestGenerated = nbt.getBoolean("Chest", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Chest", this.chestGenerated);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 1, 1);
    }

    public static @Nullable StrongholdGenerator.ChestCorridor create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainlength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
        if (!StrongholdGenerator.ChestCorridor.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.ChestCorridor(chainlength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 4, 6, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 1, 1, 0);
        this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.OPENING, 1, 1, 6);
        this.fillWithOutline(world, chunkBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
        this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, chunkBox);
        this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, chunkBox);
        for (int i = 2; i <= 4; ++i) {
            this.addBlock(world, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, chunkBox);
        }
        if (!this.chestGenerated && chunkBox.contains(this.offsetPos(3, 2, 3))) {
            this.chestGenerated = true;
            this.addChest(world, chunkBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
        }
    }
}
