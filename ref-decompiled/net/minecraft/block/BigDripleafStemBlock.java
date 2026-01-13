/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BigDripleafBlock
 *  net.minecraft.block.BigDripleafStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockLocating
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.HeightLimitView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class BigDripleafStemBlock
extends HorizontalFacingBlock
implements Fertilizable,
Waterloggable {
    public static final MapCodec<BigDripleafStemBlock> CODEC = BigDripleafStemBlock.createCodec(BigDripleafStemBlock::new);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createColumnShape((double)6.0, (double)0.0, (double)16.0).offset(0.0, 0.0, 0.25).simplify());

    public MapCodec<BigDripleafStemBlock> getCodec() {
        return CODEC;
    }

    public BigDripleafStemBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get((Property)FACING));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED, FACING});
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        BlockState blockState2 = world.getBlockState(pos.up());
        return !(!blockState.isOf((Block)this) && !blockState.isIn(BlockTags.BIG_DRIPLEAF_PLACEABLE) || !blockState2.isOf((Block)this) && !blockState2.isOf(Blocks.BIG_DRIPLEAF));
    }

    protected static boolean placeStemAt(WorldAccess world, BlockPos pos, FluidState fluidState, Direction direction) {
        BlockState blockState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.isEqualAndStill((Fluid)Fluids.WATER)))).with((Property)FACING, (Comparable)direction);
        return world.setBlockState(pos, blockState, 3);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!(direction != Direction.DOWN && direction != Direction.UP || state.canPlaceAt(world, pos))) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        Optional optional = BlockLocating.findColumnEnd((BlockView)world, (BlockPos)pos, (Block)state.getBlock(), (Direction)Direction.UP, (Block)Blocks.BIG_DRIPLEAF);
        if (optional.isEmpty()) {
            return false;
        }
        BlockPos blockPos = ((BlockPos)optional.get()).up();
        BlockState blockState = world.getBlockState(blockPos);
        return BigDripleafBlock.canGrowInto((HeightLimitView)world, (BlockPos)blockPos, (BlockState)blockState);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Optional optional = BlockLocating.findColumnEnd((BlockView)world, (BlockPos)pos, (Block)state.getBlock(), (Direction)Direction.UP, (Block)Blocks.BIG_DRIPLEAF);
        if (optional.isEmpty()) {
            return;
        }
        BlockPos blockPos = (BlockPos)optional.get();
        BlockPos blockPos2 = blockPos.up();
        Direction direction = (Direction)state.get((Property)FACING);
        BigDripleafStemBlock.placeStemAt((WorldAccess)world, (BlockPos)blockPos, (FluidState)world.getFluidState(blockPos), (Direction)direction);
        BigDripleafBlock.placeDripleafAt((WorldAccess)world, (BlockPos)blockPos2, (FluidState)world.getFluidState(blockPos2), (Direction)direction);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)Blocks.BIG_DRIPLEAF);
    }
}

