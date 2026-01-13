/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DryVegetationBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.sound.AmbientDesertBlockSounds
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DryVegetationBlock
extends PlantBlock {
    public static final MapCodec<DryVegetationBlock> CODEC = DryVegetationBlock.createCodec(DryVegetationBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)13.0);

    public MapCodec<? extends DryVegetationBlock> getCodec() {
        return CODEC;
    }

    public DryVegetationBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.DRY_VEGETATION_MAY_PLACE_ON);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        AmbientDesertBlockSounds.tryPlayDeadBushSounds((World)world, (BlockPos)pos, (Random)random);
    }
}

