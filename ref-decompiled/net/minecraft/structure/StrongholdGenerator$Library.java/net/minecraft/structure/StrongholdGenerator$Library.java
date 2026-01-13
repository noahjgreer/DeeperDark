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
import net.minecraft.block.LadderBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
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

public static class StrongholdGenerator.Library
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 14;
    protected static final int field_31636 = 6;
    protected static final int SIZE_Y = 11;
    protected static final int SIZE_Z = 15;
    private final boolean tall;

    public StrongholdGenerator.Library(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_LIBRARY, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
        this.tall = boundingBox.getBlockCountY() > 6;
    }

    public StrongholdGenerator.Library(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_LIBRARY, nbt);
        this.tall = nbt.getBoolean("Tall", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Tall", this.tall);
    }

    public static @Nullable StrongholdGenerator.Library create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 11, 15, orientation);
        if (!(StrongholdGenerator.Library.isInBounds(blockBox) && holder.getIntersecting(blockBox) == null || StrongholdGenerator.Library.isInBounds(blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 6, 15, orientation)) && holder.getIntersecting(blockBox) == null)) {
            return null;
        }
        return new StrongholdGenerator.Library(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        int l;
        int i = 11;
        if (!this.tall) {
            i = 6;
        }
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 13, i - 1, 14, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 1, 0);
        this.fillWithOutlineUnderSeaLevel(world, chunkBox, random, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
        boolean j = true;
        int k = 12;
        for (l = 1; l <= 13; ++l) {
            if ((l - 1) % 4 == 0) {
                this.fillWithOutline(world, chunkBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(world, chunkBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, chunkBox);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, chunkBox);
                if (!this.tall) continue;
                this.fillWithOutline(world, chunkBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                this.fillWithOutline(world, chunkBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
                continue;
            }
            this.fillWithOutline(world, chunkBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            if (!this.tall) continue;
            this.fillWithOutline(world, chunkBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
        }
        for (l = 3; l < 12; l += 2) {
            this.fillWithOutline(world, chunkBox, 3, 1, l, 4, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 6, 1, l, 7, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 9, 1, l, 10, 3, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
        }
        if (this.tall) {
            this.fillWithOutline(world, chunkBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.fillWithOutline(world, chunkBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
            this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, chunkBox);
            this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, chunkBox);
            this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, chunkBox);
            BlockState blockState = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            BlockState blockState2 = (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.SOUTH, true);
            this.fillWithOutline(world, chunkBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
            this.fillWithOutline(world, chunkBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
            this.fillWithOutline(world, chunkBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
            this.addBlock(world, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 3, 6, 2, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.EAST, true), 3, 6, 12, chunkBox);
            this.addBlock(world, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.WEST, true), 10, 6, 2, chunkBox);
            for (int m = 0; m <= 2; ++m) {
                this.addBlock(world, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.SOUTH, true)).with(FenceBlock.WEST, true), 8 + m, 6, 12 - m, chunkBox);
                if (m == 2) continue;
                this.addBlock(world, (BlockState)((BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.NORTH, true)).with(FenceBlock.EAST, true), 8 + m, 6, 11 - m, chunkBox);
            }
            BlockState blockState3 = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.SOUTH);
            this.addBlock(world, blockState3, 10, 1, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 2, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 3, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 4, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 5, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 6, 13, chunkBox);
            this.addBlock(world, blockState3, 10, 7, 13, chunkBox);
            int n = 7;
            int o = 7;
            BlockState blockState4 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.EAST, true);
            this.addBlock(world, blockState4, 6, 9, 7, chunkBox);
            BlockState blockState5 = (BlockState)Blocks.OAK_FENCE.getDefaultState().with(FenceBlock.WEST, true);
            this.addBlock(world, blockState5, 7, 9, 7, chunkBox);
            this.addBlock(world, blockState4, 6, 8, 7, chunkBox);
            this.addBlock(world, blockState5, 7, 8, 7, chunkBox);
            BlockState blockState6 = (BlockState)((BlockState)blockState2.with(FenceBlock.WEST, true)).with(FenceBlock.EAST, true);
            this.addBlock(world, blockState6, 6, 7, 7, chunkBox);
            this.addBlock(world, blockState6, 7, 7, 7, chunkBox);
            this.addBlock(world, blockState4, 5, 7, 7, chunkBox);
            this.addBlock(world, blockState5, 8, 7, 7, chunkBox);
            this.addBlock(world, (BlockState)blockState4.with(FenceBlock.NORTH, true), 6, 7, 6, chunkBox);
            this.addBlock(world, (BlockState)blockState4.with(FenceBlock.SOUTH, true), 6, 7, 8, chunkBox);
            this.addBlock(world, (BlockState)blockState5.with(FenceBlock.NORTH, true), 7, 7, 6, chunkBox);
            this.addBlock(world, (BlockState)blockState5.with(FenceBlock.SOUTH, true), 7, 7, 8, chunkBox);
            BlockState blockState7 = Blocks.TORCH.getDefaultState();
            this.addBlock(world, blockState7, 5, 8, 7, chunkBox);
            this.addBlock(world, blockState7, 8, 8, 7, chunkBox);
            this.addBlock(world, blockState7, 6, 8, 6, chunkBox);
            this.addBlock(world, blockState7, 6, 8, 8, chunkBox);
            this.addBlock(world, blockState7, 7, 8, 6, chunkBox);
            this.addBlock(world, blockState7, 7, 8, 8, chunkBox);
        }
        this.addChest(world, chunkBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
        if (this.tall) {
            this.addBlock(world, AIR, 12, 9, 1, chunkBox);
            this.addChest(world, chunkBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
        }
    }
}
