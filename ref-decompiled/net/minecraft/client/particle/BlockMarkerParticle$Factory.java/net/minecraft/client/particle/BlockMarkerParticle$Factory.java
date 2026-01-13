/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BlockMarkerParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class BlockMarkerParticle.Factory
implements ParticleFactory<BlockStateParticleEffect> {
    @Override
    public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        return new BlockMarkerParticle(clientWorld, d, e, f, blockStateParticleEffect.getBlockState());
    }
}
