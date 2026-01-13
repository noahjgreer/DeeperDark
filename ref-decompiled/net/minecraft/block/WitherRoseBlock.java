/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FlowerBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WitherRoseBlock
 *  net.minecraft.component.type.SuspiciousStewEffectsComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class WitherRoseBlock
extends FlowerBlock {
    public static final MapCodec<WitherRoseBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)STEW_EFFECT_CODEC.forGetter(FlowerBlock::getStewEffects), (App)WitherRoseBlock.createSettingsCodec()).apply((Applicative)instance, WitherRoseBlock::new));

    public MapCodec<WitherRoseBlock> getCodec() {
        return CODEC;
    }

    public WitherRoseBlock(RegistryEntry<StatusEffect> registryEntry, float f, AbstractBlock.Settings settings) {
        this(WitherRoseBlock.createStewEffectList(registryEntry, (float)f), settings);
    }

    public WitherRoseBlock(SuspiciousStewEffectsComponent suspiciousStewEffectsComponent, AbstractBlock.Settings settings) {
        super(suspiciousStewEffectsComponent, settings);
    }

    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.NETHERRACK) || floor.isOf(Blocks.SOUL_SAND) || floor.isOf(Blocks.SOUL_SOIL);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        VoxelShape voxelShape = this.getOutlineShape(state, (BlockView)world, pos, ShapeContext.absent());
        Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
        double d = (double)pos.getX() + vec3d.x;
        double e = (double)pos.getZ() + vec3d.z;
        for (int i = 0; i < 3; ++i) {
            if (!random.nextBoolean()) continue;
            world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d + random.nextDouble() / 5.0, (double)pos.getY() + (0.5 - random.nextDouble()), e + random.nextDouble() / 5.0, 0.0, 0.0, 0.0);
        }
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world instanceof ServerWorld) {
            LivingEntity livingEntity;
            ServerWorld serverWorld = (ServerWorld)world;
            if (world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof LivingEntity && !(livingEntity = (LivingEntity)entity).isInvulnerableTo(serverWorld, world.getDamageSources().wither())) {
                livingEntity.addStatusEffect(this.getContactEffect());
            }
        }
    }

    public StatusEffectInstance getContactEffect() {
        return new StatusEffectInstance(StatusEffects.WITHER, 40);
    }
}

