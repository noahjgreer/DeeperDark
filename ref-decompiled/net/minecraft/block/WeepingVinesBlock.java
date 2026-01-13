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
 *  net.minecraft.block.VineLogic
 *  net.minecraft.block.WeepingVinesBlock
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineLogic;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesBlock
extends AbstractPlantStemBlock {
    public static final MapCodec<WeepingVinesBlock> CODEC = WeepingVinesBlock.createCodec(WeepingVinesBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)8.0, (double)9.0, (double)16.0);

    public MapCodec<WeepingVinesBlock> getCodec() {
        return CODEC;
    }

    public WeepingVinesBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.DOWN, SHAPE, false, 0.1);
    }

    protected int getGrowthLength(Random random) {
        return VineLogic.getGrowthLength((Random)random);
    }

    protected Block getPlant() {
        return Blocks.WEEPING_VINES_PLANT;
    }

    protected boolean chooseStemState(BlockState state) {
        return VineLogic.isValidForWeepingStem((BlockState)state);
    }
}

