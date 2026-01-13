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

public static class OceanMonumentGenerator.Entry
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.Entry(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, orientation, setting, 1, 1, 1);
    }

    public OceanMonumentGenerator.Entry(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        if (this.setting.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 1, 7, 4, 2, 7);
        }
        if (this.setting.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 1, 3, 1, 2, 4);
        }
        if (this.setting.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 6, 1, 3, 7, 2, 4);
        }
    }
}
