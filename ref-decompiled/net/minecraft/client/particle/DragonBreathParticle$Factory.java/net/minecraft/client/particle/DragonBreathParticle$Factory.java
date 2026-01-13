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
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DragonBreathParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class DragonBreathParticle.Factory
implements ParticleFactory<DragonBreathParticleEffect> {
    private final SpriteProvider spriteProvider;

    public DragonBreathParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(DragonBreathParticleEffect dragonBreathParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        DragonBreathParticle dragonBreathParticle = new DragonBreathParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        dragonBreathParticle.move(dragonBreathParticleEffect.getPower());
        return dragonBreathParticle;
    }
}
