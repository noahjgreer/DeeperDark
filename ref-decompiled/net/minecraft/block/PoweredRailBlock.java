/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRailBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.PoweredRailBlock
 *  net.minecraft.block.PoweredRailBlock$1
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoweredRailBlock
extends AbstractRailBlock {
    public static final MapCodec<PoweredRailBlock> CODEC = PoweredRailBlock.createCodec(PoweredRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public MapCodec<PoweredRailBlock> getCodec() {
        return CODEC;
    }

    public PoweredRailBlock(AbstractBlock.Settings settings) {
        super(true, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)SHAPE, (Comparable)RailShape.NORTH_SOUTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean isPoweredByOtherRails(World world, BlockPos pos, BlockState state, boolean bl, int distance) {
        if (distance >= 8) {
            return false;
        }
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean bl2 = true;
        RailShape railShape = (RailShape)state.get((Property)SHAPE);
        switch (1.field_11368[railShape.ordinal()]) {
            case 1: {
                if (bl) {
                    ++k;
                    break;
                }
                --k;
                break;
            }
            case 2: {
                if (bl) {
                    --i;
                    break;
                }
                ++i;
                break;
            }
            case 3: {
                if (bl) {
                    --i;
                } else {
                    ++i;
                    ++j;
                    bl2 = false;
                }
                railShape = RailShape.EAST_WEST;
                break;
            }
            case 4: {
                if (bl) {
                    --i;
                    ++j;
                    bl2 = false;
                } else {
                    ++i;
                }
                railShape = RailShape.EAST_WEST;
                break;
            }
            case 5: {
                if (bl) {
                    ++k;
                } else {
                    --k;
                    ++j;
                    bl2 = false;
                }
                railShape = RailShape.NORTH_SOUTH;
                break;
            }
            case 6: {
                if (bl) {
                    ++k;
                    ++j;
                    bl2 = false;
                } else {
                    --k;
                }
                railShape = RailShape.NORTH_SOUTH;
            }
        }
        if (this.isPoweredByOtherRails(world, new BlockPos(i, j, k), bl, distance, railShape)) {
            return true;
        }
        return bl2 && this.isPoweredByOtherRails(world, new BlockPos(i, j - 1, k), bl, distance, railShape);
    }

    protected boolean isPoweredByOtherRails(World world, BlockPos pos, boolean bl, int distance, RailShape shape) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isOf((Block)this)) {
            return false;
        }
        RailShape railShape = (RailShape)blockState.get((Property)SHAPE);
        if (shape == RailShape.EAST_WEST && (railShape == RailShape.NORTH_SOUTH || railShape == RailShape.ASCENDING_NORTH || railShape == RailShape.ASCENDING_SOUTH)) {
            return false;
        }
        if (shape == RailShape.NORTH_SOUTH && (railShape == RailShape.EAST_WEST || railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST)) {
            return false;
        }
        if (((Boolean)blockState.get((Property)POWERED)).booleanValue()) {
            if (world.isReceivingRedstonePower(pos)) {
                return true;
            }
            return this.isPoweredByOtherRails(world, pos, blockState, bl, distance + 1);
        }
        return false;
    }

    protected void updateBlockState(BlockState state, World world, BlockPos pos, Block neighbor) {
        boolean bl2;
        boolean bl = (Boolean)state.get((Property)POWERED);
        boolean bl3 = bl2 = world.isReceivingRedstonePower(pos) || this.isPoweredByOtherRails(world, pos, state, true, 0) || this.isPoweredByOtherRails(world, pos, state, false, 0);
        if (bl2 != bl) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl2)), 3);
            world.updateNeighbors(pos.down(), (Block)this);
            if (((RailShape)state.get((Property)SHAPE)).isAscending()) {
                world.updateNeighbors(pos.up(), (Block)this);
            }
        }
    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        RailShape railShape = (RailShape)state.get((Property)SHAPE);
        RailShape railShape2 = this.rotateShape(railShape, rotation);
        return (BlockState)state.with((Property)SHAPE, (Comparable)railShape2);
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = (RailShape)state.get((Property)SHAPE);
        RailShape railShape2 = this.mirrorShape(railShape, mirror);
        return (BlockState)state.with((Property)SHAPE, (Comparable)railShape2);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SHAPE, POWERED, WATERLOGGED});
    }
}

