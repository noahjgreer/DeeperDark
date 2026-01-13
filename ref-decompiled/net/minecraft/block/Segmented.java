/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Segmented
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 */
package net.minecraft.block;

import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public interface Segmented {
    public static final int SEGMENTS_PER_PLACEMENT = 1;
    public static final int MAX_SEGMENTS = 4;
    public static final IntProperty SEGMENT_AMOUNT = Properties.SEGMENT_AMOUNT;

    default public Function<BlockState, VoxelShape> createShapeFunction(EnumProperty<Direction> directionProperty, IntProperty segmentAmountProperty) {
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidShape((double)0.0, (double)0.0, (double)0.0, (double)8.0, (double)this.getHeight(), (double)8.0));
        return state -> {
            VoxelShape voxelShape = VoxelShapes.empty();
            Direction direction = (Direction)state.get((Property)directionProperty);
            int i = (Integer)state.get((Property)segmentAmountProperty);
            for (int j = 0; j < i; ++j) {
                voxelShape = VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)((VoxelShape)map.get(direction)));
                direction = direction.rotateYCounterclockwise();
            }
            return voxelShape.asCuboid();
        };
    }

    default public IntProperty getAmountProperty() {
        return SEGMENT_AMOUNT;
    }

    default public double getHeight() {
        return 1.0;
    }

    default public boolean shouldAddSegment(BlockState state, ItemPlacementContext context, IntProperty property) {
        return !context.shouldCancelInteraction() && context.getStack().isOf(state.getBlock().asItem()) && (Integer)state.get((Property)property) < 4;
    }

    default public BlockState getPlacementState(ItemPlacementContext context, Block block, IntProperty amountProperty, EnumProperty<Direction> directionProperty) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.isOf(block)) {
            return (BlockState)blockState.with((Property)amountProperty, (Comparable)Integer.valueOf(Math.min(4, (Integer)blockState.get((Property)amountProperty) + 1)));
        }
        return (BlockState)block.getDefaultState().with(directionProperty, (Comparable)context.getHorizontalPlayerFacing().getOpposite());
    }
}

