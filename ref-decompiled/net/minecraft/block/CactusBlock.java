/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CactusBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
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

public class CactusBlock
extends Block {
    public static final MapCodec<CactusBlock> CODEC = CactusBlock.createCodec(CactusBlock::new);
    public static final IntProperty AGE = Properties.AGE_15;
    public static final int MAX_AGE = 15;
    private static final VoxelShape OUTLINE_SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);
    private static final VoxelShape COLLISION_SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)15.0);
    private static final int TALL_THRESHOLD = 3;
    private static final int FLOWER_GROWTH_AGE = 8;
    private static final double FLOWER_CHANCE_WHEN_SHORT = 0.1;
    private static final double FLOWER_CHANCE_WHEN_TALL = 0.25;

    public MapCodec<CactusBlock> getCodec() {
        return CODEC;
    }

    public CactusBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)AGE, (Comparable)Integer.valueOf(0)));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockPos blockPos = pos.up();
        if (!world.isAir(blockPos)) {
            return;
        }
        int i = 1;
        int j = (Integer)state.get((Property)AGE);
        while (world.getBlockState(pos.down(i)).isOf((Block)this)) {
            if (++i != 3 || j != 15) continue;
            return;
        }
        if (j == 8 && this.canPlaceAt(this.getDefaultState(), (WorldView)world, pos.up())) {
            double d;
            double d2 = d = i >= 3 ? 0.25 : 0.1;
            if (random.nextDouble() <= d) {
                world.setBlockState(blockPos, Blocks.CACTUS_FLOWER.getDefaultState());
            }
        } else if (j == 15 && i < 3) {
            world.setBlockState(blockPos, this.getDefaultState());
            BlockState blockState = (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(0));
            world.setBlockState(pos, blockState, 260);
            world.updateNeighbor(blockState, blockPos, (Block)this, null, false);
        }
        if (j < 15) {
            world.setBlockState(pos, (BlockState)state.with((Property)AGE, (Comparable)Integer.valueOf(j + 1)), 260);
        }
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockState blockState = world.getBlockState(pos.offset(direction));
            if (!blockState.isSolid() && !world.getFluidState(pos.offset(direction)).isIn(FluidTags.LAVA)) continue;
            return false;
        }
        BlockState blockState2 = world.getBlockState(pos.down());
        return (blockState2.isOf(Blocks.CACTUS) || blockState2.isIn(BlockTags.SAND)) && !world.getBlockState(pos.up()).isLiquid();
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        entity.serverDamage(world.getDamageSources().cactus(), 1.0f);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AGE});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

