/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FluidFillable
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.TallSeagrassBlock
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

public class TallSeagrassBlock
extends TallPlantBlock
implements FluidFillable {
    public static final MapCodec<TallSeagrassBlock> CODEC = TallSeagrassBlock.createCodec(TallSeagrassBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = TallPlantBlock.HALF;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)16.0);

    public MapCodec<TallSeagrassBlock> getCodec() {
        return CODEC;
    }

    public TallSeagrassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) && !floor.isOf(Blocks.MAGMA_BLOCK);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)Blocks.SEAGRASS);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState;
        BlockState blockState = super.getPlacementState(ctx);
        if (blockState != null && (fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos().up())).isIn(FluidTags.WATER) && fluidState.getLevel() == 8) {
            return blockState;
        }
        return null;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get((Property)HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState = world.getBlockState(pos.down());
            return blockState.isOf((Block)this) && blockState.get((Property)HALF) == DoubleBlockHalf.LOWER;
        }
        FluidState fluidState = world.getFluidState(pos);
        return super.canPlaceAt(state, world, pos) && fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}

