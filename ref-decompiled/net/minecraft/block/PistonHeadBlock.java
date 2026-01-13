/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FacingBlock
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.block.PistonHeadBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class PistonHeadBlock
extends FacingBlock {
    public static final MapCodec<PistonHeadBlock> CODEC = PistonHeadBlock.createCodec(PistonHeadBlock::new);
    public static final EnumProperty<PistonType> TYPE = Properties.PISTON_TYPE;
    public static final BooleanProperty SHORT = Properties.SHORT;
    public static final int field_55825 = 4;
    private static final VoxelShape BASE_SHAPE = Block.createCuboidZShape((double)16.0, (double)0.0, (double)4.0);
    private static final Map<Direction, VoxelShape> SHORT_SHAPES = VoxelShapes.createFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)BASE_SHAPE, (VoxelShape)Block.createCuboidZShape((double)4.0, (double)4.0, (double)16.0)));
    private static final Map<Direction, VoxelShape> LONG_SHAPES = VoxelShapes.createFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)BASE_SHAPE, (VoxelShape)Block.createCuboidZShape((double)4.0, (double)4.0, (double)20.0)));

    protected MapCodec<PistonHeadBlock> getCodec() {
        return CODEC;
    }

    public PistonHeadBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)TYPE, (Comparable)PistonType.DEFAULT)).with((Property)SHORT, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)((Boolean)state.get((Property)SHORT) != false ? SHORT_SHAPES : LONG_SHAPES).get(state.get((Property)FACING));
    }

    private boolean isAttached(BlockState headState, BlockState pistonState) {
        Block block = headState.get((Property)TYPE) == PistonType.DEFAULT ? Blocks.PISTON : Blocks.STICKY_PISTON;
        return pistonState.isOf(block) && (Boolean)pistonState.get((Property)PistonBlock.EXTENDED) != false && pistonState.get((Property)FACING) == headState.get((Property)FACING);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos blockPos;
        if (!world.isClient() && player.shouldSkipBlockDrops() && this.isAttached(state, world.getBlockState(blockPos = pos.offset(((Direction)state.get((Property)FACING)).getOpposite())))) {
            world.breakBlock(blockPos, false);
        }
        return super.onBreak(world, pos, state, player);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        BlockPos blockPos = pos.offset(((Direction)state.get((Property)FACING)).getOpposite());
        if (this.isAttached(state, world.getBlockState(blockPos))) {
            world.breakBlock(blockPos, true);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getOpposite() == state.get((Property)FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.offset(((Direction)state.get((Property)FACING)).getOpposite()));
        return this.isAttached(state, blockState) || blockState.isOf(Blocks.MOVING_PISTON) && blockState.get((Property)FACING) == state.get((Property)FACING);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (state.canPlaceAt((WorldView)world, pos)) {
            world.updateNeighbor(pos.offset(((Direction)state.get((Property)FACING)).getOpposite()), sourceBlock, OrientationHelper.withFrontNullable((WireOrientation)wireOrientation, (Direction)((Direction)state.get((Property)FACING)).getOpposite()));
        }
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack((ItemConvertible)(state.get((Property)TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, TYPE, SHORT});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

