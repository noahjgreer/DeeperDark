/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.SnowBlock
 *  net.minecraft.block.SnowyBlock
 *  net.minecraft.block.SpreadableBlock
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.chunk.light.ChunkLightProvider
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class SpreadableBlock
extends SnowyBlock {
    protected SpreadableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    private static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && (Integer)blockState.get((Property)SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (blockState.getFluidState().getLevel() == 8) {
            return false;
        }
        int i = ChunkLightProvider.getRealisticOpacity((BlockState)state, (BlockState)blockState, (Direction)Direction.UP, (int)blockState.getOpacity());
        return i < 15;
    }

    protected abstract MapCodec<? extends SpreadableBlock> getCodec();

    private static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return SpreadableBlock.canSurvive((BlockState)state, (WorldView)world, (BlockPos)pos) && !world.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!SpreadableBlock.canSurvive((BlockState)state, (WorldView)world, (BlockPos)pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            return;
        }
        if (world.getLightLevel(pos.up()) >= 9) {
            BlockState blockState = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                if (!world.getBlockState(blockPos).isOf(Blocks.DIRT) || !SpreadableBlock.canSpread((BlockState)blockState, (WorldView)world, (BlockPos)blockPos)) continue;
                world.setBlockState(blockPos, (BlockState)blockState.with((Property)SNOWY, (Comparable)Boolean.valueOf(SpreadableBlock.isSnow((BlockState)world.getBlockState(blockPos.up())))));
            }
        }
    }
}

