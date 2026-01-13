/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureContext;
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

public static class NetherFortressGenerator.BridgeEnd
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 10;
    private static final int SIZE_Z = 8;
    private final int seed;

    public NetherFortressGenerator.BridgeEnd(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.seed = random.nextInt();
    }

    public NetherFortressGenerator.BridgeEnd(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, nbt);
        this.seed = nbt.getInt("Seed", 0);
    }

    public static @Nullable NetherFortressGenerator.BridgeEnd create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 8, orientation);
        if (!NetherFortressGenerator.BridgeEnd.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.BridgeEnd(chainLength, random, blockBox, orientation);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putInt("Seed", this.seed);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int k;
        int j;
        int i;
        Random random2 = Random.create(this.seed);
        for (i = 0; i <= 4; ++i) {
            for (j = 3; j <= 4; ++j) {
                k = random2.nextInt(8);
                this.fillWithOutline(world, chunkBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }
        }
        i = random2.nextInt(8);
        this.fillWithOutline(world, chunkBox, 0, 5, 0, 0, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        i = random2.nextInt(8);
        this.fillWithOutline(world, chunkBox, 4, 5, 0, 4, 5, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        for (i = 0; i <= 4; ++i) {
            j = random2.nextInt(5);
            this.fillWithOutline(world, chunkBox, i, 2, 0, i, 2, j, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        }
        for (i = 0; i <= 4; ++i) {
            for (j = 0; j <= 1; ++j) {
                k = random2.nextInt(3);
                this.fillWithOutline(world, chunkBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
            }
        }
    }
}
