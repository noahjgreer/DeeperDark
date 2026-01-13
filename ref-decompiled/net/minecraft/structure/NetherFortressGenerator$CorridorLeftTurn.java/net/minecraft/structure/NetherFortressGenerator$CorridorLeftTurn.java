/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.NetherFortressGenerator;
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

public static class NetherFortressGenerator.CorridorLeftTurn
extends NetherFortressGenerator.Piece {
    private static final int SIZE_X = 5;
    private static final int SIZE_Y = 7;
    private static final int SIZE_Z = 5;
    private boolean containsChest;

    public NetherFortressGenerator.CorridorLeftTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.containsChest = random.nextInt(3) == 0;
    }

    public NetherFortressGenerator.CorridorLeftTurn(NbtCompound nbt) {
        super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, nbt);
        this.containsChest = nbt.getBoolean("Chest", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Chest", this.containsChest);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillNWOpening((NetherFortressGenerator.Start)start, holder, random, 0, 1, true);
    }

    public static @Nullable NetherFortressGenerator.CorridorLeftTurn create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
        if (!NetherFortressGenerator.CorridorLeftTurn.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new NetherFortressGenerator.CorridorLeftTurn(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
        this.fillWithOutline(world, chunkBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
        this.fillWithOutline(world, chunkBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
        this.fillWithOutline(world, chunkBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
        if (this.containsChest && chunkBox.contains(this.offsetPos(3, 2, 3))) {
            this.containsChest = false;
            this.addChest(world, chunkBox, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
        }
        this.fillWithOutline(world, chunkBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
        for (int i = 0; i <= 4; ++i) {
            for (int j = 0; j <= 4; ++j) {
                this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, chunkBox);
            }
        }
    }
}
