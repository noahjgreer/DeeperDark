/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
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

public static class StrongholdGenerator.PortalRoom
extends StrongholdGenerator.Piece {
    protected static final int SIZE_X = 11;
    protected static final int SIZE_Y = 8;
    protected static final int SIZE_Z = 16;
    private boolean spawnerPlaced;

    public StrongholdGenerator.PortalRoom(int chainLength, BlockBox boundingBox, Direction orientation) {
        super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, chainLength, boundingBox);
        this.setOrientation(orientation);
    }

    public StrongholdGenerator.PortalRoom(NbtCompound nbt) {
        super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, nbt);
        this.spawnerPlaced = nbt.getBoolean("Mob", false);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        super.writeNbt(context, nbt);
        nbt.putBoolean("Mob", this.spawnerPlaced);
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        if (start != null) {
            ((StrongholdGenerator.Start)start).portalRoom = this;
        }
    }

    public static @Nullable StrongholdGenerator.PortalRoom create(StructurePiecesHolder holder, int x, int y, int z, Direction orientation, int chainLength) {
        BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 8, 16, orientation);
        if (!StrongholdGenerator.PortalRoom.isInBounds(blockBox) || holder.getIntersecting(blockBox) != null) {
            return null;
        }
        return new StrongholdGenerator.PortalRoom(chainLength, blockBox, orientation);
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos.Mutable blockPos;
        int j;
        this.fillWithOutline(world, chunkBox, 0, 0, 0, 10, 7, 15, false, random, STONE_BRICK_RANDOMIZER);
        this.generateEntrance(world, random, chunkBox, StrongholdGenerator.Piece.EntranceType.GRATES, 4, 1, 0);
        int i = 6;
        this.fillWithOutline(world, chunkBox, 1, 6, 1, 1, 6, 14, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 9, 6, 1, 9, 6, 14, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 2, 6, 1, 8, 6, 2, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 2, 6, 14, 8, 6, 14, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 1, 1, 1, 2, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 8, 1, 1, 9, 1, 4, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
        this.fillWithOutline(world, chunkBox, 3, 1, 8, 7, 1, 12, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
        BlockState blockState = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, true)).with(PaneBlock.SOUTH, true);
        BlockState blockState2 = (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true)).with(PaneBlock.EAST, true);
        for (j = 3; j < 14; j += 2) {
            this.fillWithOutline(world, chunkBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
            this.fillWithOutline(world, chunkBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
        }
        for (j = 2; j < 9; j += 2) {
            this.fillWithOutline(world, chunkBox, j, 3, 15, j, 4, 15, blockState2, blockState2, false);
        }
        BlockState blockState3 = (BlockState)Blocks.STONE_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
        this.fillWithOutline(world, chunkBox, 4, 1, 5, 6, 1, 7, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 2, 6, 6, 2, 7, false, random, STONE_BRICK_RANDOMIZER);
        this.fillWithOutline(world, chunkBox, 4, 3, 7, 6, 3, 7, false, random, STONE_BRICK_RANDOMIZER);
        for (int k = 4; k <= 6; ++k) {
            this.addBlock(world, blockState3, k, 1, 4, chunkBox);
            this.addBlock(world, blockState3, k, 2, 5, chunkBox);
            this.addBlock(world, blockState3, k, 3, 6, chunkBox);
        }
        BlockState blockState4 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.NORTH);
        BlockState blockState5 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.SOUTH);
        BlockState blockState6 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.EAST);
        BlockState blockState7 = (BlockState)Blocks.END_PORTAL_FRAME.getDefaultState().with(EndPortalFrameBlock.FACING, Direction.WEST);
        boolean bl = true;
        boolean[] bls = new boolean[12];
        for (int l = 0; l < bls.length; ++l) {
            bls[l] = random.nextFloat() > 0.9f;
            bl &= bls[l];
        }
        this.addBlock(world, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[0]), 4, 3, 8, chunkBox);
        this.addBlock(world, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[1]), 5, 3, 8, chunkBox);
        this.addBlock(world, (BlockState)blockState4.with(EndPortalFrameBlock.EYE, bls[2]), 6, 3, 8, chunkBox);
        this.addBlock(world, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[3]), 4, 3, 12, chunkBox);
        this.addBlock(world, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[4]), 5, 3, 12, chunkBox);
        this.addBlock(world, (BlockState)blockState5.with(EndPortalFrameBlock.EYE, bls[5]), 6, 3, 12, chunkBox);
        this.addBlock(world, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[6]), 3, 3, 9, chunkBox);
        this.addBlock(world, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[7]), 3, 3, 10, chunkBox);
        this.addBlock(world, (BlockState)blockState6.with(EndPortalFrameBlock.EYE, bls[8]), 3, 3, 11, chunkBox);
        this.addBlock(world, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[9]), 7, 3, 9, chunkBox);
        this.addBlock(world, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[10]), 7, 3, 10, chunkBox);
        this.addBlock(world, (BlockState)blockState7.with(EndPortalFrameBlock.EYE, bls[11]), 7, 3, 11, chunkBox);
        if (bl) {
            BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
            this.addBlock(world, blockState8, 4, 3, 9, chunkBox);
            this.addBlock(world, blockState8, 5, 3, 9, chunkBox);
            this.addBlock(world, blockState8, 6, 3, 9, chunkBox);
            this.addBlock(world, blockState8, 4, 3, 10, chunkBox);
            this.addBlock(world, blockState8, 5, 3, 10, chunkBox);
            this.addBlock(world, blockState8, 6, 3, 10, chunkBox);
            this.addBlock(world, blockState8, 4, 3, 11, chunkBox);
            this.addBlock(world, blockState8, 5, 3, 11, chunkBox);
            this.addBlock(world, blockState8, 6, 3, 11, chunkBox);
        }
        if (!this.spawnerPlaced && chunkBox.contains(blockPos = this.offsetPos(5, 3, 6))) {
            this.spawnerPlaced = true;
            world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof MobSpawnerBlockEntity) {
                MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
                mobSpawnerBlockEntity.setEntityType(EntityType.SILVERFISH, random);
            }
        }
    }
}
