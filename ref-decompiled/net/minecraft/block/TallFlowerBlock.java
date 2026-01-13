/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.TallFlowerBlock
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public class TallFlowerBlock
extends TallPlantBlock
implements Fertilizable {
    public static final MapCodec<TallFlowerBlock> CODEC = TallFlowerBlock.createCodec(TallFlowerBlock::new);

    public MapCodec<TallFlowerBlock> getCodec() {
        return CODEC;
    }

    public TallFlowerBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        TallFlowerBlock.dropStack((World)world, (BlockPos)pos, (ItemStack)new ItemStack((ItemConvertible)this));
    }
}

