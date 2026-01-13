/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ConnectingBlock
 *  net.minecraft.block.HorizontalConnectingBlock
 *  net.minecraft.block.HorizontalConnectingBlock$1
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class HorizontalConnectingBlock
extends Block
implements Waterloggable {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final Map<Direction, BooleanProperty> FACING_PROPERTIES = (Map)ConnectingBlock.FACING_PROPERTIES.entrySet().stream().filter(entry -> ((Direction)entry.getKey()).getAxis().isHorizontal()).collect(Util.toMap());
    private final Function<BlockState, VoxelShape> collisionShapeFunction;
    private final Function<BlockState, VoxelShape> outlineShapeFunction;

    protected HorizontalConnectingBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, AbstractBlock.Settings settings) {
        super(settings);
        this.collisionShapeFunction = this.createShapeFunction(radius1, collisionHeight, boundingHeight1, 0.0f, collisionHeight);
        this.outlineShapeFunction = this.createShapeFunction(radius1, radius2, boundingHeight1, 0.0f, boundingHeight2);
    }

    protected abstract MapCodec<? extends HorizontalConnectingBlock> getCodec();

    protected Function<BlockState, VoxelShape> createShapeFunction(float radius1, float radius2, float height1, float offset2, float height2) {
        VoxelShape voxelShape = Block.createColumnShape((double)radius1, (double)0.0, (double)radius2);
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)height1, (double)offset2, (double)height2, (double)0.0, (double)8.0));
        return this.createShapeFunction(state -> {
            VoxelShape voxelShape2 = voxelShape;
            for (Map.Entry entry : FACING_PROPERTIES.entrySet()) {
                if (!((Boolean)state.get((Property)entry.getValue())).booleanValue()) continue;
                voxelShape2 = VoxelShapes.union((VoxelShape)voxelShape2, (VoxelShape)((VoxelShape)map.get(entry.getKey())));
            }
            return voxelShape2;
        }, new Property[]{WATERLOGGED});
    }

    protected boolean isTransparent(BlockState state) {
        return (Boolean)state.get((Property)WATERLOGGED) == false;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.outlineShapeFunction.apply(state);
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.collisionShapeFunction.apply(state);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (1.field_10909[rotation.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)EAST)));
            }
            case 2: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)NORTH)));
            }
            case 3: {
                return (BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)EAST, (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)SOUTH)));
            }
        }
        return state;
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (1.field_10908[mirror.ordinal()]) {
            case 1: {
                return (BlockState)((BlockState)state.with((Property)NORTH, (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)SOUTH, (Comparable)((Boolean)state.get((Property)NORTH)));
            }
            case 2: {
                return (BlockState)((BlockState)state.with((Property)EAST, (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)WEST, (Comparable)((Boolean)state.get((Property)EAST)));
            }
        }
        return super.mirror(state, mirror);
    }
}

