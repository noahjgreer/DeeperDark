/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.DirtPathBlock
 *  net.minecraft.block.FarmlandBlock
 *  net.minecraft.block.FenceGateBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class DirtPathBlock
extends Block {
    public static final MapCodec<DirtPathBlock> CODEC = DirtPathBlock.createCodec(DirtPathBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)15.0);

    public MapCodec<DirtPathBlock> getCodec() {
        return CODEC;
    }

    public DirtPathBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!this.getDefaultState().canPlaceAt((WorldView)ctx.getWorld(), ctx.getBlockPos())) {
            return Block.pushEntitiesUpBeforeBlockChange((BlockState)this.getDefaultState(), (BlockState)Blocks.DIRT.getDefaultState(), (WorldAccess)ctx.getWorld(), (BlockPos)ctx.getBlockPos());
        }
        return super.getPlacementState(ctx);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        FarmlandBlock.setToDirt(null, (BlockState)state, (World)world, (BlockPos)pos);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.up());
        return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

