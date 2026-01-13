/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class BlockDustParticle.DustPillarFactory
implements ParticleFactory<BlockStateParticleEffect> {
    @Override
    public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockDustParticle particle = BlockDustParticle.create(blockStateParticleEffect, clientWorld, d, e, f, g, h, i);
        if (particle != null) {
            particle.setVelocity(random.nextGaussian() / 30.0, h + random.nextGaussian() / 2.0, random.nextGaussian() / 30.0);
            particle.setMaxAge(random.nextInt(20) + 20);
        }
        return particle;
    }
}
