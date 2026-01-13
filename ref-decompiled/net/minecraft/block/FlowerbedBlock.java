/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.FlowerbedBlock
 *  net.minecraft.block.PlantBlock
 *  net.minecraft.block.Segmented
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.Segmented;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public class FlowerbedBlock
extends PlantBlock
implements Fertilizable,
Segmented {
    public static final MapCodec<FlowerbedBlock> CODEC = FlowerbedBlock.createCodec(FlowerbedBlock::new);
    public static final EnumProperty<Direction> HORIZONTAL_FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty FLOWER_AMOUNT = Properties.FLOWER_AMOUNT;
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<FlowerbedBlock> getCodec() {
        return CODEC;
    }

    public FlowerbedBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)HORIZONTAL_FACING, (Comparable)Direction.NORTH)).with((Property)FLOWER_AMOUNT, (Comparable)Integer.valueOf(1)));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        return this.createShapeFunction(this.createShapeFunction(HORIZONTAL_FACING, FLOWER_AMOUNT));
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)HORIZONTAL_FACING, (Comparable)rotation.rotate((Direction)state.get((Property)HORIZONTAL_FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)HORIZONTAL_FACING)));
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (this.shouldAddSegment(state, context, FLOWER_AMOUNT)) {
            return true;
        }
        return super.canReplace(state, context);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public double getHeight() {
        return 3.0;
    }

    public IntProperty getAmountProperty() {
        return FLOWER_AMOUNT;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx, (Block)this, FLOWER_AMOUNT, HORIZONTAL_FACING);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HORIZONTAL_FACING, FLOWER_AMOUNT});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = (Integer)state.get((Property)FLOWER_AMOUNT);
        if (i < 4) {
            world.setBlockState(pos, (BlockState)state.with((Property)FLOWER_AMOUNT, (Comparable)Integer.valueOf(i + 1)), 2);
        } else {
            FlowerbedBlock.dropStack((World)world, (BlockPos)pos, (ItemStack)new ItemStack((ItemConvertible)this));
        }
    }
}

