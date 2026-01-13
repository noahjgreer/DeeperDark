/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BambooBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.enums.BambooLeaves
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

public class BambooBlock
extends Block
implements Fertilizable {
    public static final MapCodec<BambooBlock> CODEC = BambooBlock.createCodec(BambooBlock::new);
    private static final VoxelShape SMALL_LEAVES_SHAPE = Block.createColumnShape((double)6.0, (double)0.0, (double)16.0);
    private static final VoxelShape LARGE_LEAVES_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)16.0);
    private static final VoxelShape NO_LEAVES_SHAPE = Block.createColumnShape((double)3.0, (double)0.0, (double)16.0);
    public static final IntProperty AGE = Properties.AGE_1;
    public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
    public static final IntProperty STAGE = Properties.STAGE;
    public static final int field_31000 = 16;
    public static final int field_31001 = 0;
    public static final int field_31002 = 1;
    public static final int field_31003 = 0;
    public static final int field_31004 = 1;

    public MapCodec<BambooBlock> getCodec() {
        return CODEC;
    }

    public BambooBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0))).with((Property)LEAVES, (Comparable)BambooLeaves.NONE)).with((Property)STAGE, (Comparable)Integer.valueOf(0)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE, LEAVES, STAGE});
    }

    protected boolean isTransparent(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = state.get((Property)LEAVES) == BambooLeaves.LARGE ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
        return voxelShape.offset(state.getModelOffset(pos));
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return NO_LEAVES_SHAPE.offset(state.getModelOffset(pos));
    }

    protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        if (!fluidState.isEmpty()) {
            return null;
        }
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        if (blockState.isIn(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockState.isOf(Blocks.BAMBOO_SAPLING)) {
                return (BlockState)this.getDefaultState().with((Property)AGE, (Comparable)Integer.valueOf(0));
            }
            if (blockState.isOf(Blocks.BAMBOO)) {
                int i = (Integer)blockState.get((Property)AGE) > 0 ? 1 : 0;
                return (BlockState)this.getDefaultState().with((Property)AGE, (Comparable)Integer.valueOf(i));
            }
            BlockState blockState2 = ctx.getWorld().getBlockState(ctx.getBlockPos().up());
            if (blockState2.isOf(Blocks.BAMBOO)) {
                return (BlockState)this.getDefaultState().with((Property)AGE, (Comparable)((Integer)blockState2.get((Property)AGE)));
            }
            return Blocks.BAMBOO_SAPLING.getDefaultState();
        }
        return null;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Integer)state.get((Property)STAGE) == 0;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i;
        if ((Integer)state.get((Property)STAGE) != 0) {
            return;
        }
        if (random.nextInt(3) == 0 && world.isAir(pos.up()) && world.getBaseLightLevel(pos.up(), 0) >= 9 && (i = this.countBambooBelow((BlockView)world, pos) + 1) < 16) {
            this.updateLeaves(state, (World)world, pos, random, i);
        }
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        if (direction == Direction.UP && neighborState.isOf(Blocks.BAMBOO) && (Integer)neighborState.get((Property)AGE) > (Integer)state.get((Property)AGE)) {
            return (BlockState)state.cycle((Property)AGE);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        int j;
        int i = this.countBambooAbove((BlockView)world, pos);
        return i + (j = this.countBambooBelow((BlockView)world, pos)) + 1 < 16 && (Integer)world.getBlockState(pos.up(i)).get((Property)STAGE) != 1;
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int i = this.countBambooAbove((BlockView)world, pos);
        int j = this.countBambooBelow((BlockView)world, pos);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);
        for (int m = 0; m < l; ++m) {
            BlockPos blockPos = pos.up(i);
            BlockState blockState = world.getBlockState(blockPos);
            if (k >= 16 || (Integer)blockState.get((Property)STAGE) == 1 || !world.isAir(blockPos.up())) {
                return;
            }
            this.updateLeaves(blockState, (World)world, blockPos, random, k);
            ++i;
            ++k;
        }
    }

    protected void updateLeaves(BlockState state, World world, BlockPos pos, Random random, int height) {
        BlockState blockState = world.getBlockState(pos.down());
        BlockPos blockPos = pos.down(2);
        BlockState blockState2 = world.getBlockState(blockPos);
        BambooLeaves bambooLeaves = BambooLeaves.NONE;
        if (height >= 1) {
            if (!blockState.isOf(Blocks.BAMBOO) || blockState.get((Property)LEAVES) == BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.SMALL;
            } else if (blockState.isOf(Blocks.BAMBOO) && blockState.get((Property)LEAVES) != BambooLeaves.NONE) {
                bambooLeaves = BambooLeaves.LARGE;
                if (blockState2.isOf(Blocks.BAMBOO)) {
                    world.setBlockState(pos.down(), (BlockState)blockState.with((Property)LEAVES, (Comparable)BambooLeaves.SMALL), 3);
                    world.setBlockState(blockPos, (BlockState)blockState2.with((Property)LEAVES, (Comparable)BambooLeaves.NONE), 3);
                }
            }
        }
        int i = (Integer)state.get((Property)AGE) == 1 || blockState2.isOf(Blocks.BAMBOO) ? 1 : 0;
        int j = height >= 11 && random.nextFloat() < 0.25f || height == 15 ? 1 : 0;
        world.setBlockState(pos.up(), (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)AGE, (Comparable)Integer.valueOf(i))).with((Property)LEAVES, (Comparable)bambooLeaves)).with((Property)STAGE, (Comparable)Integer.valueOf(j)), 3);
    }

    protected int countBambooAbove(BlockView world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.up(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }

    protected int countBambooBelow(BlockView world, BlockPos pos) {
        int i;
        for (i = 0; i < 16 && world.getBlockState(pos.down(i + 1)).isOf(Blocks.BAMBOO); ++i) {
        }
        return i;
    }
}

