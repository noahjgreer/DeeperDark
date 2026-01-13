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
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class ShriekParticle.Factory
implements ParticleFactory<ShriekParticleEffect> {
    private final SpriteProvider spriteProvider;

    public ShriekParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        ShriekParticle shriekParticle = new ShriekParticle(clientWorld, d, e, f, shriekParticleEffect.getDelay(), this.spriteProvider.getSprite(random));
        shriekParticle.setAlpha(1.0f);
        return shriekParticle;
    }
}
