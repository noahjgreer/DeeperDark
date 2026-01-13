/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.WallPiglinHeadBlock
 *  net.minecraft.block.WallSkullBlock
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class WallPiglinHeadBlock
extends WallSkullBlock {
    public static final MapCodec<WallPiglinHeadBlock> CODEC = WallPiglinHeadBlock.createCodec(WallPiglinHeadBlock::new);
    private static final Map<Direction, VoxelShape> SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)10.0, (double)8.0, (double)8.0, (double)16.0));

    public MapCodec<WallPiglinHeadBlock> getCodec() {
        return CODEC;
    }

    public WallPiglinHeadBlock(AbstractBlock.Settings settings) {
        super((SkullBlock.SkullType)SkullBlock.Type.PIGLIN, settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES.get(state.get((Property)FACING));
    }
}

