/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.TargetBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/*
 * Exception performing whole class analysis ignored.
 */
public class TargetBlock
extends Block {
    public static final MapCodec<TargetBlock> CODEC = TargetBlock.createCodec(TargetBlock::new);
    private static final IntProperty POWER = Properties.POWER;
    private static final int RECOVERABLE_POWER_DELAY = 20;
    private static final int REGULAR_POWER_DELAY = 8;

    public MapCodec<TargetBlock> getCodec() {
        return CODEC;
    }

    public TargetBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWER, (Comparable)Integer.valueOf(0)));
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        int i = TargetBlock.trigger((WorldAccess)world, (BlockState)state, (BlockHitResult)hit, (Entity)projectile);
        Entity entity = projectile.getOwner();
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            serverPlayerEntity.incrementStat(Stats.TARGET_HIT);
            Criteria.TARGET_HIT.trigger(serverPlayerEntity, (Entity)projectile, hit.getPos(), i);
        }
    }

    private static int trigger(WorldAccess world, BlockState state, BlockHitResult hitResult, Entity entity) {
        int j;
        int i = TargetBlock.calculatePower((BlockHitResult)hitResult, (Vec3d)hitResult.getPos());
        int n = j = entity instanceof PersistentProjectileEntity ? 20 : 8;
        if (!world.getBlockTickScheduler().isQueued(hitResult.getBlockPos(), (Object)state.getBlock())) {
            TargetBlock.setPower((WorldAccess)world, (BlockState)state, (int)i, (BlockPos)hitResult.getBlockPos(), (int)j);
        }
        return i;
    }

    private static int calculatePower(BlockHitResult hitResult, Vec3d pos) {
        Direction direction = hitResult.getSide();
        double d = Math.abs(MathHelper.fractionalPart((double)pos.x) - 0.5);
        double e = Math.abs(MathHelper.fractionalPart((double)pos.y) - 0.5);
        double f = Math.abs(MathHelper.fractionalPart((double)pos.z) - 0.5);
        Direction.Axis axis = direction.getAxis();
        double g = axis == Direction.Axis.Y ? Math.max(d, f) : (axis == Direction.Axis.Z ? Math.max(d, e) : Math.max(e, f));
        return Math.max(1, MathHelper.ceil((double)(15.0 * MathHelper.clamp((double)((0.5 - g) / 0.5), (double)0.0, (double)1.0))));
    }

    private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlockState(pos, (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(power)), 3);
        world.scheduleBlockTick(pos, state.getBlock(), delay);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Integer)state.get((Property)POWER) != 0) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(0)), 3);
        }
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)POWER);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWER});
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient() || state.isOf(oldState.getBlock())) {
            return;
        }
        if ((Integer)state.get((Property)POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            world.setBlockState(pos, (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(0)), 18);
        }
    }
}

