/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCauldronBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.PointedDripstoneBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.cauldron.CauldronBehavior
 *  net.minecraft.block.cauldron.CauldronBehavior$CauldronBehaviorMap
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Util
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractCauldronBlock
extends Block {
    protected static final int field_30988 = 4;
    private static final VoxelShape RAYCAST_SHAPE = Block.createColumnShape((double)12.0, (double)4.0, (double)16.0);
    protected static final VoxelShape OUTLINE_SHAPE = (VoxelShape)Util.make(() -> {
        int i = 4;
        int j = 3;
        int k = 2;
        return VoxelShapes.combineAndSimplify((VoxelShape)VoxelShapes.fullCube(), (VoxelShape)VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)8.0, (double)0.0, (double)3.0), (VoxelShape[])new VoxelShape[]{Block.createColumnShape((double)8.0, (double)16.0, (double)0.0, (double)3.0), Block.createColumnShape((double)12.0, (double)0.0, (double)3.0), RAYCAST_SHAPE}), (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST);
    });
    protected final CauldronBehavior.CauldronBehaviorMap behaviorMap;

    protected abstract MapCodec<? extends AbstractCauldronBlock> getCodec();

    public AbstractCauldronBlock(AbstractBlock.Settings settings, CauldronBehavior.CauldronBehaviorMap behaviorMap) {
        super(settings);
        this.behaviorMap = behaviorMap;
    }

    protected double getFluidHeight(BlockState state) {
        return 0.0;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        CauldronBehavior cauldronBehavior = (CauldronBehavior)this.behaviorMap.map().get(stack.getItem());
        return cauldronBehavior.interact(state, world, pos, player, hand, stack);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public abstract boolean isFull(BlockState var1);

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos = PointedDripstoneBlock.getDripPos((World)world, (BlockPos)pos);
        if (blockPos == null) {
            return;
        }
        Fluid fluid = PointedDripstoneBlock.getDripFluid((ServerWorld)world, (BlockPos)blockPos);
        if (fluid != Fluids.EMPTY && this.canBeFilledByDripstone(fluid)) {
            this.fillFromDripstone(state, (World)world, pos, fluid);
        }
    }

    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return false;
    }

    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
    }
}

