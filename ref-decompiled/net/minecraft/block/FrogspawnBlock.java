/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FrogspawnBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.passive.TadpoleEntity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class FrogspawnBlock
extends Block {
    public static final MapCodec<FrogspawnBlock> CODEC = FrogspawnBlock.createCodec(FrogspawnBlock::new);
    private static final int MIN_TADPOLES = 2;
    private static final int MAX_TADPOLES = 5;
    private static final int MIN_HATCH_TIME = 3600;
    private static final int MAX_HATCH_TIME = 12000;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)1.5);
    private static int minHatchTime = 3600;
    private static int maxHatchTime = 12000;

    public MapCodec<FrogspawnBlock> getCodec() {
        return CODEC;
    }

    public FrogspawnBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return FrogspawnBlock.canLayAt((BlockView)world, (BlockPos)pos.down());
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, (Block)this, FrogspawnBlock.getHatchTime((Random)world.getRandom()));
    }

    private static int getHatchTime(Random random) {
        return random.nextBetweenExclusive(minHatchTime, maxHatchTime);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canPlaceAt(state, (WorldView)world, pos)) {
            this.breakWithoutDrop((World)world, pos);
            return;
        }
        this.hatch(world, pos, random);
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (entity.getType().equals(EntityType.FALLING_BLOCK)) {
            this.breakWithoutDrop(world, pos);
        }
    }

    private static boolean canLayAt(BlockView world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        FluidState fluidState2 = world.getFluidState(pos.up());
        return fluidState.getFluid() == Fluids.WATER && fluidState2.getFluid() == Fluids.EMPTY;
    }

    private void hatch(ServerWorld world, BlockPos pos, Random random) {
        this.breakWithoutDrop((World)world, pos);
        world.playSound(null, pos, SoundEvents.BLOCK_FROGSPAWN_HATCH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        this.spawnTadpoles(world, pos, random);
    }

    private void breakWithoutDrop(World world, BlockPos pos) {
        world.breakBlock(pos, false);
    }

    private void spawnTadpoles(ServerWorld world, BlockPos pos, Random random) {
        int i = random.nextBetweenExclusive(2, 6);
        for (int j = 1; j <= i; ++j) {
            TadpoleEntity tadpoleEntity = (TadpoleEntity)EntityType.TADPOLE.create((World)world, SpawnReason.BREEDING);
            if (tadpoleEntity == null) continue;
            double d = (double)pos.getX() + this.getSpawnOffset(random);
            double e = (double)pos.getZ() + this.getSpawnOffset(random);
            int k = random.nextBetweenExclusive(1, 361);
            tadpoleEntity.refreshPositionAndAngles(d, (double)pos.getY() - 0.5, e, (float)k, 0.0f);
            tadpoleEntity.setPersistent();
            world.spawnEntity((Entity)tadpoleEntity);
        }
    }

    private double getSpawnOffset(Random random) {
        double d = 0.2f;
        return MathHelper.clamp((double)random.nextDouble(), (double)0.2f, (double)0.7999999970197678);
    }

    @VisibleForTesting
    public static void setHatchTimeRange(int min, int max) {
        minHatchTime = min;
        maxHatchTime = max;
    }

    @VisibleForTesting
    public static void resetHatchTimeRange() {
        minHatchTime = 3600;
        maxHatchTime = 12000;
    }
}

