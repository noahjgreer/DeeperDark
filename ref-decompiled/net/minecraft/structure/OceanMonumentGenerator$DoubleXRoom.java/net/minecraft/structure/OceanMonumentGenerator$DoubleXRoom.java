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

public static class OceanMonumentGenerator.DoubleXRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.DoubleXRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, orientation, setting, 2, 1, 1);
    }

    public OceanMonumentGenerator.DoubleXRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getIndex()]);
            this.generateVerticalConnection(world, chunkBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (pieceSetting2.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 4, 1, 7, 4, 6, PRISMARINE);
        }
        if (pieceSetting.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 8, 4, 1, 14, 4, 6, PRISMARINE);
        }
        this.fillWithOutline(world, chunkBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.addBlock(world, SEA_LANTERN, 6, 2, 3, chunkBox);
        this.addBlock(world, SEA_LANTERN, 9, 2, 3, chunkBox);
        if (pieceSetting2.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 1, 0, 4, 2, 0);
        }
        if (pieceSetting2.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 1, 7, 4, 2, 7);
        }
        if (pieceSetting2.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 1, 3, 0, 2, 4);
        }
        if (pieceSetting.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 11, 1, 0, 12, 2, 0);
        }
        if (pieceSetting.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 11, 1, 7, 12, 2, 7);
        }
        if (pieceSetting.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 15, 1, 3, 15, 2, 4);
        }
    }
}
