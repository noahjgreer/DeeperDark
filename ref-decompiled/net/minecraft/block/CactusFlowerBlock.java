/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CactusFlowerBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SideShapeType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CactusFlowerBlock
extends PlantBlock {
    public static final MapCodec<CactusFlowerBlock> CODEC = CactusFlowerBlock.createCodec(CactusFlowerBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)12.0);

    public MapCodec<? extends CactusFlowerBlock> getCodec() {
        return CODEC;
    }

    public CactusFlowerBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isOf(Blocks.CACTUS) || blockState.isOf(Blocks.FARMLAND) || blockState.isSideSolid(world, pos, Direction.UP, SideShapeType.CENTER);
    }
}

