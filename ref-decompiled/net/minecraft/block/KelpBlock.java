/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.block.KelpBlock
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

public class KelpBlock
extends AbstractPlantStemBlock
implements FluidFillable {
    public static final MapCodec<KelpBlock> CODEC = KelpBlock.createCodec(KelpBlock::new);
    private static final double GROWTH_CHANCE = 0.14;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)9.0);

    public MapCodec<KelpBlock> getCodec() {
        return CODEC;
    }

    public KelpBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.UP, SHAPE, true, 0.14);
    }

    protected boolean chooseStemState(BlockState state) {
        return state.isOf(Blocks.WATER);
    }

    protected Block getPlant() {
        return Blocks.KELP_PLANT;
    }

    protected boolean canAttachTo(BlockState state) {
        return !state.isOf(Blocks.MAGMA_BLOCK);
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }

    protected int getGrowthLength(Random random) {
        return 1;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8) {
            return super.getPlacementState(ctx);
        }
        return null;
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }
}

