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
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

public abstract class AbstractPlantPartBlock
extends Block {
    protected final Direction growthDirection;
    protected final boolean tickWater;
    protected final VoxelShape outlineShape;

    protected AbstractPlantPartBlock(AbstractBlock.Settings settings, Direction growthDirection, VoxelShape outlineShape, boolean tickWater) {
        super(settings);
        this.growthDirection = growthDirection;
        this.outlineShape = outlineShape;
        this.tickWater = tickWater;
    }

    protected abstract MapCodec<? extends AbstractPlantPartBlock> getCodec();

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(this.growthDirection));
        if (blockState.isOf((Block)this.getStem()) || blockState.isOf(this.getPlant())) {
            return this.getPlant().getDefaultState();
        }
        return this.getRandomGrowthState(ctx.getWorld().random);
    }

    public BlockState getRandomGrowthState(Random random) {
        return this.getDefaultState();
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.offset(this.growthDirection.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        if (!this.canAttachTo(blockState)) {
            return false;
        }
        return blockState.isOf((Block)this.getStem()) || blockState.isOf(this.getPlant()) || blockState.isSideSolidFullSquare((BlockView)world, blockPos, this.growthDirection);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected boolean canAttachTo(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.outlineShape;
    }

    protected abstract AbstractPlantStemBlock getStem();

    protected abstract Block getPlant();
}

