/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRedstoneGateBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.RepeaterBlock
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.RedstoneView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class RepeaterBlock
extends AbstractRedstoneGateBlock {
    public static final MapCodec<RepeaterBlock> CODEC = RepeaterBlock.createCodec(RepeaterBlock::new);
    public static final BooleanProperty LOCKED = Properties.LOCKED;
    public static final IntProperty DELAY = Properties.DELAY;

    public MapCodec<RepeaterBlock> getCodec() {
        return CODEC;
    }

    public RepeaterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)DELAY, (Comparable)Integer.valueOf(1))).with((Property)LOCKED, (Comparable)Boolean.valueOf(false))).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        world.setBlockState(pos, (BlockState)state.cycle((Property)DELAY), 3);
        return ActionResult.SUCCESS;
    }

    protected int getUpdateDelayInternal(BlockState state) {
        return (Integer)state.get((Property)DELAY) * 2;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = super.getPlacementState(ctx);
        return (BlockState)blockState.with((Property)LOCKED, (Comparable)Boolean.valueOf(this.isLocked((WorldView)ctx.getWorld(), ctx.getBlockPos(), blockState)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !this.canPlaceAbove(world, neighborPos, neighborState)) {
            return Blocks.AIR.getDefaultState();
        }
        if (!world.isClient() && direction.getAxis() != ((Direction)state.get((Property)FACING)).getAxis()) {
            return (BlockState)state.with((Property)LOCKED, (Comparable)Boolean.valueOf(this.isLocked(world, pos, state)));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean isLocked(WorldView world, BlockPos pos, BlockState state) {
        return this.getMaxInputLevelSides((RedstoneView)world, pos, state) > 0;
    }

    protected boolean getSideInputFromGatesOnly() {
        return true;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        Direction direction = (Direction)state.get((Property)FACING);
        double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)pos.getY() + 0.4 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        float g = -5.0f;
        if (random.nextBoolean()) {
            g = (Integer)state.get((Property)DELAY) * 2 - 1;
        }
        double h = (g /= 16.0f) * (float)direction.getOffsetX();
        double i = g * (float)direction.getOffsetZ();
        world.addParticleClient((ParticleEffect)DustParticleEffect.DEFAULT, d + h, e, f + i, 0.0, 0.0, 0.0);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, DELAY, LOCKED, POWERED});
    }
}

