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
 *  net.minecraft.block.TwistingVinesBlock
 *  net.minecraft.block.VineLogic
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

public class TwistingVinesBlock
extends AbstractPlantStemBlock {
    public static final MapCodec<TwistingVinesBlock> CODEC = TwistingVinesBlock.createCodec(TwistingVinesBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)8.0, (double)0.0, (double)15.0);

    public MapCodec<TwistingVinesBlock> getCodec() {
        return CODEC;
    }

    public TwistingVinesBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.UP, SHAPE, false, 0.1);
    }

    protected int getGrowthLength(Random random) {
        return VineLogic.getGrowthLength((Random)random);
    }

    protected Block getPlant() {
        return Blocks.TWISTING_VINES_PLANT;
    }

    protected boolean chooseStemState(BlockState state) {
        return VineLogic.isValidForWeepingStem((BlockState)state);
    }
}

