/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureContext;
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

public static class MineshaftGenerator.MineshaftCrossing
extends MineshaftGenerator.MineshaftPart {
    private final Direction direction;
    private final boolean twoFloors;

    public MineshaftGenerator.MineshaftCrossing(NbtCompound nbt) {
        super(StructurePieceType.MINESHAFT_CROSSING, nbt);
        this.twoFloors = nbt.getBoolean("tf", false);
        this.direction = nbt.get("D", Direction.HORIZONTAL_QUARTER_TURNS_CODEC).orElse(Direction.SOUTH);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("tf", this.twoFloors);
        nbt.put("D", Direction.HORIZONTAL_QUARTER_TURNS_CODEC, this.direction);
    }

    public MineshaftGenerator.MineshaftCrossing(int chainLength, BlockBox boundingBox, @Nullable Direction orientation, MineshaftStructure.Type type) {
        super(StructurePieceType.MINESHAFT_CROSSING, chainLength, type, boundingBox);
        this.direction = orientation;
        this.twoFloors = boundingBox.getBlockCountY() > 3;
    }

    public static @Nullable BlockBox getBoundingBox(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation) {
        int i = random.nextInt(4) == 0 ? 6 : 2;
        BlockBox blockBox = switch (orientation) {
            default -> new BlockBox(-1, 0, -4, 3, i, 0);
            case Direction.SOUTH -> new BlockBox(-1, 0, 0, 3, i, 4);
            case Direction.WEST -> new BlockBox(-4, 0, -1, 0, i, 3);
            case Direction.EAST -> new BlockBox(0, 0, -1, 4, i, 3);
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
        switch (this.direction) {
            default: {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                break;
            }
            case SOUTH: {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
                break;
            }
            case WEST: {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.WEST, i);
                break;
            }
            case EAST: {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, Direction.EAST, i);
            }
        }
        if (this.twoFloors) {
            if (random.nextBoolean()) {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
            }
            if (random.nextBoolean()) {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() + 1, Direction.WEST, i);
            }
            if (random.nextBoolean()) {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMinZ() + 1, Direction.EAST, i);
            }
            if (random.nextBoolean()) {
                MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3 + 1, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
            }
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.cannotGenerate(world, chunkBox)) {
            return;
        }
        BlockState blockState = this.mineshaftType.getPlanks();
        if (this.twoFloors) {
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY() + 3 - 1, this.boundingBox.getMaxZ(), AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMinY() + 3 - 1, this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMaxY() - 2, this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMaxY() - 2, this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY() + 3, this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY() + 3, this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
        } else {
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ(), this.boundingBox.getMaxX() - 1, this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, AIR, false);
            this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ() - 1, AIR, AIR, false);
        }
        this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY());
        this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMinX() + 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY());
        this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMinZ() + 1, this.boundingBox.getMaxY());
        this.generateCrossingPillar(world, chunkBox, this.boundingBox.getMaxX() - 1, this.boundingBox.getMinY(), this.boundingBox.getMaxZ() - 1, this.boundingBox.getMaxY());
        int i = this.boundingBox.getMinY() - 1;
        for (int j = this.boundingBox.getMinX(); j <= this.boundingBox.getMaxX(); ++j) {
            for (int k = this.boundingBox.getMinZ(); k <= this.boundingBox.getMaxZ(); ++k) {
                this.tryPlaceFloor(world, chunkBox, blockState, j, i, k);
            }
        }
    }

    private void generateCrossingPillar(StructureWorldAccess world, BlockBox boundingBox, int x, int minY, int z, int maxY) {
        if (!this.getBlockAt(world, x, maxY + 1, z, boundingBox).isAir()) {
            this.fillWithOutline(world, boundingBox, x, minY, z, x, maxY, z, this.mineshaftType.getPlanks(), AIR, false);
        }
    }
}
