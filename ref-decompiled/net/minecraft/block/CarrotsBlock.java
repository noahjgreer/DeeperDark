/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CarrotsBlock
 *  net.minecraft.block.CropBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.Items
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CarrotsBlock
extends CropBlock {
    public static final MapCodec<CarrotsBlock> CODEC = CarrotsBlock.createCodec(CarrotsBlock::new);
    private static final VoxelShape[] SHAPES_BY_AGE = Block.createShapeArray((int)7, age -> Block.createColumnShape((double)16.0, (double)0.0, (double)(2 + age)));

    public MapCodec<CarrotsBlock> getCodec() {
        return CODEC;
    }

    public CarrotsBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected ItemConvertible getSeedsItem() {
        return Items.CARROT;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_AGE[this.getAge(state)];
    }
}

