/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.HorizontalConnectingBlock
 *  net.minecraft.block.PaneBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
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
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
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

/*
 * Exception performing whole class analysis ignored.
 */
public class PaneBlock
extends HorizontalConnectingBlock {
    public static final MapCodec<PaneBlock> CODEC = PaneBlock.createCodec(PaneBlock::new);

    public MapCodec<? extends PaneBlock> getCodec() {
        return CODEC;
    }

    public PaneBlock(AbstractBlock.Settings settings) {
        super(2.0f, 16.0f, 2.0f, 16.0f, 16.0f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.south();
        BlockPos blockPos4 = blockPos.west();
        BlockPos blockPos5 = blockPos.east();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)NORTH, (Comparable)Boolean.valueOf(this.connectsTo(blockState, blockState.isSideSolidFullSquare((BlockView)blockView, blockPos2, Direction.SOUTH))))).with((Property)SOUTH, (Comparable)Boolean.valueOf(this.connectsTo(blockState2, blockState2.isSideSolidFullSquare((BlockView)blockView, blockPos3, Direction.NORTH))))).with((Property)WEST, (Comparable)Boolean.valueOf(this.connectsTo(blockState3, blockState3.isSideSolidFullSquare((BlockView)blockView, blockPos4, Direction.EAST))))).with((Property)EAST, (Comparable)Boolean.valueOf(this.connectsTo(blockState4, blockState4.isSideSolidFullSquare((BlockView)blockView, blockPos5, Direction.WEST))))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), (Comparable)Boolean.valueOf(this.connectsTo(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction.getOpposite()))));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf((Block)this) || stateFrom.isIn(BlockTags.BARS) && state.isIn(BlockTags.BARS) && stateFrom.contains((Property)FACING_PROPERTIES.get(direction.getOpposite()))) {
            if (!direction.getAxis().isHorizontal()) {
                return true;
            }
            if (((Boolean)state.get((Property)FACING_PROPERTIES.get(direction))).booleanValue() && ((Boolean)stateFrom.get((Property)FACING_PROPERTIES.get(direction.getOpposite()))).booleanValue()) {
                return true;
            }
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    public final boolean connectsTo(BlockState state, boolean sideSolidFullSquare) {
        return !PaneBlock.cannotConnect((BlockState)state) && sideSolidFullSquare || state.getBlock() instanceof PaneBlock || state.isIn(BlockTags.WALLS);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED});
    }
}

