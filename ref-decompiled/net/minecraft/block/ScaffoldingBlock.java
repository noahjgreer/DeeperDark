/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ScaffoldingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class ScaffoldingBlock
extends Block
implements Waterloggable {
    public static final MapCodec<ScaffoldingBlock> CODEC = ScaffoldingBlock.createCodec(ScaffoldingBlock::new);
    private static final int field_31238 = 1;
    private static final VoxelShape NORMAL_OUTLINE_SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)14.0, (double)16.0), (VoxelShape)VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidShape((double)0.0, (double)0.0, (double)0.0, (double)2.0, (double)16.0, (double)2.0)).values().stream().reduce(VoxelShapes.empty(), VoxelShapes::union));
    private static final VoxelShape COLLISION_SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)2.0);
    private static final VoxelShape BOTTOM_OUTLINE_SHAPE = VoxelShapes.union((VoxelShape)NORMAL_OUTLINE_SHAPE, (VoxelShape[])new VoxelShape[]{COLLISION_SHAPE, VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)0.0, (double)2.0, (double)0.0, (double)2.0)).values().stream().reduce(VoxelShapes.empty(), VoxelShapes::union)});
    private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.fullCube().offset(0.0, -1.0, 0.0).simplify();
    public static final int MAX_DISTANCE = 7;
    public static final IntProperty DISTANCE = Properties.DISTANCE_0_7;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty BOTTOM = Properties.BOTTOM;

    public MapCodec<ScaffoldingBlock> getCodec() {
        return CODEC;
    }

    public ScaffoldingBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)DISTANCE, (Comparable)Integer.valueOf(7))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)BOTTOM, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{DISTANCE, WATERLOGGED, BOTTOM});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!context.isHolding(state.getBlock().asItem())) {
            return (Boolean)state.get((Property)BOTTOM) != false ? BOTTOM_OUTLINE_SHAPE : NORMAL_OUTLINE_SHAPE;
        }
        return VoxelShapes.fullCube();
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return context.getStack().isOf(this.asItem());
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        int i = ScaffoldingBlock.calculateDistance((BlockView)world, (BlockPos)blockPos);
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(world.getFluidState(blockPos).getFluid() == Fluids.WATER))).with((Property)DISTANCE, (Comparable)Integer.valueOf(i))).with((Property)BOTTOM, (Comparable)Boolean.valueOf(this.shouldBeBottom((BlockView)world, blockPos, i)));
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, (Block)this, 1);
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (!world.isClient()) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return state;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = ScaffoldingBlock.calculateDistance((BlockView)world, (BlockPos)pos);
        BlockState blockState = (BlockState)((BlockState)state.with((Property)DISTANCE, (Comparable)Integer.valueOf(i))).with((Property)BOTTOM, (Comparable)Boolean.valueOf(this.shouldBeBottom((BlockView)world, pos, i)));
        if ((Integer)blockState.get((Property)DISTANCE) == 7) {
            if ((Integer)state.get((Property)DISTANCE) == 7) {
                FallingBlockEntity.spawnFromBlock((World)world, (BlockPos)pos, (BlockState)blockState);
            } else {
                world.breakBlock(pos, true);
            }
        } else if (state != blockState) {
            world.setBlockState(pos, blockState, 3);
        }
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return ScaffoldingBlock.calculateDistance((BlockView)world, (BlockPos)pos) < 7;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context.isPlacement()) {
            return VoxelShapes.empty();
        }
        if (!context.isAbove(VoxelShapes.fullCube(), pos, true) || context.isDescending()) {
            if ((Integer)state.get((Property)DISTANCE) != 0 && ((Boolean)state.get((Property)BOTTOM)).booleanValue() && context.isAbove(OUTLINE_SHAPE, pos, true)) {
                return COLLISION_SHAPE;
            }
            return VoxelShapes.empty();
        }
        return NORMAL_OUTLINE_SHAPE;
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    private boolean shouldBeBottom(BlockView world, BlockPos pos, int distance) {
        return distance > 0 && !world.getBlockState(pos.down()).isOf((Block)this);
    }

    public static int calculateDistance(BlockView world, BlockPos pos) {
        Direction direction;
        BlockState blockState2;
        BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.DOWN);
        BlockState blockState = world.getBlockState((BlockPos)mutable);
        int i = 7;
        if (blockState.isOf(Blocks.SCAFFOLDING)) {
            i = (Integer)blockState.get((Property)DISTANCE);
        } else if (blockState.isSideSolidFullSquare(world, (BlockPos)mutable, Direction.UP)) {
            return 0;
        }
        Iterator iterator = Direction.Type.HORIZONTAL.iterator();
        while (iterator.hasNext() && (!(blockState2 = world.getBlockState((BlockPos)mutable.set((Vec3i)pos, direction = (Direction)iterator.next()))).isOf(Blocks.SCAFFOLDING) || (i = Math.min(i, (Integer)blockState2.get((Property)DISTANCE) + 1)) != 1)) {
        }
        return i;
    }
}

