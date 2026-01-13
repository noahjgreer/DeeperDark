/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantBlock
 *  net.minecraft.block.AbstractPlantPartBlock
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockLocating
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantPartBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public abstract class AbstractPlantBlock
extends AbstractPlantPartBlock
implements Fertilizable {
    protected AbstractPlantBlock(AbstractBlock.Settings settings, Direction direction, VoxelShape voxelShape, boolean bl) {
        super(settings, direction, voxelShape, bl);
    }

    protected abstract MapCodec<? extends AbstractPlantBlock> getCodec();

    protected BlockState copyState(BlockState from, BlockState to) {
        return to;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == this.growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        AbstractPlantStemBlock abstractPlantStemBlock = this.getStem();
        if (direction == this.growthDirection && !neighborState.isOf((Block)this) && !neighborState.isOf((Block)abstractPlantStemBlock)) {
            return this.copyState(state, abstractPlantStemBlock.getRandomGrowthState(random));
        }
        if (this.tickWater) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)this.getStem());
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        Optional optional = this.getStemHeadPos((BlockView)world, pos, state.getBlock());
        return optional.isPresent() && this.getStem().chooseStemState(world.getBlockState(((BlockPos)optional.get()).offset(this.growthDirection)));
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Optional optional = this.getStemHeadPos((BlockView)world, pos, state.getBlock());
        if (optional.isPresent()) {
            BlockState blockState = world.getBlockState((BlockPos)optional.get());
            ((AbstractPlantStemBlock)blockState.getBlock()).grow(world, random, (BlockPos)optional.get(), blockState);
        }
    }

    private Optional<BlockPos> getStemHeadPos(BlockView world, BlockPos pos, Block block) {
        return BlockLocating.findColumnEnd((BlockView)world, (BlockPos)pos, (Block)block, (Direction)this.growthDirection, (Block)this.getStem());
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        boolean bl = super.canReplace(state, context);
        if (bl && context.getStack().isOf(this.getStem().asItem())) {
            return false;
        }
        return bl;
    }

    protected Block getPlant() {
        return this;
    }
}

