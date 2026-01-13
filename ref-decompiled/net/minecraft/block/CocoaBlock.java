/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CocoaBlock
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class CocoaBlock
extends HorizontalFacingBlock
implements Fertilizable {
    public static final MapCodec<CocoaBlock> CODEC = CocoaBlock.createCodec(CocoaBlock::new);
    public static final int MAX_AGE = 2;
    public static final IntProperty AGE = Properties.AGE_2;
    private static final List<Map<Direction, VoxelShape>> SHAPES = IntStream.rangeClosed(0, 2).mapToObj(age -> VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createColumnShape((double)(4 + age * 2), (double)(7 - age * 2), (double)12.0).offset(0.0, 0.0, (double)(age - 5) / 16.0).simplify())).toList();

    public MapCodec<CocoaBlock> getCodec() {
        return CODEC;
    }

    public CocoaBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Integer)state.get((Property)AGE) < 2;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i;
        if (world.random.nextInt(5) == 0 && (i = ((Integer)state.get((Property)AGE)).intValue()) < 2) {
            world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i + 1)), 2);
        }
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.offset((Direction)state.get((Property)FACING)));
        return blockState.isIn(BlockTags.JUNGLE_LOGS);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)((Map)SHAPES.get((Integer)state.get((Property)AGE))).get(state.get((Property)FACING));
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        World worldView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        for (Direction direction : ctx.getPlacementDirections()) {
            if (!direction.getAxis().isHorizontal() || !(blockState = (BlockState)blockState.with((Property)FACING, (Comparable)direction)).canPlaceAt((WorldView)worldView, blockPos)) continue;
            return blockState;
        }
        return null;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == state.get((Property)FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return (Integer)state.get((Property)AGE) < 2;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf((Integer)state.get((Property)AGE) + 1)), 2);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, AGE});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

