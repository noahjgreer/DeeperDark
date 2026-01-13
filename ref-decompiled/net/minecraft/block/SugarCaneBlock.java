/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SugarCaneBlock
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SugarCaneBlock
extends Block {
    public static final MapCodec<SugarCaneBlock> CODEC = SugarCaneBlock.createCodec(SugarCaneBlock::new);
    public static final IntProperty AGE = Properties.AGE_15;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)16.0);

    public MapCodec<SugarCaneBlock> getCodec() {
        return CODEC;
    }

    public SugarCaneBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isAir(pos.up())) {
            int i = 1;
            while (world.getBlockState(pos.down(i)).isOf((Block)this)) {
                ++i;
            }
            if (i < 3) {
                int j = (Integer)state.get((Property)AGE);
                if (j == 15) {
                    world.setBlockState(pos.up(), this.getDefaultState());
                    world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(0)), 260);
                } else {
                    world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(j + 1)), 260);
                }
            }
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        if (blockState.isOf((Block)this)) {
            return true;
        }
        if (blockState.isIn(BlockTags.DIRT) || blockState.isIn(BlockTags.SAND)) {
            BlockPos blockPos = pos.down();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockState blockState2 = world.getBlockState(blockPos.offset(direction));
                FluidState fluidState = world.getFluidState(blockPos.offset(direction));
                if (!fluidState.isIn(FluidTags.WATER) && !blockState2.isOf(Blocks.FROSTED_ICE)) continue;
                return true;
            }
        }
        return false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }
}

