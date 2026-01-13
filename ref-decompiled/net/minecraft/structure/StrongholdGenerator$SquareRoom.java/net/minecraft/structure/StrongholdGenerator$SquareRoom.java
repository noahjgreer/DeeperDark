/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.loot.LootTables;
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

public static class StrongholdGenerator.SquareRoom
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 11;
    protected static final int SIZE_Y = 7;
    protected static final int SIZE_Z = 11;
    protected final int roomType;

    public StrongholdGenerator.SquareRoom(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, chainLength, boundingBox);
        this.setOrientation(orientation);
        this.entryDoor = this.getRandomEntrance(random);
        this.roomType = random.nextInt(5);
    }

    public StrongholdGenerator.SquareRoom(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_SQUARE_ROOM, nbt);
        this.roomType = nbt.getInt("Type", 0);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putInt("Type", this.roomType);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        this.fillForwardOpening((StrongholdGenerator.Start)start, holder, random, 4, 1);
        this.fillNWOpening((StrongholdGenerator.Start)start, holder, random, 1, 4);
        this.fillSEOpening((StrongholdGenerator.Start)start, holder, random, 1, 4);
    }

    public static @Nullable StrongholdGenerator.SquareRoom create(StructurePiecesHolder holder, Random random, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 7, 11, orientation);
        if (!StrongholdGenerator.SquareRoom.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.SquareRoom(chainLength, random, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 10, 6, 10, true, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, this.entryDoor, 4, 1, 0);
        this.fillWithOutline(world, chunkBox, 4, 1, 10, 6, 3, 10, AIR, AIR, false);
        this.fillWithOutline(world, chunkBox, 0, 1, 4, 0, 3, 6, AIR, AIR, false);
        this.fillWithOutline(world, chunkBox, 10, 1, 4, 10, 3, 6, AIR, AIR, false);
        switch (this.roomType) {
            default: {
                break;
            }
            case 0: {
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, chunkBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, chunkBox);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, chunkBox);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, chunkBox);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, chunkBox);
                this.addBlock(world, (BlockState)Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 4, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 5, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 4, 1, 6, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 4, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 5, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 6, 1, 6, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 4, chunkBox);
                this.addBlock(world, Blocks.SMOOTH_STONE_SLAB.getDefaultState(), 5, 1, 6, chunkBox);
                break;
            }
            case 1: {
                for (int i = 0; i < 5; ++i) {
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, chunkBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, chunkBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, chunkBox);
                    this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, chunkBox);
                }
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, chunkBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, chunkBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, chunkBox);
                this.addBlock(world, Blocks.WATER.getDefaultState(), 5, 4, 5, chunkBox);
                break;
            }
            case 2: {
                int i;
                for (i = 1; i <= 9; ++i) {
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 1, 3, i, chunkBox);
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 9, 3, i, chunkBox);
                }
                for (i = 1; i <= 9; ++i) {
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), i, 3, 1, chunkBox);
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), i, 3, 9, chunkBox);
                }
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, chunkBox);
                this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, chunkBox);
                for (i = 1; i <= 3; ++i) {
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, i, 4, chunkBox);
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, i, 4, chunkBox);
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 4, i, 6, chunkBox);
                    this.addBlock(world, Blocks.COBBLESTONE.getDefaultState(), 6, i, 6, chunkBox);
                }
                this.addBlock(world, Blocks.WALL_TORCH.getDefaultState(), 5, 3, 5, chunkBox);
                for (i = 2; i <= 8; ++i) {
                    this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, i, chunkBox);
                    this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, i, chunkBox);
                    if (i <= 3 || i >= 7) {
                        this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, i, chunkBox);
                        this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, i, chunkBox);
                        this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, i, chunkBox);
                    }
                    this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, i, chunkBox);
                    this.addBlock(world, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, i, chunkBox);
                }
                BlockState blockState = (BlockState)Blocks.LADDER.getDefaultState().with(LadderBlock.FACING, Direction.WEST);
                this.addBlock(world, blockState, 9, 1, 3, chunkBox);
                this.addBlock(world, blockState, 9, 2, 3, chunkBox);
                this.addBlock(world, blockState, 9, 3, 3, chunkBox);
                this.addChest(world, chunkBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
            }
        }
    }
}
