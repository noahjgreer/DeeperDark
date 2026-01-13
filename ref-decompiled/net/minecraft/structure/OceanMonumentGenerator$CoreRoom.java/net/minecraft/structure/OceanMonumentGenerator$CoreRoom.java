/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public static class OceanMonumentGenerator.CoreRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.CoreRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, orientation, setting, 2, 2, 2);
    }

    public OceanMonumentGenerator.CoreRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillArea(world, chunkBox, 1, 8, 0, 14, 8, 14, PRISMARINE);
        int i = 7;
        BlockState blockState = PRISMARINE_BRICKS;
        this.fillWithOutline(world, chunkBox, 0, 7, 0, 0, 7, 15, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 15, 7, 0, 15, 7, 15, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 1, 7, 0, 15, 7, 0, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 1, 7, 15, 14, 7, 15, blockState, blockState, false);
        for (i = 1; i <= 6; ++i) {
            blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
                blockState = PRISMARINE;
            }
            for (int j = 0; j <= 15; j += 15) {
                this.fillWithOutline(world, chunkBox, j, i, 0, j, i, 1, blockState, blockState, false);
                this.fillWithOutline(world, chunkBox, j, i, 6, j, i, 9, blockState, blockState, false);
                this.fillWithOutline(world, chunkBox, j, i, 14, j, i, 15, blockState, blockState, false);
            }
            this.fillWithOutline(world, chunkBox, 1, i, 0, 1, i, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 6, i, 0, 9, i, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 14, i, 0, 14, i, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 1, i, 15, 14, i, 15, blockState, blockState, false);
        }
        this.fillWithOutline(world, chunkBox, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);
        for (i = 3; i <= 6; i += 3) {
            for (int k = 6; k <= 9; k += 3) {
                this.addBlock(world, SEA_LANTERN, k, i, 6, chunkBox);
                this.addBlock(world, SEA_LANTERN, k, i, 9, chunkBox);
            }
        }
        this.fillWithOutline(world, chunkBox, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
    }
}
