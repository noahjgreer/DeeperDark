/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantBlock
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.block.KelpPlantBlock
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

public class KelpPlantBlock
extends AbstractPlantBlock
implements FluidFillable {
    public static final MapCodec<KelpPlantBlock> CODEC = KelpPlantBlock.createCodec(KelpPlantBlock::new);

    public MapCodec<KelpPlantBlock> getCodec() {
        return CODEC;
    }

    public KelpPlantBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.UP, VoxelShapes.fullCube(), true);
    }

    protected AbstractPlantStemBlock getStem() {
        return (AbstractPlantStemBlock)Blocks.KELP;
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    protected boolean canAttachTo(BlockState state) {
        return this.getStem().canAttachTo(state);
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}

