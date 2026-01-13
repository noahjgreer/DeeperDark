/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

static interface TrialSpawnerState.ParticleEmitter {
    public static final TrialSpawnerState.ParticleEmitter NONE = (world, random, pos, ominous) -> {};
    public static final TrialSpawnerState.ParticleEmitter WAITING = (world, random, pos, ominous) -> {
        if (random.nextInt(2) == 0) {
            Vec3d vec3d = pos.toCenterPos().addRandom(random, 0.9f);
            TrialSpawnerState.ParticleEmitter.emitParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.SMALL_FLAME, vec3d, world);
        }
    };
    public static final TrialSpawnerState.ParticleEmitter ACTIVE = (world, random, pos, ominous) -> {
        Vec3d vec3d = pos.toCenterPos().addRandom(random, 1.0f);
        TrialSpawnerState.ParticleEmitter.emitParticle(ParticleTypes.SMOKE, vec3d, world);
        TrialSpawnerState.ParticleEmitter.emitParticle(ominous ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME, vec3d, world);
    };
    public static final TrialSpawnerState.ParticleEmitter COOLDOWN = (world, random, pos, ominous) -> {
        Vec3d vec3d = pos.toCenterPos().addRandom(random, 0.9f);
        if (random.nextInt(3) == 0) {
            TrialSpawnerState.ParticleEmitter.emitParticle(ParticleTypes.SMOKE, vec3d, world);
        }
        if (world.getTime() % 20L == 0L) {
            Vec3d vec3d2 = pos.toCenterPos().add(0.0, 0.5, 0.0);
            int i = world.getRandom().nextInt(4) + 20;
            for (int j = 0; j < i; ++j) {
                TrialSpawnerState.ParticleEmitter.emitParticle(ParticleTypes.SMOKE, vec3d2, world);
            }
        }
    };

    private static void emitParticle(SimpleParticleType type, Vec3d pos, World world) {
        world.addParticleClient(type, pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0, 0.0);
    }

    public void emit(World var1, Random var2, BlockPos var3, boolean var4);
}
