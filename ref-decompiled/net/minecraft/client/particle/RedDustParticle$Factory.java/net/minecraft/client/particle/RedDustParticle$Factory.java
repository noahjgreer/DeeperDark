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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class RedDustParticle.Factory
implements ParticleFactory<DustParticleEffect> {
    private final SpriteProvider spriteProvider;

    public RedDustParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(DustParticleEffect dustParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        return new RedDustParticle(clientWorld, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
    }
}
