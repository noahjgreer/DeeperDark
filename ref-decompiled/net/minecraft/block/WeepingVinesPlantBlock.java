/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPlantBlock
 *  net.minecraft.block.AbstractPlantStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.WeepingVinesPlantBlock
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPlantBlock;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public class WeepingVinesPlantBlock
extends AbstractPlantBlock {
    public static final MapCodec<WeepingVinesPlantBlock> CODEC = WeepingVinesPlantBlock.createCodec(WeepingVinesPlantBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);

    public MapCodec<WeepingVinesPlantBlock> getCodec() {
        return CODEC;
    }

    public WeepingVinesPlantBlock(AbstractBlock.Settings settings) {
        super(settings, Direction.DOWN, SHAPE, false);
    }

    protected AbstractPlantStemBlock getStem() {
        return (AbstractPlantStemBlock)Blocks.WEEPING_VINES;
    }
}

