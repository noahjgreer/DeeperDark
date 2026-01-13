/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.FenceBlock
 *  net.minecraft.block.FenceGateBlock
 *  net.minecraft.block.HorizontalConnectingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.LeadItem
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class FenceBlock
extends HorizontalConnectingBlock {
    public static final MapCodec<FenceBlock> CODEC = FenceBlock.createCodec(FenceBlock::new);
    private final Function<BlockState, VoxelShape> cullingShapeFunction;

    public MapCodec<FenceBlock> getCodec() {
        return CODEC;
    }

    public FenceBlock(AbstractBlock.Settings settings) {
        super(4.0f, 16.0f, 4.0f, 16.0f, 24.0f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
        this.cullingShapeFunction = this.createShapeFunction(4.0f, 16.0f, 2.0f, 6.0f, 15.0f);
    }

    protected VoxelShape getCullingShape(BlockState state) {
        return (VoxelShape)this.cullingShapeFunction.apply(state);
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        Block block = state.getBlock();
        boolean bl = this.canConnectToFence(state);
        boolean bl2 = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect((BlockState)state, (Direction)dir);
        return !FenceBlock.cannotConnect((BlockState)state) && neighborIsFullSquare || bl || bl2;
    }

    private boolean canConnectToFence(BlockState state) {
        return state.isIn(BlockTags.FENCES) && state.isIn(BlockTags.WOODEN_FENCES) == this.getDefaultState().isIn(BlockTags.WOODEN_FENCES);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return !world.isClient() ? LeadItem.attachHeldMobsToBlock((PlayerEntity)player, (World)world, (BlockPos)pos) : ActionResult.PASS;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getPlacementState(ctx).with((Property)NORTH, (Comparable)Boolean.valueOf(this.canConnect(blockState, blockState.isSideSolidFullSquare((BlockView)blockView, blockPos2, Direction.SOUTH), Direction.SOUTH)))).with((Property)EAST, (Comparable)Boolean.valueOf(this.canConnect(blockState2, blockState2.isSideSolidFullSquare((BlockView)blockView, blockPos3, Direction.WEST), Direction.WEST)))).with((Property)SOUTH, (Comparable)Boolean.valueOf(this.canConnect(blockState3, blockState3.isSideSolidFullSquare((BlockView)blockView, blockPos4, Direction.NORTH), Direction.NORTH)))).with((Property)WEST, (Comparable)Boolean.valueOf(this.canConnect(blockState4, blockState4.isSideSolidFullSquare((BlockView)blockView, blockPos5, Direction.EAST), Direction.EAST)))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction.getAxis().isHorizontal()) {
            return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), (Comparable)Boolean.valueOf(this.canConnect(neighborState, neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction.getOpposite()), direction.getOpposite())));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED});
    }
}

