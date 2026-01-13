/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ChorusPlantBlock
 *  net.minecraft.block.ConnectingBlock
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.block.ConnectingBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class ChorusPlantBlock
extends ConnectingBlock {
    public static final MapCodec<ChorusPlantBlock> CODEC = ChorusPlantBlock.createCodec(ChorusPlantBlock::new);

    public MapCodec<ChorusPlantBlock> getCodec() {
        return CODEC;
    }

    public ChorusPlantBlock(AbstractBlock.Settings settings) {
        super(10.0f, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)NORTH, (Comparable)Boolean.valueOf(false))).with((Property)EAST, (Comparable)Boolean.valueOf(false))).with((Property)SOUTH, (Comparable)Boolean.valueOf(false))).with((Property)WEST, (Comparable)Boolean.valueOf(false))).with((Property)UP, (Comparable)Boolean.valueOf(false))).with((Property)DOWN, (Comparable)Boolean.valueOf(false)));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return ChorusPlantBlock.withConnectionProperties((BlockView)ctx.getWorld(), (BlockPos)ctx.getBlockPos(), (BlockState)this.getDefaultState());
    }

    public static BlockState withConnectionProperties(BlockView world, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos.down());
        BlockState blockState2 = world.getBlockState(pos.up());
        BlockState blockState3 = world.getBlockState(pos.north());
        BlockState blockState4 = world.getBlockState(pos.east());
        BlockState blockState5 = world.getBlockState(pos.south());
        BlockState blockState6 = world.getBlockState(pos.west());
        Block block = state.getBlock();
        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)state.withIfExists((Property)DOWN, (Comparable)Boolean.valueOf(blockState.isOf(block) || blockState.isOf(Blocks.CHORUS_FLOWER) || blockState.isOf(Blocks.END_STONE)))).withIfExists((Property)UP, (Comparable)Boolean.valueOf(blockState2.isOf(block) || blockState2.isOf(Blocks.CHORUS_FLOWER)))).withIfExists((Property)NORTH, (Comparable)Boolean.valueOf(blockState3.isOf(block) || blockState3.isOf(Blocks.CHORUS_FLOWER)))).withIfExists((Property)EAST, (Comparable)Boolean.valueOf(blockState4.isOf(block) || blockState4.isOf(Blocks.CHORUS_FLOWER)))).withIfExists((Property)SOUTH, (Comparable)Boolean.valueOf(blockState5.isOf(block) || blockState5.isOf(Blocks.CHORUS_FLOWER)))).withIfExists((Property)WEST, (Comparable)Boolean.valueOf(blockState6.isOf(block) || blockState6.isOf(Blocks.CHORUS_FLOWER)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        }
        boolean bl = neighborState.isOf((Block)this) || neighborState.isOf(Blocks.CHORUS_FLOWER) || direction == Direction.DOWN && neighborState.isOf(Blocks.END_STONE);
        return (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), (Comparable)Boolean.valueOf(bl));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (!blockState2.isOf((Block)this)) continue;
            if (bl) {
                return false;
            }
            BlockState blockState3 = world.getBlockState(blockPos.down());
            if (!blockState3.isOf((Block)this) && !blockState3.isOf(Blocks.END_STONE)) continue;
            return true;
        }
        return blockState.isOf((Block)this) || blockState.isOf(Blocks.END_STONE);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, UP, DOWN});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

