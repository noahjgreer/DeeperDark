/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;

protected static abstract class OceanMonumentGenerator.Piece
extends StructurePiece {
    protected static final BlockState PRISMARINE = Blocks.PRISMARINE.getDefaultState();
    protected static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE_BRICKS.getDefaultState();
    protected static final BlockState DARK_PRISMARINE = Blocks.DARK_PRISMARINE.getDefaultState();
    protected static final BlockState ALSO_PRISMARINE_BRICKS = PRISMARINE_BRICKS;
    protected static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
    protected static final boolean field_31607 = true;
    protected static final BlockState WATER = Blocks.WATER.getDefaultState();
    protected static final Set<Block> ICE_BLOCKS = ImmutableSet.builder().add((Object)Blocks.ICE).add((Object)Blocks.PACKED_ICE).add((Object)Blocks.BLUE_ICE).add((Object)WATER.getBlock()).build();
    protected static final int BASE_SIZE_X = 8;
    protected static final int BASE_SIZE_Z = 8;
    protected static final int BASE_SIZE_Y = 4;
    protected static final int PIECE_GRID_SIZE_X = 5;
    protected static final int PIECE_GRID_SIZE_Z = 5;
    protected static final int PIECE_GRID_SIZE_Y = 3;
    protected static final int LEVEL_TWO_INDEX_BOUND = 25;
    protected static final int LEVEL_THREE_INDEX_BOUND = 75;
    protected static final int TWO_ZERO_ZERO_INDEX = OceanMonumentGenerator.Piece.getIndex(2, 0, 0);
    protected static final int TWO_TWO_ZERO_INDEX = OceanMonumentGenerator.Piece.getIndex(2, 2, 0);
    protected static final int ZERO_ONE_ZERO_INDEX = OceanMonumentGenerator.Piece.getIndex(0, 1, 0);
    protected static final int FOUR_ONE_ZERO_INDEX = OceanMonumentGenerator.Piece.getIndex(4, 1, 0);
    protected static final int WING_ROOM_A = 1001;
    protected static final int WING_ROOM_B = 1002;
    protected static final int CORE_ROOM = 1003;
    protected OceanMonumentGenerator.PieceSetting setting;

    protected static int getIndex(int x, int y, int z) {
        return y * 25 + z * 5 + x;
    }

    public OceanMonumentGenerator.Piece(StructurePieceType type, Direction orientation, int length, BlockBox box) {
        super(type, length, box);
        this.setOrientation(orientation);
    }

    protected OceanMonumentGenerator.Piece(StructurePieceType type, int length, Direction orientation, OceanMonumentGenerator.PieceSetting setting, int x, int y, int z) {
        super(type, length, OceanMonumentGenerator.Piece.createBox(orientation, setting, x, y, z));
        this.setOrientation(orientation);
        this.setting = setting;
    }

    private static BlockBox createBox(Direction orientation, OceanMonumentGenerator.PieceSetting setting, int x, int y, int z) {
        int i = setting.roomIndex;
        int j = i % 5;
        int k = i / 5 % 5;
        int l = i / 25;
        BlockBox blockBox = OceanMonumentGenerator.Piece.createBox(0, 0, 0, orientation, x * 8, y * 4, z * 8);
        switch (orientation) {
            case NORTH: {
                blockBox.move(j * 8, l * 4, -(k + z) * 8 + 1);
                break;
            }
            case SOUTH: {
                blockBox.move(j * 8, l * 4, k * 8);
                break;
            }
            case WEST: {
                blockBox.move(-(k + z) * 8 + 1, l * 4, j * 8);
                break;
            }
            default: {
                blockBox.move(k * 8, l * 4, j * 8);
            }
        }
        return blockBox;
    }

    public OceanMonumentGenerator.Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
    }

    protected void setAirAndWater(StructureWorldAccess world, BlockBox box, int x, int y, int z, int width, int height, int depth) {
        for (int i = y; i <= height; ++i) {
            for (int j = x; j <= width; ++j) {
                for (int k = z; k <= depth; ++k) {
                    BlockState blockState = this.getBlockAt(world, j, i, k, box);
                    if (ICE_BLOCKS.contains(blockState.getBlock())) continue;
                    if (this.applyYTransform(i) >= world.getSeaLevel() && blockState != WATER) {
                        this.addBlock(world, Blocks.AIR.getDefaultState(), j, i, k, box);
                        continue;
                    }
                    this.addBlock(world, WATER, j, i, k, box);
                }
            }
        }
    }

    protected void generateVerticalConnection(StructureWorldAccess world, BlockBox box, int x, int z, boolean neighbor) {
        if (neighbor) {
            this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 2, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, x + 5, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 0, x + 4, 0, z + 2, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 2, x + 4, 0, z + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, x + 2, 0, z + 3, x + 2, 0, z + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
            this.fillWithOutline(world, box, x + 5, 0, z + 3, x + 5, 0, z + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
        } else {
            this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
        }
    }

    protected void fillArea(StructureWorldAccess world, BlockBox box, int x, int y, int z, int width, int height, int depth, BlockState state) {
        for (int i = y; i <= height; ++i) {
            for (int j = x; j <= width; ++j) {
                for (int k = z; k <= depth; ++k) {
                    if (this.getBlockAt(world, j, i, k, box) != WATER) continue;
                    this.addBlock(world, state, j, i, k, box);
                }
            }
        }
    }

    protected boolean boxIntersects(BlockBox box, int x1, int z1, int x2, int z2) {
        int i = this.applyXTransform(x1, z1);
        int j = this.applyZTransform(x1, z1);
        int k = this.applyXTransform(x2, z2);
        int l = this.applyZTransform(x2, z2);
        return box.intersectsXZ(Math.min(i, k), Math.min(j, l), Math.max(i, k), Math.max(j, l));
    }

    protected void spawnElderGuardian(StructureWorldAccess world, BlockBox box, int x, int y, int z) {
        ElderGuardianEntity elderGuardianEntity;
        BlockPos.Mutable blockPos = this.offsetPos(x, y, z);
        if (box.contains(blockPos) && (elderGuardianEntity = EntityType.ELDER_GUARDIAN.create(world.toServerWorld(), SpawnReason.STRUCTURE)) != null) {
            elderGuardianEntity.heal(elderGuardianEntity.getMaxHealth());
            elderGuardianEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
            elderGuardianEntity.initialize(world, world.getLocalDifficulty(elderGuardianEntity.getBlockPos()), SpawnReason.STRUCTURE, null);
            world.spawnEntityAndPassengers(elderGuardianEntity);
        }
    }
}
