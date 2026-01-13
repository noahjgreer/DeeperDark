/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.MineshaftStructure;
import org.jspecify.annotations.Nullable;

public static class MineshaftGenerator.MineshaftStairs
extends MineshaftGenerator.MineshaftPart {
    public MineshaftGenerator.MineshaftStairs(int chainLength, BlockBox boundingBox, Direction orientation, MineshaftStructure.Type type) {
        super(StructurePieceType.MINESHAFT_STAIRS, chainLength, type, boundingBox);
        this.setOrientation(orientation);
    }

    public MineshaftGenerator.MineshaftStairs(NbtCompound nbt) {
        super(StructurePieceType.MINESHAFT_STAIRS, nbt);
    }

    public static @Nullable BlockBox getBoundingBox(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation) {
        BlockBox blockBox = switch (orientation) {
            default -> new BlockBox(0, -5, -8, 2, 2, 0);
            case Direction.SOUTH -> new BlockBox(0, -5, 0, 2, 2, 8);
            case Direction.WEST -> new BlockBox(-8, -5, 0, 0, 2, 2);
            case Direction.EAST -> new BlockBox(0, -5, 0, 8, 2, 2);
        };
        blockBox.move(x, y, z);
        if (holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return blockBox;
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        int i = this.getChainLength();
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                default: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                    break;
                }
                case SOUTH: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                    break;
                }
                case WEST: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.WEST, i);
                    break;
                }
                case EAST: {
                    MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), Direction.EAST, i);
                }
            }
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.cannotGenerate(world, chunkBox)) {
            return;
        }
        this.fillWithOutline(world, chunkBox, 0, 5, 0, 2, 7, 1, AIR, AIR, false);
        this.fillWithOutline(world, chunkBox, 0, 0, 7, 2, 2, 8, AIR, AIR, false);
        for (int i = 0; i < 5; ++i) {
            this.fillWithOutline(world, chunkBox, 0, 5 - i - (i < 4 ? 1 : 0), 2 + i, 2, 7 - i, 2 + i, AIR, AIR, false);
        }
    }
}
