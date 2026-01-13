/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ConnectingBlock
 *  net.minecraft.block.MushroomBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class MushroomBlock
extends Block {
    public static final MapCodec<MushroomBlock> CODEC = MushroomBlock.createCodec(MushroomBlock::new);
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;

    public MapCodec<MushroomBlock> getCodec() {
        return CODEC;
    }

    public MushroomBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)NORTH, (Comparable)Boolean.valueOf(true))).with((Property)EAST, (Comparable)Boolean.valueOf(true))).with((Property)SOUTH, (Comparable)Boolean.valueOf(true))).with((Property)WEST, (Comparable)Boolean.valueOf(true))).with((Property)UP, (Comparable)Boolean.valueOf(true))).with((Property)DOWN, (Comparable)Boolean.valueOf(true)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)DOWN, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.down()).isOf((Block)this)))).with((Property)UP, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.up()).isOf((Block)this)))).with((Property)NORTH, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.north()).isOf((Block)this)))).with((Property)EAST, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.east()).isOf((Block)this)))).with((Property)SOUTH, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.south()).isOf((Block)this)))).with((Property)WEST, (Comparable)Boolean.valueOf(!blockView.getBlockState(blockPos.west()).isOf((Block)this)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (neighborState.isOf((Block)this)) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), (Comparable)Boolean.valueOf(false));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.NORTH)), (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.SOUTH)), (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.EAST)), (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.WEST)), (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.UP)), (Comparable)((Boolean)state.get((Property)UP)))).with((Property)FACING_PROPERTIES.get(rotation.rotate(Direction.DOWN)), (Comparable)((Boolean)state.get((Property)DOWN)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.NORTH)), (Comparable)((Boolean)state.get((Property)NORTH)))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.SOUTH)), (Comparable)((Boolean)state.get((Property)SOUTH)))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.EAST)), (Comparable)((Boolean)state.get((Property)EAST)))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.WEST)), (Comparable)((Boolean)state.get((Property)WEST)))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.UP)), (Comparable)((Boolean)state.get((Property)UP)))).with((Property)FACING_PROPERTIES.get(mirror.apply(Direction.DOWN)), (Comparable)((Boolean)state.get((Property)DOWN)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{UP, DOWN, NORTH, EAST, SOUTH, WEST});
    }
}

