/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCandleBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class AbstractCandleBlock
extends Block {
    public static final int field_30987 = 3;
    public static final BooleanProperty LIT = Properties.LIT;

    protected abstract MapCodec<? extends AbstractCandleBlock> getCodec();

    protected AbstractCandleBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract Iterable<Vec3d> getParticleOffsets(BlockState var1);

    public static boolean isLitCandle(BlockState state) {
        return state.contains((Property)LIT) && (state.isIn(BlockTags.CANDLES) || state.isIn(BlockTags.CANDLE_CAKES)) && (Boolean)state.get((Property)LIT) != false;
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient() && projectile.isOnFire() && this.isNotLit(state)) {
            AbstractCandleBlock.setLit((WorldAccess)world, (BlockState)state, (BlockPos)hit.getBlockPos(), (boolean)true);
        }
    }

    protected boolean isNotLit(BlockState state) {
        return (Boolean)state.get((Property)LIT) == false;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)LIT)).booleanValue()) {
            return;
        }
        this.getParticleOffsets(state).forEach(offset -> AbstractCandleBlock.spawnCandleParticles((World)world, (Vec3d)offset.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), (Random)random));
    }

    private static void spawnCandleParticles(World world, Vec3d vec3d, Random random) {
        float f = random.nextFloat();
        if (f < 0.3f) {
            world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
            if (f < 0.17f) {
                world.playSoundClient(vec3d.x + 0.5, vec3d.y + 0.5, vec3d.z + 0.5, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
            }
        }
        world.addParticleClient((ParticleEffect)ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
    }

    public static void extinguish(@Nullable PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos) {
        AbstractCandleBlock.setLit((WorldAccess)world, (BlockState)state, (BlockPos)pos, (boolean)false);
        if (state.getBlock() instanceof AbstractCandleBlock) {
            ((AbstractCandleBlock)state.getBlock()).getParticleOffsets(state).forEach(offset -> world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, (double)pos.getX() + offset.getX(), (double)pos.getY() + offset.getY(), (double)pos.getZ() + offset.getZ(), 0.0, (double)0.1f, 0.0));
        }
        world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
    }

    private static void setLit(WorldAccess world, BlockState state, BlockPos pos, boolean lit) {
        world.setBlockState(pos, (BlockState)state.with((Property)LIT, (Comparable)Boolean.valueOf(lit)), 11);
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && ((Boolean)state.get((Property)LIT)).booleanValue()) {
            AbstractCandleBlock.extinguish(null, (BlockState)state, (WorldAccess)world, (BlockPos)pos);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }
}

