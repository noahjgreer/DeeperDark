/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LeafLitterBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.Segmented
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.Segmented;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class LeafLitterBlock
extends PlantBlock
implements Segmented {
    public static final MapCodec<LeafLitterBlock> CODEC = LeafLitterBlock.createCodec(LeafLitterBlock::new);
    public static final EnumProperty<Direction> HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
    private final Function<BlockState, VoxelShape> shapeFunction;

    public LeafLitterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HORIZONTAL_FACING, (Comparable)Direction.NORTH)).with((Property)this.getAmountProperty(), (Comparable)Integer.valueOf(1)));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        return this.createShapeFunction(this.createShapeFunction(HORIZONTAL_FACING, this.getAmountProperty()));
    }

    protected MapCodec<LeafLitterBlock> getCodec() {
        return CODEC;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)HORIZONTAL_FACING, (Comparable)rotation.rotate((Direction)state.get((Property)HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)HORIZONTAL_FACING)));
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (this.shouldAddSegment(state, context, this.getAmountProperty())) {
            return true;
        }
        return super.canReplace(state, context);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        return world.getBlockState(blockPos).isSideSolidFullSquare((BlockView)world, blockPos, Direction.UP);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx, (Block)this, this.getAmountProperty(), HORIZONTAL_FACING);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HORIZONTAL_FACING, this.getAmountProperty()});
    }
}

