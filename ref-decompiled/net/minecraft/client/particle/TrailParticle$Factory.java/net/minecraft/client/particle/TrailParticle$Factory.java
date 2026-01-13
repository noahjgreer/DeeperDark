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
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.TrailParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class TrailParticle.Factory
implements ParticleFactory<TrailParticleEffect> {
    private final SpriteProvider spriteProvider;

    public TrailParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(TrailParticleEffect trailParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        TrailParticle trailParticle = new TrailParticle(clientWorld, d, e, f, g, h, i, trailParticleEffect.target(), trailParticleEffect.color(), this.spriteProvider.getSprite(random));
        trailParticle.setMaxAge(trailParticleEffect.duration());
        return trailParticle;
    }
}
