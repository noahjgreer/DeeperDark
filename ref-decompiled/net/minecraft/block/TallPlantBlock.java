/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.Block$SetBlockStateFlag
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TallPlantBlock
extends PlantBlock {
    public static final MapCodec<TallPlantBlock> CODEC = TallPlantBlock.createCodec(TallPlantBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public MapCodec<? extends TallPlantBlock> getCodec() {
        return CODEC;
    }

    public TallPlantBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HALF, (Comparable)DoubleBlockHalf.LOWER));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.get((Property)HALF);
        if (!(direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP) || neighborState.isOf((Block)this) && neighborState.get((Property)HALF) != doubleBlockHalf)) {
            return Blocks.AIR.getDefaultState();
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopYInclusive() && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            return super.getPlacementState(ctx);
        }
        return null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockPos blockPos = pos.up();
        world.setBlockState(blockPos, TallPlantBlock.withWaterloggedState((WorldView)world, (BlockPos)blockPos, (BlockState)((BlockState)this.getDefaultState().with((Property)HALF, (Comparable)DoubleBlockHalf.UPPER))), 3);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get((Property)HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState = world.getBlockState(pos.down());
            return blockState.isOf((Block)this) && blockState.get((Property)HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canPlaceAt(state, world, pos);
    }

    public static void placeAt(WorldAccess world, BlockState state, BlockPos pos, @Block.SetBlockStateFlag int flags) {
        BlockPos blockPos = pos.up();
        world.setBlockState(pos, TallPlantBlock.withWaterloggedState((WorldView)world, (BlockPos)pos, (BlockState)((BlockState)state.with((Property)HALF, (Comparable)DoubleBlockHalf.LOWER))), flags);
        world.setBlockState(blockPos, TallPlantBlock.withWaterloggedState((WorldView)world, (BlockPos)blockPos, (BlockState)((BlockState)state.with((Property)HALF, (Comparable)DoubleBlockHalf.UPPER))), flags);
    }

    public static BlockState withWaterloggedState(WorldView world, BlockPos pos, BlockState state) {
        if (state.contains((Property)Properties.WATERLOGGED)) {
            return (BlockState)state.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(world.isWater(pos)));
        }
        return state;
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient()) {
            if (player.shouldSkipBlockDrops()) {
                TallPlantBlock.onBreakInCreative((World)world, (BlockPos)pos, (BlockState)state, (PlayerEntity)player);
            } else {
                TallPlantBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos, null, (Entity)player, (ItemStack)player.getMainHandStack());
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, tool);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos blockPos;
        BlockState blockState;
        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.get((Property)HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER && (blockState = world.getBlockState(blockPos = pos.down())).isOf(state.getBlock()) && blockState.get((Property)HALF) == DoubleBlockHalf.LOWER) {
            BlockState blockState2 = blockState.getFluidState().isOf((Fluid)Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
            world.setBlockState(blockPos, blockState2, 35);
            world.syncWorldEvent((Entity)player, 2001, blockPos, Block.getRawIdFromState((BlockState)blockState));
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF});
    }

    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode((int)pos.getX(), (int)pos.down(state.get((Property)HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), (int)pos.getZ());
    }
}

