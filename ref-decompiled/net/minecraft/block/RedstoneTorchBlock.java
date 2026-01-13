/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractTorchBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.RedstoneTorchBlock
 *  net.minecraft.block.RedstoneTorchBlock$BurnoutEntry
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class RedstoneTorchBlock
extends AbstractTorchBlock {
    public static final MapCodec<RedstoneTorchBlock> CODEC = RedstoneTorchBlock.createCodec(RedstoneTorchBlock::new);
    public static final BooleanProperty LIT = Properties.LIT;
    private static final Map<BlockView, List<BurnoutEntry>> BURNOUT_MAP = new WeakHashMap();
    public static final int field_31227 = 60;
    public static final int field_31228 = 8;
    public static final int field_31229 = 160;
    private static final int SCHEDULED_TICK_DELAY = 2;

    public MapCodec<? extends RedstoneTorchBlock> getCodec() {
        return CODEC;
    }

    public RedstoneTorchBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LIT, (Comparable)Boolean.valueOf(true)));
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        this.update(world, pos, state);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        WireOrientation wireOrientation = this.getEmissionOrientation(world, state);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), (Block)this, OrientationHelper.withFrontNullable((WireOrientation)wireOrientation, (Direction)direction));
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (!moved) {
            this.update((World)world, pos, state);
        }
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)LIT)).booleanValue() && Direction.UP != direction) {
            return 15;
        }
        return 0;
    }

    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = this.shouldUnpower((World)world, pos, state);
        List list = (List)BURNOUT_MAP.get(world);
        while (list != null && !list.isEmpty() && world.getTime() - ((BurnoutEntry)list.get((int)0)).time > 60L) {
            list.remove(0);
        }
        if (((Boolean)state.get((Property)LIT)).booleanValue()) {
            if (bl) {
                world.setBlockState(pos, (BlockState)state.with((Property)LIT, (Comparable)Boolean.valueOf(false)), 3);
                if (RedstoneTorchBlock.isBurnedOut((World)world, (BlockPos)pos, (boolean)true)) {
                    world.syncWorldEvent(1502, pos, 0);
                    world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!bl && !RedstoneTorchBlock.isBurnedOut((World)world, (BlockPos)pos, (boolean)false)) {
            world.setBlockState(pos, (BlockState)state.with((Property)LIT, (Comparable)Boolean.valueOf(true)), 3);
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (((Boolean)state.get((Property)LIT)).booleanValue() == this.shouldUnpower(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, (Object)this)) {
            world.scheduleBlockTick(pos, (Block)this, 2);
        }
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction == Direction.DOWN) {
            return state.getWeakRedstonePower(world, pos, direction);
        }
        return 0;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)LIT)).booleanValue()) {
            return;
        }
        double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        double e = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
        double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
        world.addParticleClient((ParticleEffect)DustParticleEffect.DEFAULT, d, e, f, 0.0, 0.0, 0.0);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }

    private static boolean isBurnedOut(World world, BlockPos pos, boolean addNew) {
        List list = BURNOUT_MAP.computeIfAbsent(world, worldx -> Lists.newArrayList());
        if (addNew) {
            list.add(new BurnoutEntry(pos.toImmutable(), world.getTime()));
        }
        int i = 0;
        for (BurnoutEntry burnoutEntry : list) {
            if (!burnoutEntry.pos.equals((Object)pos) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    protected @Nullable WireOrientation getEmissionOrientation(World world, BlockState state) {
        return OrientationHelper.getEmissionOrientation((World)world, null, (Direction)Direction.UP);
    }
}

