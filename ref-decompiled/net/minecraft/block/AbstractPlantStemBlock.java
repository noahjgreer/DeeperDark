/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantPartBlock
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public abstract class AbstractPlantStemBlock
extends AbstractPlantPartBlock
implements Fertilizable {
    public static final IntProperty AGE = Properties.AGE_25;
    public static final int MAX_AGE = 25;
    private final double growthChance;

    protected AbstractPlantStemBlock(AbstractBlock.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater, double growthChance) {
        super(settings, growthDirection, outlineShape, tickWater);
        this.growthChance = growthChance;
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected abstract MapCodec<? extends AbstractPlantStemBlock> getCodec();

    public BlockState getRandomGrowthState(Random random) {
        return (BlockState)this.getDefaultState().with((Property)AGE, (Comparable)Integer.valueOf(random.nextInt(25)));
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Integer)state.get((Property)AGE) < 25;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos;
        if ((Integer)state.get((Property)AGE) < 25 && random.nextDouble() < this.growthChance && this.chooseStemState(world.getBlockState(blockPos = pos.offset(this.growthDirection)))) {
            world.setBlockState(blockPos, this.age(state, world.random));
        }
    }

    protected BlockState age(BlockState state, Random random) {
        return (BlockState)state.cycle((Property)AGE);
    }

    public BlockState withMaxAge(BlockState state) {
        return (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(25));
    }

    public boolean hasMaxAge(BlockState state) {
        return (Integer)state.get((Property)AGE) == 25;
    }

    protected BlockState copyState(BlockState from, BlockState to) {
        return to;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == this.growthDirection.getOpposite()) {
            if (!state.canPlaceAt(world, pos)) {
                tickView.scheduleBlockTick(pos, (Block)this, 1);
            } else {
                BlockState blockState = world.getBlockState(pos.offset(this.growthDirection));
                if (blockState.isOf((Block)this) || blockState.isOf(this.getPlant())) {
                    return this.copyState(state, this.getPlant().getDefaultState());
                }
            }
        }
        if (direction == this.growthDirection && (neighborState.isOf((Block)this) || neighborState.isOf(this.getPlant()))) {
            return this.copyState(state, this.getPlant().getDefaultState());
        }
        if (this.tickWater) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return this.chooseStemState(world.getBlockState(pos.offset(this.growthDirection)));
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = pos.offset(this.growthDirection);
        int i = Math.min((Integer)state.get((Property)AGE) + 1, 25);
        int j = this.getGrowthLength(random);
        for (int k = 0; k < j && this.chooseStemState(world.getBlockState(blockPos)); ++k) {
            world.setBlockState(blockPos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(i)));
            blockPos = blockPos.offset(this.growthDirection);
            i = Math.min(i + 1, 25);
        }
    }

    protected abstract int getGrowthLength(Random var1);

    protected abstract boolean chooseStemState(BlockState var1);

    protected AbstractPlantStemBlock getStem() {
        return this;
    }
}

