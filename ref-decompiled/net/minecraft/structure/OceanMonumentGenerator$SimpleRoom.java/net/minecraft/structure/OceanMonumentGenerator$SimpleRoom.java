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

public static class OceanMonumentGenerator.SimpleRoom
extends OceanMonumentGenerator.Piece {
    private int roomType;

    public OceanMonumentGenerator.SimpleRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting, Random random) {
        super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, orientation, setting, 1, 1, 1);
        this.roomType = random.nextInt(3);
    }

    public OceanMonumentGenerator.SimpleRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        boolean bl;
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (this.setting.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
        }
        boolean bl2 = bl = this.roomType != 0 && random.nextBoolean() && !this.setting.neighborPresences[Direction.DOWN.getIndex()] && !this.setting.neighborPresences[Direction.UP.getIndex()] && this.setting.countNeighbors() > 1;
        if (this.roomType == 0) {
            this.fillWithOutline(world, chunkBox, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
            this.addBlock(world, SEA_LANTERN, 1, 2, 1, chunkBox);
            this.fillWithOutline(world, chunkBox, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
            this.addBlock(world, SEA_LANTERN, 6, 2, 1, chunkBox);
            this.fillWithOutline(world, chunkBox, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
            this.addBlock(world, SEA_LANTERN, 1, 2, 6, chunkBox);
            this.fillWithOutline(world, chunkBox, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
            this.addBlock(world, SEA_LANTERN, 6, 2, 6, chunkBox);
            if (this.setting.neighborPresences[Direction.SOUTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (this.setting.neighborPresences[Direction.NORTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (this.setting.neighborPresences[Direction.WEST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (this.setting.neighborPresences[Direction.EAST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
        } else if (this.roomType == 1) {
            this.fillWithOutline(world, chunkBox, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, SEA_LANTERN, 2, 2, 2, chunkBox);
            this.addBlock(world, SEA_LANTERN, 2, 2, 5, chunkBox);
            this.addBlock(world, SEA_LANTERN, 5, 2, 5, chunkBox);
            this.addBlock(world, SEA_LANTERN, 5, 2, 2, chunkBox);
            this.fillWithOutline(world, chunkBox, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.addBlock(world, PRISMARINE, 1, 2, 0, chunkBox);
            this.addBlock(world, PRISMARINE, 0, 2, 1, chunkBox);
            this.addBlock(world, PRISMARINE, 1, 2, 7, chunkBox);
            this.addBlock(world, PRISMARINE, 0, 2, 6, chunkBox);
            this.addBlock(world, PRISMARINE, 6, 2, 7, chunkBox);
            this.addBlock(world, PRISMARINE, 7, 2, 6, chunkBox);
            this.addBlock(world, PRISMARINE, 6, 2, 0, chunkBox);
            this.addBlock(world, PRISMARINE, 7, 2, 1, chunkBox);
            if (!this.setting.neighborPresences[Direction.SOUTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (!this.setting.neighborPresences[Direction.NORTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (!this.setting.neighborPresences[Direction.WEST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            if (!this.setting.neighborPresences[Direction.EAST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
                this.fillWithOutline(world, chunkBox, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
        } else if (this.roomType == 2) {
            this.fillWithOutline(world, chunkBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            if (this.setting.neighborPresences[Direction.SOUTH.getIndex()]) {
                this.setAirAndWater(world, chunkBox, 3, 1, 0, 4, 2, 0);
            }
            if (this.setting.neighborPresences[Direction.NORTH.getIndex()]) {
                this.setAirAndWater(world, chunkBox, 3, 1, 7, 4, 2, 7);
            }
            if (this.setting.neighborPresences[Direction.WEST.getIndex()]) {
                this.setAirAndWater(world, chunkBox, 0, 1, 3, 0, 2, 4);
            }
            if (this.setting.neighborPresences[Direction.EAST.getIndex()]) {
                this.setAirAndWater(world, chunkBox, 7, 1, 3, 7, 2, 4);
            }
        }
        if (bl) {
            this.fillWithOutline(world, chunkBox, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        }
    }
}
