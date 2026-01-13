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

public static class OceanMonumentGenerator.DoubleYRoom
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.DoubleYRoom(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, orientation, setting, 1, 2, 1);
    }

    public OceanMonumentGenerator.DoubleYRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getIndex()]);
        }
        OceanMonumentGenerator.PieceSetting pieceSetting = this.setting.neighbors[Direction.UP.getIndex()];
        if (pieceSetting.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 8, 1, 6, 8, 6, PRISMARINE);
        }
        this.fillWithOutline(world, chunkBox, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        this.fillWithOutline(world, chunkBox, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        OceanMonumentGenerator.PieceSetting pieceSetting2 = this.setting;
        for (int i = 1; i <= 5; i += 4) {
            int j = 0;
            if (pieceSetting2.neighborPresences[Direction.SOUTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
            }
            j = 7;
            if (pieceSetting2.neighborPresences[Direction.NORTH.getIndex()]) {
                this.fillWithOutline(world, chunkBox, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
            }
            int k = 0;
            if (pieceSetting2.neighborPresences[Direction.WEST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
            }
            k = 7;
            if (pieceSetting2.neighborPresences[Direction.EAST.getIndex()]) {
                this.fillWithOutline(world, chunkBox, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            } else {
                this.fillWithOutline(world, chunkBox, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
            }
            pieceSetting2 = pieceSetting;
        }
    }
}
