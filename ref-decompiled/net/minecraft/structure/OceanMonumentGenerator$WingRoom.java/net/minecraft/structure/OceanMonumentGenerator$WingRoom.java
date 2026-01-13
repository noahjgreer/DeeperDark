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

public static class OceanMonumentGenerator.WingRoom
extends OceanMonumentGenerator.Piece {
    private int roomType;

    public OceanMonumentGenerator.WingRoom(Direction orientation, BlockBox box, int wing) {
        super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, orientation, 1, box);
        this.roomType = wing & 1;
    }

    public OceanMonumentGenerator.WingRoom(NbtCompound nbt) {
        super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, nbt);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.roomType == 0) {
            int i;
            for (i = 0; i < 4; ++i) {
                this.fillWithOutline(world, chunkBox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            }
            this.fillWithOutline(world, chunkBox, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
            for (i = 18; i >= 7; i -= 3) {
                this.addBlock(world, SEA_LANTERN, 6, 3, i, chunkBox);
                this.addBlock(world, SEA_LANTERN, 16, 3, i, chunkBox);
            }
            this.addBlock(world, SEA_LANTERN, 10, 0, 10, chunkBox);
            this.addBlock(world, SEA_LANTERN, 12, 0, 10, chunkBox);
            this.addBlock(world, SEA_LANTERN, 10, 0, 12, chunkBox);
            this.addBlock(world, SEA_LANTERN, 12, 0, 12, chunkBox);
            this.addBlock(world, SEA_LANTERN, 8, 3, 6, chunkBox);
            this.addBlock(world, SEA_LANTERN, 14, 3, 6, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 4, chunkBox);
            this.addBlock(world, SEA_LANTERN, 4, 1, 4, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 4, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 4, chunkBox);
            this.addBlock(world, SEA_LANTERN, 18, 1, 4, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 4, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 4, 2, 18, chunkBox);
            this.addBlock(world, SEA_LANTERN, 4, 1, 18, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 4, 0, 18, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 18, 2, 18, chunkBox);
            this.addBlock(world, SEA_LANTERN, 18, 1, 18, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 18, 0, 18, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 9, 7, 20, chunkBox);
            this.addBlock(world, PRISMARINE_BRICKS, 13, 7, 20, chunkBox);
            this.fillWithOutline(world, chunkBox, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.spawnElderGuardian(world, chunkBox, 11, 2, 16);
        } else if (this.roomType == 1) {
            int l;
            this.fillWithOutline(world, chunkBox, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, chunkBox, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            int i = 9;
            int j = 20;
            int k = 5;
            for (l = 0; l < 2; ++l) {
                this.addBlock(world, PRISMARINE_BRICKS, i, 6, 20, chunkBox);
                this.addBlock(world, SEA_LANTERN, i, 5, 20, chunkBox);
                this.addBlock(world, PRISMARINE_BRICKS, i, 4, 20, chunkBox);
                i = 13;
            }
            this.fillWithOutline(world, chunkBox, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            i = 10;
            for (l = 0; l < 2; ++l) {
                this.fillWithOutline(world, chunkBox, i, 0, 10, i, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, i, 0, 12, i, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.addBlock(world, SEA_LANTERN, i, 0, 10, chunkBox);
                this.addBlock(world, SEA_LANTERN, i, 0, 12, chunkBox);
                this.addBlock(world, SEA_LANTERN, i, 4, 10, chunkBox);
                this.addBlock(world, SEA_LANTERN, i, 4, 12, chunkBox);
                i = 12;
            }
            i = 8;
            for (l = 0; l < 2; ++l) {
                this.fillWithOutline(world, chunkBox, i, 0, 7, i, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                this.fillWithOutline(world, chunkBox, i, 0, 14, i, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
                i = 14;
            }
            this.fillWithOutline(world, chunkBox, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.fillWithOutline(world, chunkBox, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
            this.spawnElderGuardian(world, chunkBox, 11, 5, 13);
        }
    }
}
