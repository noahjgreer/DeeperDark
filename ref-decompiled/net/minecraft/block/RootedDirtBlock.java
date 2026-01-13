/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.RootedDirtBlock
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RootedDirtBlock
extends Block
implements Fertilizable {
    public static final MapCodec<RootedDirtBlock> CODEC = RootedDirtBlock.createCodec(RootedDirtBlock::new);

    public MapCodec<RootedDirtBlock> getCodec() {
        return CODEC;
    }

    public RootedDirtBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.down()).isAir();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.setBlockState(pos.down(), Blocks.HANGING_ROOTS.getDefaultState());
    }

    public BlockPos getFertilizeParticlePos(BlockPos pos) {
        return pos.down();
    }
}

