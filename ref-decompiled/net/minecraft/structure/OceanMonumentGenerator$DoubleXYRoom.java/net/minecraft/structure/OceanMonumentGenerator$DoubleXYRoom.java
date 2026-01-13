/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
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

public static class OceanMonumentGenerator.DoubleXYRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.DoubleXYRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, 1, orientation, setting, 2, 2, 1);
    }

    public OceanMonumentGenerator.DoubleXYRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_Y_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
        OceanMonumentGenerator.PieceSetting pieceSetting3 = pieceSetting2.neighbors[Direction.UP.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting4 = pieceSetting.neighbors[Direction.UP.getIndex()];
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getIndex()]);
            this.generateVerticalConnection(world, chunkBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (pieceSetting3.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 8, 1, 7, 8, 6, PRISMARINE);
        }
        if (pieceSetting4.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 8, 8, 1, 14, 8, 6, PRISMARINE);
        }
        for (int i = 1; i <= 7; ++i) {
            BlockState blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
                blockState = PRISMARINE;
            }
            this.fillWithOutline(world, chunkBox, 0, i, 0, 0, i, 7, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 15, i, 0, 15, i, 7, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 1, i, 0, 15, i, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 1, i, 7, 14, i, 7, blockState, blockState, false);
        }
        this.fillWithOutline(world, chunkBox, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 2, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 2, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 6, 6, 5, chunkBox);
        this.addBlock(world, PRISMARINE_BRICKS, 9, 6, 5, chunkBox);
        this.fillWithOutline(world, chunkBox, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.addBlock(world, SEA_LANTERN, 5, 4, 2, chunkBox);
        this.addBlock(world, SEA_LANTERN, 5, 4, 5, chunkBox);
        this.addBlock(world, SEA_LANTERN, 10, 4, 2, chunkBox);
        this.addBlock(world, SEA_LANTERN, 10, 4, 5, chunkBox);
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
        if (pieceSetting3.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 5, 0, 4, 6, 0);
        }
        if (pieceSetting3.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 5, 7, 4, 6, 7);
        }
        if (pieceSetting3.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 5, 3, 0, 6, 4);
        }
        if (pieceSetting4.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 11, 5, 0, 12, 6, 0);
        }
        if (pieceSetting4.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 11, 5, 7, 12, 6, 7);
        }
        if (pieceSetting4.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 15, 5, 3, 15, 6, 4);
        }
    }
}
