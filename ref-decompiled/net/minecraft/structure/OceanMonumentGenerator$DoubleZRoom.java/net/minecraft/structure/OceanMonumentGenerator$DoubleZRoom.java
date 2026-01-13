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

public static class OceanMonumentGenerator.DoubleZRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.DoubleZRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, orientation, setting, 1, 1, 2);
    }

    public OceanMonumentGenerator.DoubleZRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getIndex()]);
            this.generateVerticalConnection(world, chunkBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (pieceSetting2.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 4, 1, 6, 4, 7, PRISMARINE);
        }
        if (pieceSetting.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 4, 8, 6, 4, 14, PRISMARINE);
        }
        this.fillWithOutline(world, chunkBox, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.addBlock(world, SEA_LANTERN, 2, 2, 5, chunkBox);
        this.addBlock(world, SEA_LANTERN, 5, 2, 5, chunkBox);
        this.addBlock(world, SEA_LANTERN, 2, 2, 10, chunkBox);
        this.addBlock(world, SEA_LANTERN, 5, 2, 10, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 5, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 5, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 2, 3, 10, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 5, 3, 10, chunkBox);
        if (pieceSetting2.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 1, 0, 4, 2, 0);
        }
        if (pieceSetting2.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 7, 1, 3, 7, 2, 4);
        }
        if (pieceSetting2.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 1, 3, 0, 2, 4);
        }
        if (pieceSetting.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 1, 15, 4, 2, 15);
        }
        if (pieceSetting.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 1, 11, 0, 2, 12);
        }
        if (pieceSetting.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 7, 1, 11, 7, 2, 12);
        }
    }
}
