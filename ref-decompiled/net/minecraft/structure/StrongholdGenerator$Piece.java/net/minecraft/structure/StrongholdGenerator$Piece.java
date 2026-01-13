/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import org.jspecify.annotations.Nullable;

static abstract class StrongholdGenerator.Piece
extends StructurePiece {
    protected EntranceType entryDoor = EntranceType.OPENING;

    protected StrongholdGenerator.Piece(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
        super(structurePieceType, i, blockBox);
    }

    public StrongholdGenerator.Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
        super(structurePieceType, nbtCompound);
        this.entryDoor = nbtCompound.get("EntryDoor", EntranceType.CODEC).orElseThrow();
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.put("EntryDoor", EntranceType.CODEC, this.entryDoor);
    }

    protected void generateEntrance(StructureWorldAccess world, Random random, BlockBox boundingBox, EntranceType type, int x, int y, int z) {
        switch (type.ordinal()) {
            case 0: {
                this.fillWithOutline(world, boundingBox, x, y, z, x + 3 - 1, y + 3 - 1, z, AIR, AIR, false);
                break;
            }
            case 1: {
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
                this.addBlock(world, Blocks.OAK_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.OAK_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
                break;
            }
            case 2: {
                this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y, z, boundingBox);
                this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x + 1, y + 1, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), x, y, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, true), x, y + 1, z, boundingBox);
                this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x, y + 2, z, boundingBox);
                this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x + 1, y + 2, z, boundingBox);
                this.addBlock(world, (BlockState)((BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true)).with(PaneBlock.WEST, true), x + 2, y + 2, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), x + 2, y + 1, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, true), x + 2, y, z, boundingBox);
                break;
            }
            case 3: {
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, boundingBox);
                this.addBlock(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, boundingBox);
                this.addBlock(world, Blocks.IRON_DOOR.getDefaultState(), x + 1, y, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x + 1, y + 1, z, boundingBox);
                this.addBlock(world, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(ButtonBlock.FACING, Direction.NORTH), x + 2, y + 1, z + 1, boundingBox);
                this.addBlock(world, (BlockState)Blocks.STONE_BUTTON.getDefaultState().with(ButtonBlock.FACING, Direction.SOUTH), x + 2, y + 1, z - 1, boundingBox);
            }
        }
    }

    protected EntranceType getRandomEntrance(Random random) {
        int i = random.nextInt(5);
        switch (i) {
            default: {
                return EntranceType.OPENING;
            }
            case 2: {
                return EntranceType.WOOD_DOOR;
            }
            case 3: {
                return EntranceType.GRATES;
            }
            case 4: 
        }
        return EntranceType.IRON_DOOR;
    }

    protected @Nullable StructurePiece fillForwardOpening(StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int leftRightOffset, int heightOffset) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, direction, this.getChainLength());
                }
                case SOUTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, direction, this.getChainLength());
                }
                case WEST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, direction, this.getChainLength());
                }
                case EAST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, direction, this.getChainLength());
                }
            }
        }
        return null;
    }

    protected @Nullable StructurePiece fillNWOpening(StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.WEST, this.getChainLength());
                }
                case SOUTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() - 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.WEST, this.getChainLength());
                }
                case WEST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, Direction.NORTH, this.getChainLength());
                }
                case EAST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() - 1, Direction.NORTH, this.getChainLength());
                }
            }
        }
        return null;
    }

    protected @Nullable StructurePiece fillSEOpening(StrongholdGenerator.Start start, StructurePiecesHolder holder, Random random, int heightOffset, int leftRightOffset) {
        Direction direction = this.getFacing();
        if (direction != null) {
            switch (direction) {
                case NORTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.EAST, this.getChainLength());
                }
                case SOUTH: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMaxX() + 1, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMinZ() + leftRightOffset, Direction.EAST, this.getChainLength());
                }
                case WEST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, this.getChainLength());
                }
                case EAST: {
                    return StrongholdGenerator.pieceGenerator(start, holder, random, this.boundingBox.getMinX() + leftRightOffset, this.boundingBox.getMinY() + heightOffset, this.boundingBox.getMaxZ() + 1, Direction.SOUTH, this.getChainLength());
                }
            }
        }
        return null;
    }

    protected static boolean isInBounds(BlockBox boundingBox) {
        return boundingBox.getMinY() > 10;
    }

    protected static final class EntranceType
    extends Enum<EntranceType> {
        public static final /* enum */ EntranceType OPENING = new EntranceType();
        public static final /* enum */ EntranceType WOOD_DOOR = new EntranceType();
        public static final /* enum */ EntranceType GRATES = new EntranceType();
        public static final /* enum */ EntranceType IRON_DOOR = new EntranceType();
        @Deprecated
        public static final Codec<EntranceType> CODEC;
        private static final /* synthetic */ EntranceType[] field_15292;

        public static EntranceType[] values() {
            return (EntranceType[])field_15292.clone();
        }

        public static EntranceType valueOf(String string) {
            return Enum.valueOf(EntranceType.class, string);
        }

        private static /* synthetic */ EntranceType[] method_36762() {
            return new EntranceType[]{OPENING, WOOD_DOOR, GRATES, IRON_DOOR};
        }

        static {
            field_15292 = EntranceType.method_36762();
            CODEC = Codecs.enumByName(EntranceType::valueOf);
        }
    }
}
