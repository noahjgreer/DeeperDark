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

public static class OceanMonumentGenerator.SimpleRoomTop
extends OceanMonumentGenerator.Piece {
    public OceanMonumentGenerator.SimpleRoomTop(Direction orientation, OceanMonumentGenerator.PieceSetting setting) {
        super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, orientation, setting, 1, 1, 1);
    }

    public OceanMonumentGenerator.SimpleRoomTop(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.setting.roomIndex / 25 > 0) {
            this.generateVerticalConnection(world, chunkBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getIndex()]);
        }
        if (this.setting.neighbors[Direction.UP.getIndex()] == null) {
            this.fillArea(world, chunkBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
        }
        for (int i = 1; i <= 6; ++i) {
            for (int j = 1; j <= 6; ++j) {
                if (random.nextInt(3) == 0) continue;
                int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                BlockState blockState = Blocks.WET_SPONGE.getDefaultState();
                this.fillWithOutline(world, chunkBox, i, k, j, i, 3, j, blockState, blockState, false);
            }
        }
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
    }
}
