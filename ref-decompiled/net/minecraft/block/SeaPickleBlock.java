/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.SeaPickleBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
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
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class SeaPickleBlock
extends PlantBlock
implements Fertilizable,
Waterloggable {
    public static final MapCodec<SeaPickleBlock> CODEC = SeaPickleBlock.createCodec(SeaPickleBlock::new);
    public static final int MAX_PICKLES = 4;
    public static final IntProperty PICKLES = Properties.PICKLES;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape ONE_PICKLE_SHAPE = Block.createColumnShape((double)4.0, (double)0.0, (double)6.0);
    private static final VoxelShape TWO_PICKLES_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)6.0);
    private static final VoxelShape THREE_PICKLES_SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)6.0);
    private static final VoxelShape FOUR_PICKLES_SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)7.0);

    public MapCodec<SeaPickleBlock> getCodec() {
        return CODEC;
    }

    public SeaPickleBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)PICKLES, (Comparable)Integer.valueOf(1))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(true)));
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block)this)) {
            return (BlockState)blockState.with((Property)PICKLES, (Comparable)Integer.valueOf(Math.min(4, (Integer)blockState.get((Property)PICKLES) + 1)));
        }
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)super.getPlacementState(ctx).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl));
    }

    public static boolean isDry(BlockState state) {
        return (Boolean)state.get((Property)WATERLOGGED) == false;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return !floor.getCollisionShape(world, pos).getFace(Direction.UP).isEmpty() || floor.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return this.canPlantOnTop(world.getBlockState(blockPos), (BlockView)world, blockPos);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && (Integer)state.get((Property)PICKLES) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch ((Integer)state.get((Property)PICKLES)) {
            default -> ONE_PICKLE_SHAPE;
            case 2 -> TWO_PICKLES_SHAPE;
            case 3 -> THREE_PICKLES_SHAPE;
            case 4 -> FOUR_PICKLES_SHAPE;
        };
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{PICKLES, WATERLOGGED});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !SeaPickleBlock.isDry((BlockState)state) && world.getBlockState(pos.down()).isIn(BlockTags.CORAL_BLOCKS);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = 5;
        int j = 1;
        int k = 2;
        int l = 0;
        int m = pos.getX() - 2;
        int n = 0;
        for (int o = 0; o < 5; ++o) {
            for (int p = 0; p < j; ++p) {
                int q = 2 + pos.getY() - 1;
                for (int r = q - 2; r < q; ++r) {
                    BlockState blockState;
                    BlockPos blockPos = new BlockPos(m + o, r, pos.getZ() - n + p);
                    if (blockPos.equals((Object)pos) || random.nextInt(6) != 0 || !world.getBlockState(blockPos).isOf(Blocks.WATER) || !(blockState = world.getBlockState(blockPos.down())).isIn(BlockTags.CORAL_BLOCKS)) continue;
                    world.setBlockState(blockPos, (BlockState)Blocks.SEA_PICKLE.getDefaultState().with((Property)PICKLES, (Comparable)Integer.valueOf(random.nextInt(4) + 1)), 3);
                }
            }
            if (l < 2) {
                j += 2;
                ++n;
            } else {
                j -= 2;
                --n;
            }
            ++l;
        }
        world.setBlockState(pos, (BlockState)state.with((Property)PICKLES, (Comparable)Integer.valueOf(4)), 2);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

