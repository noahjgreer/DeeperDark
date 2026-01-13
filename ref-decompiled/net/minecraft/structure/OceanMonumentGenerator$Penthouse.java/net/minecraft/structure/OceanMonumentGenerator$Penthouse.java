/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

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

public static class OceanMonumentGenerator.Penthouse
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.Penthouse(Direction orientation, BlockBox box) {
        super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, orientation, 1, box);
    }

    public OceanMonumentGenerator.Penthouse(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int i;
        this.fillWithOutline(world, chunkBox, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        for (i = 2; i <= 11; i += 3) {
            this.addBlock(world, SEA_LANTERN, 0, 0, i, chunkBox);
            this.addBlock(world, SEA_LANTERN, 13, 0, i, chunkBox);
            this.addBlock(world, SEA_LANTERN, i, 0, 0, chunkBox);
        }
        this.fillWithOutline(world, chunkBox, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.addBlock(world, PRISMARINE_BRICKS, 5, 0, 8, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 8, 0, 8, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 10, 0, 10, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 3, 0, 10, chunkBox);
        this.fillWithOutline(world, chunkBox, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
        i = 3;
        for (int j = 0; j < 2; ++j) {
            for (int k = 2; k <= 8; k += 3) {
                this.fillWithOutline(world, chunkBox, i, 0, k, i, 2, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            i = 10;
        }
        this.fillWithOutline(world, chunkBox, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
        this.setAirAndWater(world, chunkBox, 6, -1, 3, 7, -1, 4);
        this.spawnElderGuardian(world, chunkBox, 6, 1, 6);
    }
}
