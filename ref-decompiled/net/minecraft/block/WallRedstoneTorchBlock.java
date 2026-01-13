/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.RedstoneTorchBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallRedstoneTorchBlock
 *  net.minecraft.block.WallTorchBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
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
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
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
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class WallRedstoneTorchBlock
extends RedstoneTorchBlock {
    public static final MapCodec<WallRedstoneTorchBlock> CODEC = WallRedstoneTorchBlock.createCodec(WallRedstoneTorchBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public MapCodec<WallRedstoneTorchBlock> getCodec() {
        return CODEC;
    }

    public WallRedstoneTorchBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)LIT, (Comparable)Boolean.valueOf(true)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WallTorchBlock.getBoundingShape((BlockState)state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return WallTorchBlock.canPlaceAt((WorldView)world, (BlockPos)pos, (Direction)((Direction)state.get((Property)FACING)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction.getOpposite() == state.get((Property)FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = Blocks.WALL_TORCH.getPlacementState(ctx);
        return blockState == null ? null : (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)((Direction)blockState.get((Property)FACING)));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)LIT)).booleanValue()) {
            return;
        }
        Direction direction = ((Direction)state.get((Property)FACING)).getOpposite();
        double d = 0.27;
        double e = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetX();
        double f = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2 + 0.22;
        double g = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2 + 0.27 * (double)direction.getOffsetZ();
        world.addParticleClient((ParticleEffect)DustParticleEffect.DEFAULT, e, f, g, 0.0, 0.0, 0.0);
    }

    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        Direction direction = ((Direction)state.get((Property)FACING)).getOpposite();
        return world.isEmittingRedstonePower(pos.offset(direction), direction);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)LIT)).booleanValue() && state.get((Property)FACING) != direction) {
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
        builder.add(new Property[]{FACING, LIT});
    }

    protected @Nullable WireOrientation getEmissionOrientation(World world, BlockState state) {
        return OrientationHelper.getEmissionOrientation((World)world, (Direction)((Direction)state.get((Property)FACING)).getOpposite(), (Direction)Direction.UP);
    }
}

