/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
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
import org.jspecify.annotations.Nullable;

public static class StrongholdGenerator.SmallCorridor
extends StrongholdGenerator.Piece {
    private final int length;

    public StrongholdGenerator.SmallCorridor(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.length = orientation == Direction.NORTH || orientation == Direction.SOUTH ? boundingBox.getBlockCountZ() : boundingBox.getBlockCountX();
    }

    public StrongholdGenerator.SmallCorridor(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_SMALL_CORRIDOR, nbt);
        this.length = nbt.getInt("Steps", 0);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putInt("Steps", this.length);
    }

    public static @Nullable BlockBox create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation) {
        int i = 3;
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 4, orientation);
        StructurePiece structurePiece = holder.getIntersecting(blockBox);
        if (structurePiece == null) {
            return null;
        }
        if (structurePiece.getBoundingBox().getMinY() == blockBox.getMinY()) {
            for (int j = 2; j >= 1; --j) {
                blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j, orientation);
                if (structurePiece.getBoundingBox().intersects(blockBox)) continue;
                return BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j + 1, orientation);
            }
        }
        return null;
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        for (int i = 0; i < this.length; ++i) {
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, chunkBox);
            for (int j = 1; j <= 3; ++j) {
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, chunkBox);
                this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, chunkBox);
                this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, chunkBox);
                this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, chunkBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, chunkBox);
            }
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, chunkBox);
            this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, chunkBox);
        }
    }
}
