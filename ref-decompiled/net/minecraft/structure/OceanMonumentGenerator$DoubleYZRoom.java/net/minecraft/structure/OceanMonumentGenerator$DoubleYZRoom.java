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

public static class OceanMonumentGenerator.DoubleYZRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.DoubleYZRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, 1, orientation, setting, 1, 2, 2);
    }

    public OceanMonumentGenerator.DoubleYZRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockState blockState;
        int i;
        OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
        OceanMonumentGenerator.PieceSetting pieceSetting3 = pieceSetting.neighbors[Direction.UP.getIndex()];
        OceanMonumentGenerator.PieceSetting pieceSetting4 = pieceSetting2.neighbors[Direction.UP.getIndex()];
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getIndex()]);
            this.generateVerticalConnection(world, chunkBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (pieceSetting4.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 8, 1, 6, 8, 7, PRISMARINE);
        }
        if (pieceSetting3.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 8, 8, 6, 8, 14, PRISMARINE);
        }
        for (i = 1; i <= 7; ++i) {
            blockState = PRISMARINE_BRICKS;
            if (i == 2 || i == 6) {
                blockState = PRISMARINE;
            }
            this.fillWithOutline(world, chunkBox, 0, i, 0, 0, i, 15, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 7, i, 0, 7, i, 15, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 1, i, 0, 6, i, 0, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 1, i, 15, 6, i, 15, blockState, blockState, false);
        }
        for (i = 1; i <= 7; ++i) {
            blockState = DARK_PRISMARINE;
            if (i == 2 || i == 6) {
                blockState = SEA_LANTERN;
            }
            this.fillWithOutline(world, chunkBox, 3, i, 7, 4, i, 8, blockState, blockState, false);
        }
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
        if (pieceSetting4.neighborPresences[Direction.SOUTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 5, 0, 4, 6, 0);
        }
        if (pieceSetting4.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 7, 5, 3, 7, 6, 4);
            this.fillWithOutline(world, chunkBox, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        }
        if (pieceSetting4.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 5, 3, 0, 6, 4);
            this.fillWithOutline(world, chunkBox, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        }
        if (pieceSetting3.neighborPresences[Direction.NORTH.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 3, 5, 15, 4, 6, 15);
        }
        if (pieceSetting3.neighborPresences[Direction.WEST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 0, 5, 11, 0, 6, 12);
            this.fillWithOutline(world, chunkBox, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        }
        if (pieceSetting3.neighborPresences[Direction.EAST.getIndex()]) {
            this.setAirAndWater(world, chunkBox, 7, 5, 11, 7, 6, 12);
            this.fillWithOutline(world, chunkBox, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        }
    }
}
