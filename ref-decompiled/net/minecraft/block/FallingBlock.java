/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Falling
 *  net.minecraft.block.FallingBlock
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.particle.BlockStateParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.ParticleUtil
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.block.Falling;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class FallingBlock
extends Block
implements Falling {
    public FallingBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract MapCodec<? extends FallingBlock> getCodec();

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, (Block)this, this.getFallDelay());
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        tickView.scheduleBlockTick(pos, (Block)this, this.getFallDelay());
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!FallingBlock.canFallThrough((BlockState)world.getBlockState(pos.down())) || pos.getY() < world.getBottomY()) {
            return;
        }
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock((World)world, (BlockPos)pos, (BlockState)state);
        this.configureFallingBlockEntity(fallingBlockEntity);
    }

    protected void configureFallingBlockEntity(FallingBlockEntity entity) {
    }

    protected int getFallDelay() {
        return 2;
    }

    public static boolean canFallThrough(BlockState state) {
        return state.isAir() || state.isIn(BlockTags.FIRE) || state.isLiquid() || state.isReplaceable();
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockPos blockPos;
        if (random.nextInt(16) == 0 && FallingBlock.canFallThrough((BlockState)world.getBlockState(blockPos = pos.down()))) {
            ParticleUtil.spawnParticle((World)world, (BlockPos)pos, (Random)random, (ParticleEffect)new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state));
        }
    }

    public abstract int getColor(BlockState var1, BlockView var2, BlockPos var3);
}

