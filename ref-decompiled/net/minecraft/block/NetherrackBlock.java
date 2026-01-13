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
 *  net.minecraft.block.Fertilizable$FertilizableType
 *  net.minecraft.block.NetherrackBlock
 *  net.minecraft.registry.tag.BlockTags
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
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class NetherrackBlock
extends Block
implements Fertilizable {
    public static final MapCodec<NetherrackBlock> CODEC = NetherrackBlock.createCodec(NetherrackBlock::new);

    public MapCodec<NetherrackBlock> getCodec() {
        return CODEC;
    }

    public NetherrackBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        if (!world.getBlockState(pos.up()).isTransparent()) {
            return false;
        }
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos.add(-1, -1, -1), (BlockPos)pos.add(1, 1, 1))) {
            if (!world.getBlockState(blockPos).isIn(BlockTags.NYLIUM)) continue;
            return true;
        }
        return false;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        boolean bl = false;
        boolean bl2 = false;
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos.add(-1, -1, -1), (BlockPos)pos.add(1, 1, 1))) {
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
                bl2 = true;
            }
            if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
                bl = true;
            }
            if (!bl2 || !bl) continue;
            break;
        }
        if (bl2 && bl) {
            world.setBlockState(pos, random.nextBoolean() ? Blocks.WARPED_NYLIUM.getDefaultState() : Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
        } else if (bl2) {
            world.setBlockState(pos, Blocks.WARPED_NYLIUM.getDefaultState(), 3);
        } else if (bl) {
            world.setBlockState(pos, Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
        }
    }

    public Fertilizable.FertilizableType getFertilizableType() {
        return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
    }
}

