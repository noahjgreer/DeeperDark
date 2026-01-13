/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
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

public static class MineshaftGenerator.MineshaftRoom
extends MineshaftGenerator.MineshaftPart {
    private final List<BlockBox> entrances = Lists.newLinkedList();

    public MineshaftGenerator.MineshaftRoom(int chainLength, Random random, int x, int z, MineshaftStructure.Type type) {
        super(StructurePieceType.MINESHAFT_ROOM, chainLength, type, new BlockBox(x, 50, z, x + 7 + random.nextInt(6), 54 + random.nextInt(6), z + 7 + random.nextInt(6)));
        this.mineshaftType = type;
    }

    public MineshaftGenerator.MineshaftRoom(NbtCompound nbt) {
        super(StructurePieceType.MINESHAFT_ROOM, nbt);
        this.entrances.addAll(nbt.get("Entrances", BlockBox.CODEC.listOf()).orElse(List.of()));
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        BlockBox blockBox;
        MineshaftGenerator.MineshaftPart mineshaftPart;
        int k;
        int i = this.getChainLength();
        int j = this.boundingBox.getBlockCountY() - 3 - 1;
        if (j <= 0) {
            j = 1;
        }
        for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
            mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + k, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() - 1, Direction.NORTH, i);
            if (mineshaftPart == null) continue;
            blockBox = mineshaftPart.getBoundingBox();
            this.entrances.add(new BlockBox(blockBox.getMinX(), blockBox.getMinY(), this.boundingBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), this.boundingBox.getMinZ() + 1));
        }
        for (k = 0; k < this.boundingBox.getBlockCountX() && (k += random.nextInt(this.boundingBox.getBlockCountX())) + 3 <= this.boundingBox.getBlockCountX(); k += 4) {
            mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + k, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, i);
            if (mineshaftPart == null) continue;
            blockBox = mineshaftPart.getBoundingBox();
            this.entrances.add(new BlockBox(blockBox.getMinX(), blockBox.getMinY(), this.boundingBox.getMaxZ() - 1, blockBox.getMaxX(), blockBox.getMaxY(), this.boundingBox.getMaxZ()));
        }
        for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
            mineshaftPart = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() + k, Direction.WEST, i);
            if (mineshaftPart == null) continue;
            blockBox = mineshaftPart.getBoundingBox();
            this.entrances.add(new BlockBox(this.boundingBox.getMinX(), blockBox.getMinY(), blockBox.getMinZ(), this.boundingBox.getMinX() + 1, blockBox.getMaxY(), blockBox.getMaxZ()));
        }
        for (k = 0; k < this.boundingBox.getBlockCountZ() && (k += random.nextInt(this.boundingBox.getBlockCountZ())) + 3 <= this.boundingBox.getBlockCountZ(); k += 4) {
            MineshaftGenerator.MineshaftPart structurePiece = MineshaftGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + random.nextInt(j) + 1, this.boundingBox.getMinZ() + k, Direction.EAST, i);
            if (structurePiece == null) continue;
            blockBox = structurePiece.getBoundingBox();
            this.entrances.add(new BlockBox(this.boundingBox.getMaxX() - 1, blockBox.getMinY(), blockBox.getMinZ(), this.boundingBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ()));
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (this.cannotGenerate(world, chunkBox)) {
            return;
        }
        this.fillWithOutline(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY() + 1, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), Math.min(this.boundingBox.getMinY() + 3, this.boundingBox.getMaxY()), this.boundingBox.getMaxZ(), AIR, AIR, false);
        for (BlockBox blockBox : this.entrances) {
            this.fillWithOutline(world, chunkBox, blockBox.getMinX(), blockBox.getMaxY() - 2, blockBox.getMinZ(), blockBox.getMaxX(), blockBox.getMaxY(), blockBox.getMaxZ(), AIR, AIR, false);
        }
        this.fillHalfEllipsoid(world, chunkBox, this.boundingBox.getMinX(), this.boundingBox.getMinY() + 4, this.boundingBox.getMinZ(), this.boundingBox.getMaxX(), this.boundingBox.getMaxY(), this.boundingBox.getMaxZ(), AIR, false);
    }

    @Override
    public void translate(int x, int y, int z) {
        super.translate(x, y, z);
        for (BlockBox blockBox : this.entrances) {
            blockBox.move(x, y, z);
        }
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.put("Entrances", BlockBox.CODEC.listOf(), this.entrances);
    }
}
