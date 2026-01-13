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
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class DustColorTransitionParticle.Factory
implements ParticleFactory<DustColorTransitionParticleEffect> {
    private final SpriteProvider spriteProvider;

    public DustColorTransitionParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(DustColorTransitionParticleEffect dustColorTransitionParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        return new DustColorTransitionParticle(clientWorld, d, e, f, g, h, i, dustColorTransitionParticleEffect, this.spriteProvider);
    }
}
