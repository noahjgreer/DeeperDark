/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.EndPortalFrameBlock
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.pattern.BlockPattern
 *  net.minecraft.block.pattern.BlockPatternBuilder
 *  net.minecraft.block.pattern.CachedBlockPosition
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.predicate.block.BlockStatePredicate
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.base.Predicates;
import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class EndPortalFrameBlock
extends Block {
    public static final MapCodec<EndPortalFrameBlock> CODEC = EndPortalFrameBlock.createCodec(EndPortalFrameBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty EYE = Properties.EYE;
    private static final VoxelShape FRAME_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)13.0);
    private static final VoxelShape FRAME_WITH_EYE_SHAPE = VoxelShapes.union((VoxelShape)FRAME_SHAPE, (VoxelShape)Block.createColumnShape((double)8.0, (double)13.0, (double)16.0));
    private static @Nullable BlockPattern completedFrame;

    public MapCodec<EndPortalFrameBlock> getCodec() {
        return CODEC;
    }

    public EndPortalFrameBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)EYE, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get((Property)EYE) != false ? FRAME_WITH_EYE_SHAPE : FRAME_SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite())).with((Property)EYE, (Comparable)Boolean.valueOf(false));
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)EYE)).booleanValue()) {
            return 15;
        }
        return 0;
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, EYE});
    }

    public static BlockPattern getCompletedFramePattern() {
        if (completedFrame == null) {
            completedFrame = BlockPatternBuilder.start().aisle(new String[]{"?vvv?", ">???<", ">???<", ">???<", "?^^^?"}).where('?', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.ANY)).where('^', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.forBlock((Block)Blocks.END_PORTAL_FRAME).with((Property)EYE, (Predicate)Predicates.equalTo((Object)true)).with((Property)FACING, (Predicate)Predicates.equalTo((Object)Direction.SOUTH)))).where('>', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.forBlock((Block)Blocks.END_PORTAL_FRAME).with((Property)EYE, (Predicate)Predicates.equalTo((Object)true)).with((Property)FACING, (Predicate)Predicates.equalTo((Object)Direction.WEST)))).where('v', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.forBlock((Block)Blocks.END_PORTAL_FRAME).with((Property)EYE, (Predicate)Predicates.equalTo((Object)true)).with((Property)FACING, (Predicate)Predicates.equalTo((Object)Direction.NORTH)))).where('<', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.forBlock((Block)Blocks.END_PORTAL_FRAME).with((Property)EYE, (Predicate)Predicates.equalTo((Object)true)).with((Property)FACING, (Predicate)Predicates.equalTo((Object)Direction.EAST)))).build();
        }
        return completedFrame;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

