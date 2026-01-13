/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.PropaguleBlock
 *  net.minecraft.block.SaplingBlock
 *  net.minecraft.block.SaplingGenerator
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.SaplingGenerator;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
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
public class PropaguleBlock
extends SaplingBlock
implements Waterloggable {
    public static final MapCodec<PropaguleBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SaplingGenerator.CODEC.fieldOf("tree").forGetter(block -> block.generator), (App)PropaguleBlock.createSettingsCodec()).apply((Applicative)instance, PropaguleBlock::new));
    public static final IntProperty AGE = Properties.AGE_4;
    public static final int field_37589 = 4;
    private static final int[] MIN_Y_BY_AGE = new int[]{13, 10, 7, 3, 0};
    private static final VoxelShape[] SHAPES_BY_AGE = Block.createShapeArray((int)4, age -> Block.createColumnShape((double)2.0, (double)MIN_Y_BY_AGE[age], (double)16.0));
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty HANGING = Properties.HANGING;

    public MapCodec<PropaguleBlock> getCodec() {
        return CODEC;
    }

    public PropaguleBlock(SaplingGenerator saplingGenerator, AbstractBlock.Settings settings) {
        super(saplingGenerator, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)STAGE, (Comparable)Integer.valueOf(0))).with((Property)AGE, (Comparable)Integer.valueOf(0))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)HANGING, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STAGE}).add(new Property[]{AGE}).add(new Property[]{WATERLOGGED}).add(new Property[]{HANGING});
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.CLAY);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)super.getPlacementState(ctx).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl))).with((Property)AGE, (Comparable)Integer.valueOf(4));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int i = (Boolean)state.get((Property)HANGING) != false ? (Integer)state.get((Property)AGE) : 4;
        return SHAPES_BY_AGE[i].offset(state.getModelOffset(pos));
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (PropaguleBlock.isHanging((BlockState)state)) {
            return world.getBlockState(pos.up()).isOf(Blocks.MANGROVE_LEAVES);
        }
        return super.canPlaceAt(state, world, pos);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!PropaguleBlock.isHanging((BlockState)state)) {
            if (random.nextInt(7) == 0) {
                this.generate(world, pos, state, random);
            }
            return;
        }
        if (!PropaguleBlock.isFullyGrown((BlockState)state)) {
            world.setBlockState(pos, (BlockState)state.cycle((Property)AGE), 2);
        }
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return !PropaguleBlock.isHanging((BlockState)state) || !PropaguleBlock.isFullyGrown((BlockState)state);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return PropaguleBlock.isHanging((BlockState)state) ? !PropaguleBlock.isFullyGrown((BlockState)state) : super.canGrow(world, random, pos, state);
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (PropaguleBlock.isHanging((BlockState)state) && !PropaguleBlock.isFullyGrown((BlockState)state)) {
            world.setBlockState(pos, (BlockState)state.cycle((Property)AGE), 2);
        } else {
            super.grow(world, random, pos, state);
        }
    }

    private static boolean isHanging(BlockState state) {
        return (Boolean)state.get((Property)HANGING);
    }

    private static boolean isFullyGrown(BlockState state) {
        return (Integer)state.get((Property)AGE) == 4;
    }

    public static BlockState getDefaultHangingState() {
        return PropaguleBlock.getHangingState((int)0);
    }

    public static BlockState getHangingState(int age) {
        return (BlockState)((BlockState)Blocks.MANGROVE_PROPAGULE.getDefaultState().with((Property)HANGING, (Comparable)Boolean.valueOf(true))).with((Property)AGE, (Comparable)Integer.valueOf(age));
    }
}

