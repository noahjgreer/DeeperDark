/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCoralBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CoralBlock
 *  net.minecraft.block.CoralBlockBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCoralBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CoralBlockBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
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
public class CoralBlock
extends AbstractCoralBlock {
    public static final MapCodec<CoralBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)CoralBlockBlock.DEAD_FIELD.forGetter(block -> block.deadCoralBlock), (App)CoralBlock.createSettingsCodec()).apply((Applicative)instance, CoralBlock::new));
    private final Block deadCoralBlock;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)15.0);

    public MapCodec<CoralBlock> getCodec() {
        return CODEC;
    }

    public CoralBlock(Block deadCoralBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.deadCoralBlock = deadCoralBlock;
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.checkLivingConditions(state, (BlockView)world, (ScheduledTickView)world, world.random, pos);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!CoralBlock.isInWater((BlockState)state, (BlockView)world, (BlockPos)pos)) {
            world.setBlockState(pos, (BlockState)this.deadCoralBlock.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)), 2);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        this.checkLivingConditions(state, (BlockView)world, tickView, random, pos);
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

